package com.practicandospring.todolist.service.impl;

import com.practicandospring.todolist.config.InfoAppConfig;
import com.practicandospring.todolist.dto.TareaDTO;
import com.practicandospring.todolist.mapper.TareaMapper;
import com.practicandospring.todolist.entity.CategoriaEntity;
import com.practicandospring.todolist.entity.TareaEntity;
import com.practicandospring.todolist.repository.ITareaRepository;
import com.practicandospring.todolist.service.ICategoriaService;
import com.practicandospring.todolist.service.ITareaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TareaService implements ITareaService {
    private final InfoAppConfig appConfig;
    private final TareaMapper tareaMapper;
    private final ITareaRepository tareaRepository;
    private final ICategoriaService categoriaService; // Dependencia de Servicio a Servicio

    public TareaService(InfoAppConfig appConfig, TareaMapper tareaMapper, ITareaRepository tareaRepository, ICategoriaService categoriaService) {
        this.appConfig = appConfig;
        this.tareaMapper = tareaMapper;
        this.tareaRepository = tareaRepository;
        this.categoriaService = categoriaService;
    }

    @Override
    public TareaDTO crearTarea(TareaDTO dto) {
        System.out.println(">>> Registro iniciado por: " + appConfig.developer());

        // Se usa el servicio de categorías para validar existencia
        CategoriaEntity categoria = categoriaService.buscarPorNombre(dto.nombreCategoria());

        // Mapeo Pro: Una sola línea
        TareaEntity entidad = tareaMapper.toEntity(dto);

        entidad.setCategoria(categoria);
        entidad.setCompletada(false);
        entidad.setFechaCreacion(LocalDateTime.now());

        // Se guarda en PostgreSQL
        TareaEntity entidadGuardada = tareaRepository.save(entidad);

        // Se retorna mapeando de vuelta a DTO
        return tareaMapper.toDto(entidadGuardada);
    }

    public List<TareaDTO> listarPorCategoria(String nombreCat) {
        return tareaRepository.findByCategoriaNombreIgnoreCase(nombreCat)
                .stream()
                .map(tareaMapper::toDto)
                .toList();
    }

    @Override
    public List<TareaDTO> listarTodas() {
        return tareaRepository.findAll() // Trae todo de la BD
                .stream()
                .map(tareaMapper::toDto)
                .toList();
    }

    @Override
    public TareaDTO actualizarTarea(Long id, TareaDTO dto) {
        TareaEntity tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Se valida que exista el nombre de la categoria que viene en el DTO
        CategoriaEntity categoria = categoriaService.buscarPorNombre(dto.nombreCategoria());

        tarea.setTitulo(dto.titulo());
        tarea.setCategoria(categoria);

        return tareaMapper.toDto(tareaRepository.save(tarea));
    }

    @Override
    public void eliminarTarea(Long id) {
        if (!tareaRepository.existsById(id)) {
            throw new RuntimeException("La tarea con ID " + id + " no existe.");
        }
        tareaRepository.deleteById(id);
    }
}
