package dev.owasplab.secure;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy;

@Configuration
public class SecureSecurityConfig {

	@Bean
	@Order(2)
	SecurityFilterChain secureChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/secure/**")
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers(HttpMethod.POST, "/secure/register").permitAll()
				.anyRequest().authenticated())
			.httpBasic(withDefaults())
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// CSRF sigue activo. Solo se exceptúa el registro: es público, así que no hay sesión ni identidad de la
			// víctima que un atacante pueda aprovechar.
			.csrf((csrf) -> csrf.ignoringRequestMatchers("/secure/register"))
			// Es una API JSON: el navegador no tiene por qué cargar nada ni embeberla en un frame.
			.headers((headers) -> headers
				.contentSecurityPolicy((csp) -> csp.policyDirectives("default-src 'none'; frame-ancestors 'none'"))
				.crossOriginResourcePolicy((corp) -> corp.policy(CrossOriginResourcePolicy.SAME_ORIGIN)));
		return http.build();
	}

}
