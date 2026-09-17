package br.edu.fiap.api.exception;

public class CategoriaNaoEncontradoException extends RuntimeException {
    public CategoriaNaoEncontradoException(Long id) {
        super("Categoria nao encontrado: " + id);
    }
}
