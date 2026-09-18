package br.com.personalAgent.Main.User.Model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public record UserResponseDTO(
        @NotBlank(message = "O nome é obrigatório")
        String name,
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,
        @NotNull(message = "O tipo de usuário é obrigatório")
        UserType type,
        LocalDateTime create
) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getName(),
                user.getEmail(),
                user.getType(),
                user.getCreate()
        );
    }
}
