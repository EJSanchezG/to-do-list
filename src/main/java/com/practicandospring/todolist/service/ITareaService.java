package com.practicandospring.todolist.service;

import com.practicandospring.todolist.dto.TareaDTO;

import java.util.List;

public interface ITareaService {
    TareaDTO crearTarea(TareaDTO dto);
    List<TareaDTO> listarPorCategoria(String nombreCat);
    List<TareaDTO> listarTodas();
    TareaDTO actualizarTarea(Long id, TareaDTO dto);
    void eliminarTarea(Long id);
}
