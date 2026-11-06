package dev.owasplab.common;

import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CommonSecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/**
	 * Cadena para todo lo que no sea /vuln/** ni /secure/**: Actuator y cualquier otra ruta.
	 * Sin esta cadena esas rutas quedarían sin ningún filtro de seguridad, porque Spring Boot retira su cadena por
	 * defecto en cuanto existe un SecurityFilterChain propio. Con el perfil insecure la reemplaza InsecureActuatorConfig.
	 */
	@Bean
	@Profile("!insecure")
	@Order(Ordered.LOWEST_PRECEDENCE)
	SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((auth) -> auth
				// El ERROR dispatch de /error tiene que pasar para que el cliente reciba el status original.
				.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
				.requestMatchers("/actuator/health").permitAll()
				.anyRequest().authenticated())
			.httpBasic(withDefaults())
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}

}
