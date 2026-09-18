package br.com.personalAgent.Main.Login.Controller;

import br.com.personalAgent.Main.Login.Model.LoginRequestDTO;
import br.com.personalAgent.Main.Login.Model.LoginResponseDTO;
import br.com.personalAgent.Main.Login.Service.TokenService;
import br.com.personalAgent.Main.User.Model.User;
import br.com.personalAgent.Main.User.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody @Valid LoginRequestDTO body) {
        User user = userService.findUserByEmailAuth(body.email());
        if(!passwordEncoder.matches(body.password(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
