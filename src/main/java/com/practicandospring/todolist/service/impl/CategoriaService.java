package com.practicandospring.todolist.service.impl;

import com.practicandospring.todolist.entity.CategoriaEntity;
import com.practicandospring.todolist.repository.ICategoriaRepository;
import com.practicandospring.todolist.repository.ITareaRepository;
import com.practicandospring.todolist.service.ICategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService implements ICategoriaService {
    private final ICategoriaRepository categoriaRepository;
    private final ITareaRepository tareaRepository; // Inyecta el repo de tareas

    public CategoriaService(ICategoriaRepository categoriaRepository, ITareaRepository tareaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.tareaRepository = tareaRepository;
    }

    //GUARDAR
    public CategoriaEntity guardar(CategoriaEntity categoria) {
        // Validación: No permitir nombres duplicados (ignorando mayúsculas/minúsculas)
        categoriaRepository.findByNombreIgnoreCase(categoria.getNombre())
                .ifPresent(c -> {
                    throw new RuntimeException("La categoría '" + categoria.getNombre() + "' ya existe.");
                });

        return categoriaRepository.save(categoria);
    }

    //LISTAR
    public List<CategoriaEntity> listarTodas() {
        return categoriaRepository.findAll();
    }

    //BUSCAR POR NOMBRE
    public CategoriaEntity buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Categoría '" + nombre + "' no encontrada."));
    }

    // ACTUALIZAR
    public CategoriaEntity actualizar(Long id, CategoriaEntity nuevaData) {
        CategoriaEntity existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Validación extra: Si está en uso, no permite el cambio
        if (tareaRepository.countByCategoriaId(id) > 0) {
            throw new RuntimeException("No se puede actualizar una categoría que ya tiene tareas asociadas.");
        }

        existente.setNombre(nuevaData.getNombre());
        return categoriaRepository.save(existente);
    }

    // ELIMINAR POR ID
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada");
        }

        if (tareaRepository.countByCategoriaId(id) > 0) {
            throw new RuntimeException("No se puede eliminar la categoría porque está siendo usada en tareas.");
        }

        categoriaRepository.deleteById(id);
    }
}
