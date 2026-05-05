package com.practicandospring.todolist.service;

import com.practicandospring.todolist.model.CategoriaEntity;

import java.util.List;

public interface ICategoriaService {
    CategoriaEntity guardar(CategoriaEntity categoria);
    List<CategoriaEntity> listarTodas();
    CategoriaEntity buscarPorNombre(String nombre);
    CategoriaEntity actualizar(Long id, CategoriaEntity nuevaData);
    void eliminar(Long id);
}
