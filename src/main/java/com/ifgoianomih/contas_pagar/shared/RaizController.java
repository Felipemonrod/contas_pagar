package com.ifgoianomih.contas_pagar.shared;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Hidden;

/**
 * A aplicacao e uma API REST e nao tem pagina inicial. Sem este redirect,
 * quem abre http://localhost:8080 ve a pagina de erro padrao do Spring e
 * acha que o sistema esta fora do ar.
 */
@Controller
@Hidden
public class RaizController {

    @GetMapping("/")
    public String paraDocumentacao() {
        return "redirect:/swagger-ui.html";
    }
}
