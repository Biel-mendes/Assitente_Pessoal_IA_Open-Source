package br.com.personalAgent.Main.Config;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class WslKeepAlive {

    private Process keepAliveProcess;

    public WslKeepAlive() {
        try {
            keepAliveProcess = new ProcessBuilder("wsl.exe", "-d", "Ubuntu", "-e", "tail", "-f", "/dev/null")
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
        } catch (Exception ignored) {
            // se falhar, RedisReadinessCheck ainda tenta reconectar normalmente
        }
    }

    @PreDestroy
    public void stop() {
        if (keepAliveProcess != null) {
            keepAliveProcess.destroy();
        }
    }
}