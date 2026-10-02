package br.com.personalAgent.Main.Login.Controller;

import br.com.personalAgent.Main.Component.TokenBlacklist;
import br.com.personalAgent.Main.Login.Model.LoginRequestDTO;
import br.com.personalAgent.Main.Login.Model.LoginResponseDTO;
import br.com.personalAgent.Main.Login.Service.TokenService;
import br.com.personalAgent.Main.User.Model.User;
import br.com.personalAgent.Main.User.Service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final TokenBlacklist tokenBlacklist;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService,
                          TokenBlacklist tokenBlacklist)
    {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody @Valid LoginRequestDTO body) {
        User user = userService.findUserByEmailAuth(body.email());
        if(!passwordEncoder.matches(body.password(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }
        userService.reactivateIfInactive(user);
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = authHeader.replace("Bearer ", "");
        Claims claims = tokenService.validateToken(token);

        Duration ttl = Duration.between(Instant.now(), claims.getExpiration().toInstant());
        tokenBlacklist.blacklist(claims.getId(), ttl);

        return ResponseEntity.noContent().build();
    }

}
