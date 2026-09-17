package br.edu.fiap.api.controller;

import br.edu.fiap.api.controller.dto.CategoriaRequest;
import br.edu.fiap.api.controller.dto.CategoriaResponse;
import br.edu.fiap.api.entity.Categoria;
import br.edu.fiap.api.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias")
public class CategoriaController {
    private final CategoriaService service;

    /**
     * Cria o controller com o serviço de aplicação.
     *
     * @param service casos de uso de categoria
     */
    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    /**
     * Lista os categorias.
     *
     * @return representações dos categorias cadastrados
     */
    @GetMapping
    @Operation(summary = "Listar categoria", description = "Retorna todas as categorias cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    public List<CategoriaResponse> listar() {
        return service.listar().stream().map(CategoriaResponse::de).toList();
    }

    /**
     * Busca um categoria.
     *
     * @param id identificador recebido na URI
     * @return representação do categoria
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "categoria encontrado"),
            @ApiResponse(responseCode = "404", description = "categoria não encontrado")
    })
    public CategoriaResponse buscar(
            @Parameter(description = "Identificador do categoria", example = "1")
            @PathVariable Long id) {
        return CategoriaResponse.de(service.buscar(id));
    }

    /**
     * Cria um categoria e informa sua URI no cabeçalho {@code Location}.
     *
     * @param request corpo JSON validado
     * @return resposta 201 com o categoria criado
     */
    @PostMapping
    @Operation(summary = "Criar categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "categoria criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<CategoriaResponse> criar(
            @Valid @RequestBody CategoriaRequest request) {
        Categoria salvo = service.criar(request.nome(), request.descricao());
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
        return ResponseEntity.created(localizacao).body(CategoriaResponse.de(salvo));
    }

    /**
     * Atualiza integralmente os dados editáveis de um categoria.
     *
     * @param id identificador recebido na URI
     * @param request novo estado validado
     * @return representação atualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "categoria atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "categoria não encontrado")
    })
    public CategoriaResponse atualizar(
            @Parameter(description = "Identificador do categoria", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {
        return CategoriaResponse.de(
                service.atualizar(id, request.nome(), request.descricao()));
    }

    /**
     * Exclui um categoria.
     *
     * @param id identificador recebido na URI
     * @return resposta 204 sem corpo
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "categoria excluído"),
            @ApiResponse(responseCode = "404", description = "categoria não encontrado")
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador do categoria", example = "1")
            @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
