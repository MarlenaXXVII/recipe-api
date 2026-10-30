
package nl.novi.recipeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/recipes/myrecipes").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/recipes", "/recipes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ingredients/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/recipes/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/recipes/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/recipes/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/profiles/me").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/profiles").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/weekmenu").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/weekmenu").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/weekmenu/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/weekmenu/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/shopping-list").hasAnyRole("USER", "ADMIN")
                        .anyRequest().denyAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();

            authorities.addAll(scopesConverter.convert(jwt));

            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

            if (resourceAccess != null && resourceAccess.get("recipe-api") instanceof Map<?, ?> clientAccess) {
                if (clientAccess.get("roles") instanceof List<?> roles) {
                    for (Object role : roles) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                }
            }


            return authorities;
        });

        return converter;
    }
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, exception) -> {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            response.getWriter().write(
                    "{\"message\":\"Please log in to continue\"}"
            );
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> {
            response.setStatus(403);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            response.getWriter().write(
                    "{\"message\":\"You dont have permission to do that.\"}"
            );
        };
    }
}
