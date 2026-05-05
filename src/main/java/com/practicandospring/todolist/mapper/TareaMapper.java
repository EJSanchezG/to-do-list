package com.practicandospring.todolist.mapper;

import com.practicandospring.todolist.dto.TareaDTO;
import com.practicandospring.todolist.model.TareaEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Esto permite inyectarlo como un Bean
public interface TareaMapper {

    // Convierte de DTO (Record) a Entity (Clase)
    @Mapping(target = "fechaCreacion", ignore = true) // Se ignora fechaCreacion porque el DTO no tiene
    @Mapping(source = "nombreCategoria", target = "categoria.nombre")
    TareaEntity toEntity(TareaDTO dto);

    // Convierte de Entity (Clase) a DTO (Record)
    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    TareaDTO toDto(TareaEntity entity);
}