package com.practicandospring.todolist.dto;

import jakarta.validation.constraints.NotBlank;

public record TareaDTO(
        Long id,
        @NotBlank(message = "Debe ingresar el nombre de la tarea")
        String titulo,
        @NotBlank(message = "Debe ingresar una categoria para la tarea")
        String nombreCategoria, // <--- Solo importa recibir/enviar el nombre
        Boolean completada
){
}
