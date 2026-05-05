package com.practicandospring.todolist.repository;

import com.practicandospring.todolist.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
    /*
    Métodos Estándar:
        save(S entity): Sirve tanto para insertar como para actualizar.

        findAll(): Retorna todas las entidades.

        findById(ID id): Busca por la llave primaria.

        deleteById(ID id): Elimina un registro.

    Al escribir el método en la interfaz siguiendo la convención de nombres de Spring:

    findBy: Le indica a Spring que quieres una consulta de búsqueda.

    Nombre: Le indica que el filtro es sobre el atributo nombre de tu CategoriaEntity.

    IgnoreCase: Le indica que debe aplicar un UPPER o LOWER en el SQL para que no importe si escribes "estudio" o "ESTUDIO".
    */
    Optional<CategoriaEntity> findByNombreIgnoreCase(String nombre);
}
