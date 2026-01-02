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

    public GenericSpecification(String search,
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

        // aplicar filtros por coluna
        for (var entry : columnFilters.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();

            // converte automÃ¡tico se for transient
            field = transientMap.getOrDefault(field, field);

            if (!isValid(field, value))
                continue;

            try {
                Path<?> path = resolvePath(root, field);
                Class<?> type = path.getJavaType();

                predicates.add(createPredicate(cb, path, type, value));
            } catch (Exception ignored) {
            }
        }

        // filtro global
        if (isValid(search)) {
            String term = "%" + search.toLowerCase() + "%";
            List<Predicate> globalPreds = new ArrayList<>();

            for (String field : searchableFields) {

                field = transientMap.getOrDefault(field, field);

                try {
                    Path<?> path = resolvePath(root, field);
                    if (path.getJavaType().equals(String.class)) {
                        globalPreds.add(cb.like(cb.lower(path.as(String.class)), term));
                    }

                } catch (Exception ignored) {
                }
            }

            if (!globalPreds.isEmpty()) {
                predicates.add(cb.or(globalPreds.toArray(new Predicate[0])));
            }
        }

        boolean usuarioAplicouFiltro = (search != null && !search.isBlank()) ||
                !columnFilters.isEmpty();

        if (predicates.isEmpty()) {
            return usuarioAplicouFiltro
                    ? cb.disjunction() // forÃ§a retornar 0 resultados
                    : cb.conjunction(); // sem filtro = retorna tudo
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    // metodos de suporte

    private boolean isValid(String s) {
        return s != null && !s.isBlank();
    }

    private boolean isValid(String field, String value) {
        return isValid(field) && isValid(value);
    }

    private Path<?> resolvePath(From<?, ?> root, String field) {
        if (!field.contains("."))
            return root.get(field);
        String[] parts = field.split("\\.");
        From<?, ?> join = root;
        for (int i = 0; i < parts.length - 1; i++) {
            join = join.join(parts[i], JoinType.LEFT);
        }
        return join.get(parts[parts.length - 1]);
    }

    private Predicate createPredicate(CriteriaBuilder cb, Path<?> path, Class<?> type, String value) {

        // String â†’ LIKE
        if (type.equals(String.class)) {
            return cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%");
        }

        // NÃºmero â†’ igualdade
        if (Number.class.isAssignableFrom(type)) {
            try {
                // aqui vocÃª poderia fazer um cast por tipo (Integer, Long, etc.)
                Number num = Double.valueOf(value);
                return cb.equal(path, num);
            } catch (Exception ignored) {
                // se o valor não é nÃºmero vÃ¡lido â†’ filtro impossivel â†’ nenhum registro
                return cb.disjunction();
            }
        }

        // LocalDate
        if (type.equals(LocalDate.class)) {
            try {
                LocalDate d = LocalDate.parse(value);
                return cb.equal(path, d);
            } catch (Exception ignored) {
                return cb.disjunction();
            }
        }

        // LocalDateTime
        if (type.equals(LocalDateTime.class)) {
            try {
                LocalDateTime dt = LocalDateTime.parse(value);
                return cb.equal(path, dt);
            } catch (Exception ignored) {
                return cb.disjunction();
            }
        }

        // Boolean
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                return cb.disjunction();
            }
            return cb.equal(path, Boolean.valueOf(value));
        }

        // Enum
        if (type.isEnum()) {
            try {
                Object enumValue = Enum.valueOf((Class<Enum>) type, value.toUpperCase());
                return cb.equal(path, enumValue);
            } catch (Exception ignored) {
                // fallback: tentar LIKE no nome do enum (se estiver mapeado como STRING)
                return cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%");
            }
        }

        // Tipo estranho â†’ não sabemos filtrar â†’ nenhum resultado
        return cb.disjunction();
    }

    // mapeia getters @Transient â†’ caminho real
    private Map<String, String> mapTransientGetters(Class<?> entityClass) {
        Map<String, String> result = new HashMap<>();

        for (Method method : entityClass.getMethods()) {
            if (method.isAnnotationPresent(Transient.class) &&
                    method.getName().startsWith("get")) {

                String transientName = lowerCamelFromGetter(method.getName());
                String realPath = inferFieldPath(method);

                result.put(transientName, realPath);
            }
        }

        return result;
    }

    private String lowerCamelFromGetter(String getter) {
        String base = getter.substring(3);
        return Character.toLowerCase(base.charAt(0)) + base.substring(1);
    }

    // private String inferFieldPath(Method method) {
    // // getNmProjetista -> projetista.nm_projetista
    // String name = lowerCamelFromGetter(method.getName());
    // if (name.contains("_")) {
    // String[] parts = name.split("_");
    // return parts[1] + "." + name.substring(parts[1].length() + 1);
    // }
    // return name;
    // }

    private String inferFieldPath(Method method) {
        String name = lowerCamelFromGetter(method.getName()); // ex: nm_projetista

        // trata padrÃ£o de nomes com underline
        if (name.contains("_")) {
            String[] parts = name.split("_", 2);
            String rel = parts[1]; // projetista
            return rel + "." + name; // projetista.nm_projetista
        }

        return name; // Ex: nome
    }
}