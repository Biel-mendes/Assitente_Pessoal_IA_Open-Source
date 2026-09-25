package br.com.personalAgent.Main.User.Service;

import br.com.personalAgent.Main.User.Model.UserStatus;
import br.com.personalAgent.Main.User.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(UserCleanupJob.class);

    private final UserRepository userRepository;

    public UserCleanupJob(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 3 * * *") // todo dia às 3h
    public void deleteInactiveUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        var toDelete = userRepository.findByStatusAndInactivatedAtBefore(UserStatus.INACTIVE, cutoff);

        if (!toDelete.isEmpty()) {
            userRepository.deleteAll(toDelete);
            log.info("Exclusão automática: {} usuário(s) removido(s) após 30 dias de inatividade.", toDelete.size());
        }
    }

}
