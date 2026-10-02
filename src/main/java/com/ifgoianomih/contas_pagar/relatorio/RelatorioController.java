package com.ifgoianomih.contas_pagar.relatorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ifgoianomih.contas_pagar.relatorio.RelatorioDtos.InadimplenciaResponse;
import com.ifgoianomih.contas_pagar.relatorio.RelatorioDtos.ResumoFinanceiroResponse;
import com.ifgoianomih.contas_pagar.relatorio.RelatorioDtos.TotalPorCategoriaResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatorios", description = "Consultas financeiras consolidadas")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping("/resumo")
    @Operation(summary = "Posicao do periodo: previsto, quitado, em aberto e vencido. "
            + "Sem datas, usa o mes corrente.")
    public ResumoFinanceiroResponse resumo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {
        return service.resumo(de, ate);
    }

    @GetMapping("/por-categoria")
    @Operation(summary = "Totais agrupados por categoria do plano de contas")
    public List<TotalPorCategoriaResponse> porCategoria(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {
        return service.porCategoria(de, ate);
    }

    @GetMapping("/inadimplencia")
    @Operation(summary = "Titulos vencidos e em aberto nos dois dominios, com dias de atraso")
    public InadimplenciaResponse inadimplencia() {
        return service.inadimplencia();
    }
}
