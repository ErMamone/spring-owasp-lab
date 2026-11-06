package dev.owasplab.vulnerable;

import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/** A02. INSEGURO a propósito: reemplaza a la cadena por defecto y deja todo Actuator abierto sin autenticación. */
@Configuration
@Profile("insecure")
public class InsecureActuatorConfig {

	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	SecurityFilterChain insecureDefaultChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((auth) -> auth
				.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
				.requestMatchers("/actuator/**").permitAll()
				.anyRequest().authenticated())
			.httpBasic(withDefaults())
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}

}
