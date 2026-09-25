package br.com.personalAgent.Main.User.Controller;

import br.com.personalAgent.Main.User.Model.User;
import br.com.personalAgent.Main.User.Model.UserRequestDTO;
import br.com.personalAgent.Main.User.Model.UserResponseDTO;
import br.com.personalAgent.Main.User.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Todos os endpoints relacionados à gestão de usuários")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Cria um novo usuário", description = "Cadastra um usuário com senha criptografada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou e-mail duplicado"),
            @ApiResponse(responseCode = "504", description = "Timeout na operação com o banco")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO dto) {
        User userEntity = new User(dto.type(), dto.password(), dto.name(), dto.email());
        User created = userService.createUser(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.fromEntity(created));
    }

    @Operation(summary = "Busca usuário por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal == #id.toString()")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
    }

    @Operation(summary = "Busca usuário por E-mail")
    @GetMapping("/email")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal == #email")
    public ResponseEntity<UserResponseDTO> findByEmail(@RequestParam("value") String email) {
        User user = userService.findUserByEmail(email);
        return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
    }

    @Operation(summary = "Lista todos os usuários")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<UserResponseDTO> users = userService.findAllUser()
                .stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Atualiza um usuário existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal == #id.toString()")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UserRequestDTO dto
    ) {
        User user = new User(dto.type(), dto.password(), dto.name(), dto.email());
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(UserResponseDTO.fromEntity(updatedUser));
    }

    @Operation(summary = "Remove um usuário por ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal == #id.toString()")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
