package com.ifgoianomih.contas_pagar.contapagar;

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

import com.ifgoianomih.contas_pagar.contapagar.dto.ContaPagarRequest;
import com.ifgoianomih.contas_pagar.contapagar.dto.ContaPagarResponse;
import com.ifgoianomih.contas_pagar.contapagar.dto.QuitacaoRequest;
import com.ifgoianomih.contas_pagar.contapagar.dto.SimulacaoQuitacaoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Camada de entrada HTTP. Nao contem regra de negocio: recebe, delega ao
 * service e devolve o DTO de resposta.
 */
@RestController
@RequestMapping("/api/contas-pagar")
@Tag(name = "Contas a Pagar", description = "Lancamento, consulta e quitacao de titulos a pagar")
public class ContaPagarController {

    private final ContaPagarService service;

    public ContaPagarController(ContaPagarService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista titulos, com filtro opcional por situacao e periodo de vencimento")
    public List<ContaPagarResponse> listar(
            @RequestParam(required = false) String situacao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {
        return service.listar(situacao, de, ate);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha um titulo, com seus rateios e pagamentos")
    public ContaPagarResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/inadimplentes")
    @Operation(summary = "Titulos vencidos e ainda em aberto")
    public List<ContaPagarResponse> inadimplentes() {
        return service.inadimplentes();
    }

    @PostMapping
    @Operation(summary = "Lanca um novo titulo a pagar")
    public ResponseEntity<ContaPagarResponse> lancar(@RequestBody @Valid ContaPagarRequest req) {
        ContaPagarResponse criada = service.lancar(req);
        return ResponseEntity.created(URI.create("/api/contas-pagar/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera um titulo que ainda nao teve pagamento")
    public ContaPagarResponse alterar(@PathVariable Long id, @RequestBody @Valid ContaPagarRequest req) {
        return service.alterar(id, req);
    }

    @GetMapping("/{id}/simulacao")
    @Operation(summary = "Previa do valor devido numa data, com desconto ou juros aplicados")
    public SimulacaoQuitacaoResponse simular(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return service.simular(id, data);
    }

    @PostMapping("/{id}/pagamento")
    @Operation(summary = "Registra o pagamento do titulo (aceita pagamento parcial)")
    public ContaPagarResponse quitar(@PathVariable Long id, @RequestBody @Valid QuitacaoRequest req) {
        return service.quitar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancela o titulo, mantendo o registro para consulta")
    public ContaPagarResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PostMapping("/atualizar-vencidas")
    @Operation(summary = "Marca como VENCIDA todo titulo em aberto com vencimento passado")
    public ResponseEntity<String> atualizarVencidas() {
        int total = service.atualizarVencidas();
        return ResponseEntity.ok(total + " titulo(s) marcado(s) como vencido(s).");
    }
}
