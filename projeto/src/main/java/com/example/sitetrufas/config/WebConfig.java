package com.example.sitetrufas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Expõe a pasta de uploads (onde ficam as imagens de produto enviadas
 * pelo administrador) como recurso estático acessível em /img/produtos/**,
 * mesmo quando essa pasta está fora do classpath/jar da aplicação.
 *
 * Também registra o interceptor responsável por proteger as rotas que
 * exigem login de cliente ou administrador.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads/produtos}")
    private String diretorioUpload;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String localizacao = "file:" + diretorioUpload + "/";
        registry.addResourceHandler("/img/produtos/**")
                .addResourceLocations(localizacao);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AutenticacaoInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/img/**");
    }
}
