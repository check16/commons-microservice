package com.formacionbdi.microservicios.commons.controllers;

import com.formacionbdi.microservicios.commons.services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommonController<E, S extends CommonService<E>> {

    @Autowired
    protected S service;

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/pageable")
    public ResponseEntity<?> listar(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> ver(@PathVariable Long id) {
        Optional<E> optional = service.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound()
                                 .build();
        }
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody E entity, BindingResult result) {
        if (result.hasErrors()) {
            return this.validar(result);
        }
        E entityDB = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(entityDB);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent()
                             .build();
    }

    protected ResponseEntity<?> validar(BindingResult result) {
        Map<String, Object> errores = new HashMap<>();
        result.getFieldErrors()
              .forEach(err -> {
                  errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
              });

        return ResponseEntity.badRequest()
                             .body(errores);
    }
}
