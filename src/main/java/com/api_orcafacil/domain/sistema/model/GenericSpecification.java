package com.api_orcafacil.domain.sistema.model;

import jakarta.persistence.Transient;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class GenericSpecification<T> implements Specification<T> {

    private final String search;
    private final Map<String, String> columnFilters;
    private final List<String> searchableFields;

    public GenericSpecification(
            String search,
            Map<String, String> columnFilters,
            List<String> searchableFields) {

        this.search = search;
        this.columnFilters = (columnFilters != null ? columnFilters : Map.of());
        this.searchableFields = (searchableFields != null ? searchableFields : List.of());
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        Class<?> entityClass = root.getJavaType();
        Map<String, String> transientMap = mapTransientGetters(entityClass);

        List<Predicate> predicates = new ArrayList<>();

        // filtros por coluna
        for (var entry : columnFilters.entrySet()) {
            String field = normalize(entry.getKey());
            String value = normalize(entry.getValue());

            field = transientMap.getOrDefault(field, field);

            if (!isValid(field, value)) {
                continue;
            }

            Path<?> path = resolvePath(root, field);
            Predicate predicate = createPredicate(cb, path, path.getJavaType(), value);

            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        // filtro global
        if (isValid(search)) {
            String term = "%" + search.toLowerCase() + "%";
            List<Predicate> globalPreds = new ArrayList<>();

            for (String field : searchableFields) {

                field = normalize(transientMap.getOrDefault(field, field));

                Path<?> path = resolvePath(root, field);

                if (String.class.isAssignableFrom(path.getJavaType())) {
                    globalPreds.add(
                            cb.like(cb.lower(path.as(String.class)), term)
                    );
                }
            }

            if (!globalPreds.isEmpty()) {
                predicates.add(cb.or(globalPreds.toArray(new Predicate[0])));
            }
        }

        boolean usuarioAplicouFiltro =
                isValid(search) || !columnFilters.isEmpty();

        if (predicates.isEmpty()) {
            return usuarioAplicouFiltro
                    ? cb.disjunction()
                    : cb.conjunction();
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    // ================= SUPORTE =================

    private boolean isValid(String s) {
        return s != null && !s.isBlank();
    }

    private boolean isValid(String field, String value) {
        return isValid(field) && isValid(value);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private Path<?> resolvePath(From<?, ?> root, String field) {
        try {
            if (!field.contains(".")) {
                return root.get(field);
            }

            String[] parts = field.split("\\.");
            From<?, ?> join = root;

            for (int i = 0; i < parts.length - 1; i++) {
                join = join.join(parts[i], JoinType.LEFT);
            }

            return join.get(parts[parts.length - 1]);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Campo inválido para filtro: " + field, e);
        }
    }

    private Predicate createPredicate(
            CriteriaBuilder cb,
            Path<?> path,
            Class<?> type,
            String value) {

        // String
        if (String.class.isAssignableFrom(type)) {
            return cb.like(
                    cb.lower(path.as(String.class)),
                    "%" + value.toLowerCase() + "%"
            );
        }

        // Long
        if (type.equals(Long.class)) {
            return cb.equal(path, Long.valueOf(value));
        }

        // Integer
        if (type.equals(Integer.class)) {
            return cb.equal(path, Integer.valueOf(value));
        }

        // Double / BigDecimal
        if (Number.class.isAssignableFrom(type)) {
            return cb.equal(path, Double.valueOf(value));
        }

        // Boolean
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return cb.equal(path, Boolean.valueOf(value));
        }

        // LocalDate
        if (type.equals(LocalDate.class)) {
            return cb.equal(path, LocalDate.parse(value));
        }

        // LocalDateTime
        if (type.equals(LocalDateTime.class)) {
            return cb.equal(path, LocalDateTime.parse(value));
        }

        // Enum
        if (type.isEnum()) {
            Object enumValue = Enum.valueOf((Class<Enum>) type, value.toUpperCase());
            return cb.equal(path, enumValue);
        }

        return null;
    }

    // ================= TRANSIENT =================

    private Map<String, String> mapTransientGetters(Class<?> entityClass) {
        Map<String, String> result = new HashMap<>();

        for (Method method : entityClass.getMethods()) {
            if (method.isAnnotationPresent(Transient.class)
                    && method.getName().startsWith("get")) {

                String transientName = lowerCamelFromGetter(method.getName());
                result.put(transientName, inferFieldPath(method));
            }
        }

        return result;
    }

    private String lowerCamelFromGetter(String getter) {
        String base = getter.substring(3);
        return Character.toLowerCase(base.charAt(0)) + base.substring(1);
    }

    private String inferFieldPath(Method method) {
        String name = lowerCamelFromGetter(method.getName());

        if (name.contains("_")) {
            String[] parts = name.split("_", 2);
            return parts[1] + "." + name;
        }

        return name;
    }
}
