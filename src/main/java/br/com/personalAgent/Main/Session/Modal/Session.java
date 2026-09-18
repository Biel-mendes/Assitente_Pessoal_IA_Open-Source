package br.com.personalAgent.Main.Session.Modal;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Document(collection = "sessions")
public class Session {

    @Id
    private String id;

    private String userId;
    private String token;
    private String actions;
    private String resource;
    private Map<String, Object> details;
    private LocalDateTime timestamp = LocalDateTime.now();

    public Session() {}

    public Session(String userId, String token, String actions, String resource, Map<String, Object> details) {
        this.userId = userId;
        this.token = token;
        this.actions = actions;
        this.resource = resource;
        this.details = details;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}