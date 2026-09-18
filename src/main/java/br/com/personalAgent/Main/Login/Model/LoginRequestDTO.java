package br.com.personalAgent.Main.Login.Model;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
