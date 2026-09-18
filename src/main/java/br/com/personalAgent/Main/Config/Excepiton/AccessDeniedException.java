package br.com.personalAgent.Main.Config.Excepiton;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
