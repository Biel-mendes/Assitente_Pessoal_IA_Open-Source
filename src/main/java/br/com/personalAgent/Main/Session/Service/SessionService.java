package br.com.personalAgent.Main.Session.Service;

import br.com.personalAgent.Main.Config.Excepiton.ResourceNotFoundException;
import br.com.personalAgent.Main.Config.Excepiton.TimeoutException;
import br.com.personalAgent.Main.Session.Modal.Session;
import br.com.personalAgent.Main.Session.Repository.SessionRepository;
import jakarta.persistence.QueryTimeoutException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void registerSession(String userId,
                                String actions,
                                String resource,
                                String token,
                                Map<String, Object> details)
    {
        try{
            Session log = new Session(userId, actions, resource, token, details);
            sessionRepository.save(log);
        } catch (QueryTimeoutException e) {
            throw new TimeoutException("Tempo limite excedido ao registrar sessão");
        } catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao registrar sessão: " + e.getMessage());
        }
    }

    public Optional<Session> findSessionById(String sessionId) {
        try {
            return sessionRepository.findById(sessionId);
        } catch (QueryTimeoutException e) {
            throw new TimeoutException("Tempo limite excedido ao buscar sessão");
        }catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao buscar sessão do usuário: " + e.getMessage());
        }
    }

    public List<Session> findByUserId(String userId) {
        try {
            return sessionRepository.findByUserId(userId);
        } catch (QueryTimeoutException e) {
            throw new TimeoutException("Tempo limite excedido ao buscar sessão do usuário");
        } catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao buscar sessão do usuário: " + e.getMessage());
        }
    }

    public List<Session> findByActions(String actions) {
        try {
            return sessionRepository.findByActions(actions);
        } catch (QueryTimeoutException e) {
            throw new TimeoutException("Tempo limite excedido ao buscar sessão por ações");
        } catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao buscar sessão por ações: " + e.getMessage());
        }
    }

}
