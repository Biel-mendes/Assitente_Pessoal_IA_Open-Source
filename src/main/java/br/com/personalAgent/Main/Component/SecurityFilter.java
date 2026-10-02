package br.com.personalAgent.Main.Component;

import br.com.personalAgent.Main.Login.Service.TokenService;
import br.com.personalAgent.Main.User.Model.Enum.UserStatus;
import br.com.personalAgent.Main.User.Service.UserStatusCache;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserStatusCache userStatusCache;
    private final RateLimiter rateLimiter;
    private final TokenBlacklist tokenBlacklist;

    public SecurityFilter(TokenService tokenService,
                          UserStatusCache userStatusCache,
                          RateLimiter rateLimiter,
                          TokenBlacklist tokenBlacklist)
    {
        this.tokenService = tokenService;
        this.userStatusCache = userStatusCache;
        this.rateLimiter = rateLimiter;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        return path.equals("/auth/login") ||
                path.startsWith("/error") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                (path.equals("/users") && method.equalsIgnoreCase("POST"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null){
            try{
                Claims claims = tokenService.validateToken(token);
                String userId = claims.getSubject();
                String role = claims.get("role", String.class);
                String jti = claims.getId();

                if (tokenBlacklist.isBlacklisted(jti)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                UUID id = UUID.fromString(userId);
                UserStatus status = userStatusCache.getStatus(id);

                if (status == null || status == UserStatus.INACTIVE) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                if (!rateLimiter.isAllowed(userId)) {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    return;
                }

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.replace("Bearer ", "");
    }
}