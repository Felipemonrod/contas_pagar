package com.ifgoianomih.contas_pagar.pessoa;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ifgoianomih.contas_pagar.pessoa.PessoaDtos.PessoaRequest;
import com.ifgoianomih.contas_pagar.pessoa.PessoaDtos.PessoaResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pessoas")
@Tag(name = "Pessoas", description = "Clientes e fornecedores")
public class PessoaController {

    private final PessoaService service;

    public PessoaController(PessoaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista pessoas, com filtro opcional por nome ou papel")
    public List<PessoaResponse> listar(@RequestParam(required = false) String nome,
                                       @RequestParam(required = false) String papel) {
        return service.listar(nome, papel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha uma pessoa com telefones e enderecos")
    public PessoaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra cliente, fornecedor ou ambos")
    public ResponseEntity<PessoaResponse> cadastrar(@RequestBody @Valid PessoaRequest req) {
        PessoaResponse criada = service.cadastrar(req);
        return ResponseEntity.created(URI.create("/api/pessoas/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera os dados de uma pessoa")
    public PessoaResponse alterar(@PathVariable Long id, @RequestBody @Valid PessoaRequest req) {
        return service.alterar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativa a pessoa, preservando o historico de titulos")
    public PessoaResponse inativar(@PathVariable Long id) {
        return service.inativar(id);
    }
}
