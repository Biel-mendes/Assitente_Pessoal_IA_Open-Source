package br.com.personalAgent.Main.Config.Excepiton;

public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException(String message) {
        super(message);
    }
}
