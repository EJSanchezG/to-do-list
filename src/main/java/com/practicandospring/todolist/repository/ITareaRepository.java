package com.practicandospring.todolist.repository;

import com.practicandospring.todolist.model.TareaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITareaRepository extends JpaRepository<TareaEntity, Long> {
    // Spring Data JPA crea la consulta automáticamente basándose en el nombre del método
    List<TareaEntity> findByCategoriaNombreIgnoreCase(String nombre);

    // Spring generará el conteo automáticamente
    long countByCategoriaId(Long categoriaId);
}