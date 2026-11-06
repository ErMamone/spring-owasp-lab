package dev.owasplab.vulnerable;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Sigue exigiendo autenticación: los casos de este paquete se explotan siendo un usuario válido, igual que un atacante
 * con una cuenta propia. Lo roto está en los controllers, no acá.
 */
@Configuration
public class VulnerableSecurityConfig {

	@Bean
	@Order(1)
	SecurityFilterChain vulnerableChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/vuln/**")
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers(HttpMethod.POST, "/vuln/register").permitAll()
				.anyRequest().authenticated())
			.httpBasic(withDefaults())
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// INSEGURO a propósito: CSRF apagado en toda la cadena, en vez de exceptuar solo lo que hace falta.
			.csrf((csrf) -> csrf.disable());
		return http.build();
	}

}
