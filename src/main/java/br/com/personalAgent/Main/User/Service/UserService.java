package br.com.personalAgent.Main.User.Service;

import br.com.personalAgent.Main.Config.Excepiton.ForbiddenActionException;
import br.com.personalAgent.Main.Config.Excepiton.BusinessException;
import br.com.personalAgent.Main.Config.Excepiton.ResourceNotFoundException;
import br.com.personalAgent.Main.Config.Excepiton.TimeoutException;
import br.com.personalAgent.Main.User.Model.User;
import br.com.personalAgent.Main.User.Model.UserStatus;
import br.com.personalAgent.Main.User.Model.UserType;
import br.com.personalAgent.Main.User.Repository.UserRepository;
import jakarta.persistence.QueryTimeoutException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Construtor único sem necessidade de @Autowired no atributo
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Métodos Auxiliares de Validação de Permissão
    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isOwner(Authentication auth, UUID targetUserId) {
        if (auth == null || auth.getPrincipal() == null || targetUserId == null) {
            return false;
        }
        // Como o SecurityFilter grava a String do ID no Principal:
        return auth.getPrincipal().toString().equalsIgnoreCase(targetUserId.toString());
    }

    // CREATE
    @Transactional
    public User createUser(User user) {
        if (user.getType() == UserType.ADMIN) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (!isAdmin(auth)) {
                throw new ForbiddenActionException("Apenas administradores podem criar novos usuários do tipo ADMIN.");
            }
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("E-mail já cadastrado.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            return userRepository.save(user);
        } catch (QueryTimeoutException e) {
            throw new TimeoutException("Tempo limite da consulta excedido.");
        } catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao salvar usuário.");
        }
    }

    // FIND BY ID
    @Transactional(readOnly = true)
    public User findUserById(UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isAdmin(auth) && !isOwner(auth, id)) {
            throw new ForbiddenActionException("Você não possui permissão para visualizar os dados deste usuário.");
        }

        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        } catch (QueryTimeoutException e) {
            throw new TimeoutException("Tempo limite da consulta excedido.");
        }
    }

    // FIND BY EMAIL
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Compara com o ID do usuário encontrado pelo e-mail
        if (!isAdmin(auth) && !isOwner(auth, user.getId())) {
            throw new ForbiddenActionException("Você não possui permissão para visualizar este usuário.");
        }

        return user;
    }

    // FIND BY EMAIL (para uso somente interno)
    public User findUserByEmailAuth (String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        return user;
    }

    // FIND ALL
    @Transactional(readOnly = true)
    public List<User> findAllUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth)) {
            throw new ForbiddenActionException("Apenas administradores podem listar todos os usuários.");
        }
        try {
            return userRepository.findAll();
        } catch (QueryTimeoutException e) {
            throw new TimeoutException("Tempo limite da consulta excedido.");
        }
    }

    // UPDATE
    @Transactional
    public User updateUser(UUID id, User userUpdate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isAdmin(auth) && !isOwner(auth, id)) {
            throw new ForbiddenActionException("Você não possui permissão para alterar os dados deste usuário.");
        }

        // Busca direto no repositório para evitar revalidar permissão dentro de findUserById
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        // Se alterou o e-mail, verifica se o novo já existe em outro usuário
        if (!existingUser.getEmail().equalsIgnoreCase(userUpdate.getEmail())) {
            if (userRepository.existsByEmail(userUpdate.getEmail())) {
                throw new BusinessException("E-mail já cadastrado por outro usuário.");
            }
            existingUser.setEmail(userUpdate.getEmail());
        }

        existingUser.setName(userUpdate.getName());

        // Atualiza a senha somente se foi enviada uma nova
        if (userUpdate.getPassword() != null && !userUpdate.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        }

        try {
            return userRepository.save(existingUser);
        } catch (QueryTimeoutException e) {
            throw new TimeoutException("Tempo limite da consulta excedido.");
        } catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao atualizar usuário.");
        }
    }

    // DELETE
    @Transactional
    public void deleteUser(UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isAdmin(auth) && !isOwner(auth, id)) {
            throw new ForbiddenActionException("Você não possui permissão para excluir este usuário.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        try {
            if (isAdmin(auth)) {
                userRepository.delete(user);
            } else {
                user.setStatus(UserStatus.INACTIVE);
                user.setInactivatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        } catch (QueryTimeoutException e) {
            throw new TimeoutException("Tempo limite da consulta excedido.");
        } catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao deletar usuário.");
        }
    }

    @Transactional
    public void reactivateIfInactive(User user) {
        if (user.getStatus() == UserStatus.INACTIVE) {
            user.setStatus(UserStatus.ACTIVE);
            user.setInactivatedAt(null);
            userRepository.save(user);
        }
    }

}