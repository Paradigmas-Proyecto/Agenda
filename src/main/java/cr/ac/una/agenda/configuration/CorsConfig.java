package cr.ac.una.agenda.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuración global de CORS para permitir peticiones desde el frontend
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir todas las orígenes (para desarrollo)
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Permite cualquier origen
        
        // Permitir todos los headers
        config.addAllowedHeader("*");
        
        // Permitir todos los métodos HTTP
        config.addAllowedMethod("*");
        
        // Exponer headers de respuesta
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
