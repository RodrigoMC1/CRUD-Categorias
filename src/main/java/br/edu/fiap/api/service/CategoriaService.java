package br.edu.fiap.api.service;

import br.edu.fiap.api.entity.Categoria;
import br.edu.fiap.api.exception.CategoriaNaoEncontradoException;
import br.edu.fiap.api.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaService {
    private final CategoriaRepository repository;

    /**
     * Cria o serviço com sua dependência de persistência.
     *
     * @param repository repositório de categorias
     */
    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todos os categorias cadastrados.
     *
     * @return categorias encontrados
     */
    public List<Categoria> listar() {
        return repository.findAll();
    }

    /**
     * Busca um categoria pelo identificador.
     *
     * @param id identificador do categoria
     * @return categoria encontrado
     * @throws CategoriaNaoEncontradoException quando o identificador não existe
     */
    public Categoria buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradoException(id));
    }

    /**
     * Cria e persiste um categoria.
     *
     * @param nome nome do categoria
     * @param descricao preço do categoria
     * @return categoria persistido, com identificador
     */
    @Transactional
    public Categoria criar(String nome, String descricao) {
        return repository.save(new Categoria(nome, descricao));
    }

    /**
     * Atualiza integralmente os campos editáveis de um categoria.
     *
     * @param id identificador do categoria
     * @param nome novo nome
     * @param descricao novo preço
     * @return categoria atualizado
     * @throws CategoriaNaoEncontradoException quando o identificador não existe
     */
    @Transactional
    public Categoria atualizar(Long id, String nome, String descricao) {
        Categoria categoria = buscar(id);
        categoria.atualizar(nome, descricao);
        return repository.save(categoria);
    }

    /**
     * Exclui um categoria.
     *
     * @param id identificador do categoria
     * @throws CategoriaNaoEncontradoException quando o identificador não existe
     */
    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }
}