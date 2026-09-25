package br.com.personalAgent.Main.User.Repository;

import br.com.personalAgent.Main.User.Model.User;
import br.com.personalAgent.Main.User.Model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByStatusAndInactivatedAtBefore(UserStatus status, LocalDateTime cutoff);
}
