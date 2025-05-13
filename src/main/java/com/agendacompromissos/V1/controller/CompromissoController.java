package com.agendacompromissos.V1.controller;

import com.agendacompromissos.V1.model.Compromisso;
import com.agendacompromissos.V1.service.CompromissoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Anotação para indicar que é um controlador REST
@RequestMapping("/api/compromissos") // Define o caminho base para os endpoints deste controlador
public class CompromissoController {

    private final CompromissoService compromissoService;

    @Autowired // Injeção de dependência do serviço
    public CompromissoController(CompromissoService compromissoService) {
        this.compromissoService = compromissoService;
    }

    @GetMapping // HTTP GET para /api/compromissos
    public ResponseEntity<List<Compromisso>> listarTodosCompromissos() {
        List<Compromisso> compromissos = compromissoService.listarTodos();
        return ResponseEntity.ok(compromissos);
    }

    @GetMapping("/{id}") // HTTP GET para /api/compromissos/{id}
    public ResponseEntity<Compromisso> buscarCompromissoPorId(@PathVariable Long id) {
        return compromissoService.buscarPorId(id)
                .map(ResponseEntity::ok) // Se encontrado, retorna 200 OK com o compromisso
                .orElse(ResponseEntity.notFound().build()); // Se não, retorna 404 Not Found
    }

    @PostMapping // HTTP POST para /api/compromissos
    @ResponseStatus(HttpStatus.CREATED) // Define o status de resposta para 201 Created
    public Compromisso criarCompromisso(@RequestBody Compromisso compromisso) {
        // @RequestBody converte o JSON do corpo da requisição para o objeto Compromisso
        return compromissoService.salvar(compromisso);
    }

    @PutMapping("/{id}") // HTTP PUT para /api/compromissos/{id}
    public ResponseEntity<Compromisso> atualizarCompromisso(@PathVariable Long id, @RequestBody Compromisso compromisso) {
        try {
            Compromisso compromissoAtualizado = compromissoService.atualizar(id, compromisso);
            return ResponseEntity.ok(compromissoAtualizado);
        } catch (RuntimeException e) { // Exemplo de tratamento de exceção
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}") // HTTP DELETE para /api/compromissos/{id}
    public ResponseEntity<Void> deletarCompromisso(@PathVariable Long id) {
        try {
            compromissoService.deletarPorId(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } catch (RuntimeException e) { // Exemplo de tratamento de exceção
            return ResponseEntity.notFound().build();
        }
    }
}