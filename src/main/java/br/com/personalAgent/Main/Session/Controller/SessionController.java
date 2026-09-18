package br.com.personalAgent.Main.Session.Controller;

import br.com.personalAgent.Main.Session.Modal.Session;
import br.com.personalAgent.Main.Session.Service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@Tag(name = "Sessões", description = "Endpoints para visualizar sessões e logs de auditoria")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(summary = "Buscar sessão por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Session> getSessionById(@PathVariable String id) {
        return sessionService.findSessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar sessões por ação (ex: GET, POST)")
    @GetMapping("/actions/{actions}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Session>> getSessionsByActions(@PathVariable String actions) {
        List<Session> sessions = sessionService.findByActions(actions);
        if (sessions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "Buscar sessões por ID do usuário")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Session>> getSessionsByIdUser(@PathVariable String userId) {
        List<Session> sessions = sessionService.findByUserId(userId);
        if (sessions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sessions);
    }
}