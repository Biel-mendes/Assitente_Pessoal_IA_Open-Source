package br.com.personalAgent.Main.Component;

import br.com.personalAgent.Main.Session.Service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;
    private static final Logger log = LoggerFactory.getLogger(SessionInterceptor.class);

    public SessionInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String userId = "N/A";

            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                userId = auth.getName();
            }

            String method = request.getMethod(); // GET, POST, PUT, DELETE
            String uri = request.getRequestURI();   // /users, etc.
            String tokenBruto = request.getHeader("Authorization");
            String token = (tokenBruto != null && tokenBruto.length() > 16)
                    ? tokenBruto.substring(tokenBruto.length() - 16)
                    : "No Token";

            sessionService.registerSession(
                    userId,
                    method,
                    uri,
                    token,
                    Map.of("remoteAddr", request.getRemoteAddr())
            );

        } catch (Exception e) {
            log.error("Erro ao salvar log de sessão", e);
        }
        return true;
    }

}