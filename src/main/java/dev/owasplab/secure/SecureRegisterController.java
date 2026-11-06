package dev.owasplab.secure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.owasplab.common.User;
import dev.owasplab.common.UserRepository;

@RestController
public class SecureRegisterController {

	// El máximo de 72 es el límite de entrada de bcrypt.
	record RegisterRequest(@NotBlank @Size(max = 64) String username, @NotBlank @Size(min = 8, max = 72) String password) {
	}

	record RegisterResponse(String username) {
	}

	private final UserRepository users;

	private final PasswordEncoder passwordEncoder;

	public SecureRegisterController(UserRepository users, PasswordEncoder passwordEncoder) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * A04. El DelegatingPasswordEncoder usa bcrypt por defecto: lento a propósito y con salt aleatorio por hash. El
	 * prefijo {bcrypt} guarda el algoritmo junto al hash, lo que permite migrar a otro sin invalidar los existentes.
	 */
	@PostMapping("/secure/register")
	ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
		if (users.existsByUsername(request.username())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		users.save(new User(request.username(), passwordEncoder.encode(request.password())));
		return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(request.username()));
	}

}
