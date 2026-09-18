package br.com.personalAgent.Main.Component;

import br.com.personalAgent.Main.Session.Service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public SessionInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = "Anonymous";
        String userId = "N/A"; // Valor padrão se não estiver logado

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            userId = auth.getName();

            // Se o seu objeto Principal guarda o ID, extraia ele aqui.
            // Exemplo: se o seu UserDetails customizado tem o método getId():
            // Object principal = auth.getPrincipal();
            // if (principal instanceof SeuUserCustomizado) {
            //     userId = ((SeuUserCustomizado) principal).getId().toString();
            // }
        }

        String method = request.getMethod(); // GET, POST, PUT, DELETE
        String uri = request.getRequestURI();   // /users, etc.
        String token = request.getHeader("Authorization");

        // ORDEM CORRETA DOS PARÂMETROS:
        // 1. userId
        // 2. user (nome)
        // 3. token
        // 4. actions (método HTTP)
        // 5. resource (URI)
        // 6. details
        sessionService.registerSession(
                userId,
                token != null ? token : "No Token",
                method,
                uri,
                Map.of("remoteAddr", request.getRemoteAddr())
        );

        return true;
    }

}