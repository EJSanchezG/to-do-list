package com.practicandospring.todolist.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity // Marca esta clase como una tabla de BD
@Table(name = "tareas") // Nombre de la tabla en Postgres
@Data
@NoArgsConstructor // OBLIGATORIO para JPA (Hibernate)
public class TareaEntity {
    @Id // Define la llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincremental en Postgres (Serial)
    private Long id;

    @Column(nullable = false, length = 150) // Restricciones de columna
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;

    private boolean completada;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
}
