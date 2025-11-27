package ee.mihkel.veebipood.config;

import ee.mihkel.veebipood.service.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain configureHttp(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.GET, "parcelmachines").permitAll()
                                .requestMatchers(HttpMethod.GET, "public-products").permitAll()
                                .requestMatchers(HttpMethod.GET, "categories").permitAll()
                                .requestMatchers(HttpMethod.POST, "login").permitAll()
                                .requestMatchers(HttpMethod.POST, "signup").permitAll()
                                .requestMatchers(HttpMethod.GET, "products/*").permitAll()
                                .requestMatchers(HttpMethod.GET, "public-persons").permitAll()
                                .requestMatchers(HttpMethod.POST, "categories").hasAuthority("admin")
                                .requestMatchers(HttpMethod.DELETE, "categories").hasAuthority("admin")
                                .requestMatchers(HttpMethod.POST, "products").hasAuthority("admin")
                                .requestMatchers(HttpMethod.DELETE, "products").hasAuthority("admin")
                                .requestMatchers(HttpMethod.PUT, "products").hasAuthority("admin")
                                .requestMatchers(HttpMethod.PATCH, "change-admin").hasAuthority("superadmin")
                                .requestMatchers(HttpMethod.GET, "persons").hasAuthority("superadmin")
                                .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // RestControllerist võin ära võtta @CrossOrigin
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
