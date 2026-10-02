package com.ifgoianomih.contas_pagar.contareceber;

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

import com.ifgoianomih.contas_pagar.contareceber.dto.ContaReceberRequest;
import com.ifgoianomih.contas_pagar.contareceber.dto.ContaReceberResponse;
import com.ifgoianomih.contas_pagar.contareceber.dto.QuitacaoRecebimentoRequest;
import com.ifgoianomih.contas_pagar.contareceber.dto.SimulacaoRecebimentoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Camada de entrada HTTP. Nao contem regra de negocio: recebe, delega ao
 * service e devolve o DTO de resposta.
 */
@RestController
@RequestMapping("/api/contas-receber")
@Tag(name = "Contas a Receber", description = "Lancamento, consulta e quitacao de titulos a receber")
public class ContaReceberController {

    private final ContaReceberService service;

    public ContaReceberController(ContaReceberService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista titulos, com filtro opcional por situacao e periodo de vencimento")
    public List<ContaReceberResponse> listar(
            @RequestParam(required = false) String situacao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {
        return service.listar(situacao, de, ate);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha um titulo, com seus rateios e recebimentos")
    public ContaReceberResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/inadimplentes")
    @Operation(summary = "Titulos vencidos e ainda em aberto")
    public List<ContaReceberResponse> inadimplentes() {
        return service.inadimplentes();
    }

    @PostMapping
    @Operation(summary = "Lanca um novo titulo a receber")
    public ResponseEntity<ContaReceberResponse> lancar(@RequestBody @Valid ContaReceberRequest req) {
        ContaReceberResponse criada = service.lancar(req);
        return ResponseEntity.created(URI.create("/api/contas-receber/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera um titulo que ainda nao teve recebimento")
    public ContaReceberResponse alterar(@PathVariable Long id, @RequestBody @Valid ContaReceberRequest req) {
        return service.alterar(id, req);
    }

    @GetMapping("/{id}/simulacao")
    @Operation(summary = "Previa do valor devido numa data, com desconto ou juros aplicados")
    public SimulacaoRecebimentoResponse simular(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return service.simular(id, data);
    }

    @PostMapping("/{id}/recebimento")
    @Operation(summary = "Registra o recebimento do titulo (aceita recebimento parcial)")
    public ContaReceberResponse quitar(@PathVariable Long id, @RequestBody @Valid QuitacaoRecebimentoRequest req) {
        return service.quitar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancela o titulo, mantendo o registro para consulta")
    public ContaReceberResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PostMapping("/atualizar-vencidas")
    @Operation(summary = "Marca como VENCIDA todo titulo em aberto com vencimento passado")
    public ResponseEntity<String> atualizarVencidas() {
        int total = service.atualizarVencidas();
        return ResponseEntity.ok(total + " titulo(s) marcado(s) como vencido(s).");
    }
}
