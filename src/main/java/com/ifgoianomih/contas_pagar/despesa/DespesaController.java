package com.ifgoianomih.contas_pagar.despesa;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.ifgoianomih.contas_pagar.despesa.DespesaDtos.DespesaRequest;
import com.ifgoianomih.contas_pagar.despesa.DespesaDtos.DespesaResponse;
import com.ifgoianomih.contas_pagar.despesa.DespesaDtos.GerarTituloRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/despesas")
@Tag(name = "Despesas", description = "Previsao de gastos fixos que depois vira titulo a pagar")
public class DespesaController {

    private final DespesaService service;

    public DespesaController(DespesaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista despesas; use previstas=true para as que ainda nao viraram titulo")
    public List<DespesaResponse> listar(
            @RequestParam(required = false) Boolean previstas,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {
        return service.listar(previstas, de, ate);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha uma despesa")
    public DespesaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma despesa prevista")
    public ResponseEntity<DespesaResponse> cadastrar(@RequestBody @Valid DespesaRequest req) {
        DespesaResponse criada = service.cadastrar(req);
        return ResponseEntity.created(URI.create("/api/despesas/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera uma despesa que ainda nao virou titulo")
    public DespesaResponse alterar(@PathVariable Long id, @RequestBody @Valid DespesaRequest req) {
        return service.alterar(id, req);
    }

    @PostMapping("/{id}/gerar-titulo")
    @Operation(summary = "Converte a previsao em titulo a pagar, com o valor real da fatura")
    public DespesaResponse gerarTitulo(@PathVariable Long id, @RequestBody @Valid GerarTituloRequest req) {
        return service.gerarTitulo(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma despesa que ainda nao virou titulo")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
