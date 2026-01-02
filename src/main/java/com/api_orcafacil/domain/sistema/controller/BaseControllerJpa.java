package com.api_orcafacil.domain.sistema.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.domain.sistema.model.GenericSpecification;

import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.domain.Sort;

public abstract class BaseControllerJpa<T, ID> {

    protected final JpaRepository<T, ID> repository;

    public BaseControllerJpa(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    private Class<?> getEntityClass() {
        return (Class<?>) ((java.lang.reflect.ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    /**
     * Método opcional — controllers NÃO são obrigados a sobrescrever.
     */
    protected List<String> getSearchableFields() {
        Class<?> entityClass = getEntityClass();

        List<String> fields = new ArrayList<>();

        Arrays.stream(entityClass.getDeclaredFields())
                .map(Field::getName)
                .forEach(fields::add);

        return fields;
    }

    @GetMapping("/listar")
    public Page<T> listar(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam Map<String, String> params) {

        Map<String, String> columnFilters = new HashMap<>(params);
        columnFilters.remove("page");
        columnFilters.remove("size");
        columnFilters.remove("sort");
        columnFilters.remove("search");

        boolean temCamposPesquisa = !getSearchableFields().isEmpty();

        if (!temCamposPesquisa && columnFilters.isEmpty()) {
            return repository.findAll(pageable);
        }

        if (repository instanceof JpaSpecificationExecutor) {
            @SuppressWarnings("unchecked")
            JpaSpecificationExecutor<T> specRepo = (JpaSpecificationExecutor<T>) repository;

            var spec = new GenericSpecification<T>(search, columnFilters, getSearchableFields());
            return specRepo.findAll(spec, pageable);
        }

        return repository.findAll(pageable);
    }

    @GetMapping("/")
    public ResponseEntity<List<T>> obterTodos() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<T>> obterPorId(@PathVariable ID id) {
        return ResponseEntity.ok(repository.findById(id));
    }

    @PostMapping("/")
    public ResponseEntity<?> cadastrar(@RequestBody T objeto) throws Exception {
        repository.save(objeto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registro salvo com sucesso"));
    }

    @PutMapping("/")
    public ResponseEntity<T> atualizar(@RequestBody T objeto) throws Exception {
        return ResponseEntity.ok(repository.save(objeto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id) throws Exception {
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Registro deletado com sucesso"));
    }

}