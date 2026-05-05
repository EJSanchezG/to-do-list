package com.practicandospring.todolist.controller;

import com.practicandospring.todolist.dto.TareaDTO;
import com.practicandospring.todolist.service.ITareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final ITareaService tareaService;

    public TareaController(ITareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public ResponseEntity<List<TareaDTO>> listar(@RequestParam(required = false) String categoria) {
        List<TareaDTO> tareas;

        if (categoria != null && !categoria.isBlank()) {
            // Filtra por categoría en la base de datos
            tareas = tareaService.listarPorCategoria(categoria);
        } else {
            // Trae todas las tareas de la base de datos
            tareas = tareaService.listarTodas();
        }

        return ResponseEntity.ok(tareas);
    }

    @PostMapping
    public ResponseEntity<TareaDTO> crear(@RequestBody TareaDTO dto) {
        // Al crear, Spring Data JPA generará el ID y guardará la relación
        TareaDTO nuevaTarea = tareaService.crearTarea(dto);
        return new ResponseEntity<>(nuevaTarea, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaDTO> actualizar(@PathVariable Long id, @RequestBody TareaDTO dto) {
        return ResponseEntity.ok(tareaService.actualizarTarea(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tareaService.eliminarTarea(id);
        return ResponseEntity.noContent().build();
    }
}