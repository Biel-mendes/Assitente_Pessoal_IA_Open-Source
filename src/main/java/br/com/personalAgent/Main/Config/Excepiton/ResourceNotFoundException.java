package br.com.personalAgent.Main.Config.Excepiton;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
