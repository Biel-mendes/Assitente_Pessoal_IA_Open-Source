package br.com.personalAgent.Main.Config;

import br.com.personalAgent.Main.Component.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SessionInterceptor sessionInterceptor;

    public WebConfig(SessionInterceptor sessionInterceptor) {
        this.sessionInterceptor = sessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**") // Ajuste para as rotas que deseja interceptar (ex: /** para tudo)
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**"); // Exclui rotas de documentação se tiver
    }
}