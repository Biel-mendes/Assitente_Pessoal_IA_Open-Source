package br.com.personalAgent.Main.Session.Repository;

import br.com.personalAgent.Main.Session.Modal.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
    List<Session> findByUserId(String userId);
    List<Session> findByActions(String actions);
}
