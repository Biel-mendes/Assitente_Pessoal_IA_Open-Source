package br.com.personalAgent.Main.User.Model.Record;

import br.com.personalAgent.Main.User.Model.Enum.UserStatus;
import br.com.personalAgent.Main.User.Model.Enum.UserType;

public record UserFilter(
        String name,
        String email,
        UserType type,
        UserStatus status
) {
}
