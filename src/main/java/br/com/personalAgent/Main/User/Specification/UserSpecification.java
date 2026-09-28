package br.com.personalAgent.Main.User.Specification;

import br.com.personalAgent.Main.User.Model.Enum.UserStatus;
import br.com.personalAgent.Main.User.Model.Enum.UserType;
import br.com.personalAgent.Main.User.Model.Record.UserFilter;
import br.com.personalAgent.Main.User.Model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> withFilter(UserFilter filter){
        return Specification.where(hasName(filter.name()))
                .and(hasEmail(filter.email()))
                .and(hasType(filter.type()))
                .and(hasStatus(filter.status()));
    }

    private static Specification<User> hasName(String name) {
        if (name == null || name.isBlank()) return null;
        return (root, query, cb)
                -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<User> hasEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return (root, query, cb)
                -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<User> hasType(UserType type) {
        if (type == null) return null;
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    private static Specification<User> hasStatus(UserStatus status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

}
