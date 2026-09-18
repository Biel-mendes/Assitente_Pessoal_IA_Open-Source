package br.com.personalAgent.Main.User.Model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record UserRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String name,
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,
        String password,
        @NotNull(message = "O tipo de usuário é obrigatório")
        UserType type
) {
    public static UserRequestDTO fromEntity(User user) {
        return new UserRequestDTO(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getType()
        );
    }
}
