package br.edu.fiap.api.repository;

import br.edu.fiap.api.entity.Categoria;
import br.edu.fiap.api.entity.Produto;
import jakarta.persistence.FetchType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>
{
    @EntityGraph(attributePaths = {"produtos"})
    Optional<Categoria> findWithProdutosById(Long id);
}
