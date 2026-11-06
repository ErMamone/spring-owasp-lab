package dev.owasplab.vulnerable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.owasplab.common.LegacyUser;
import dev.owasplab.common.LegacyUserRepository;

@RestController
public class VulnerableRegisterController {

	record RegisterRequest(String username, String password) {
	}

	record RegisterResponse(String username) {
	}

	private final LegacyUserRepository legacyUsers;

	public VulnerableRegisterController(LegacyUserRepository legacyUsers) {
		this.legacyUsers = legacyUsers;
	}

	/**
	 * A04. INSEGURO a propósito: MD5 es rapidísimo (facilita la fuerza bruta) y sin salt el mismo password da siempre el
	 * mismo hash, así que un volcado de la tabla revela quiénes lo comparten y se revierte con tablas precalculadas.
	 */
	@PostMapping("/vuln/register")
	@ResponseStatus(HttpStatus.CREATED)
	RegisterResponse register(@RequestBody RegisterRequest request) throws NoSuchAlgorithmException {
		byte[] digest = MessageDigest.getInstance("MD5").digest(request.password().getBytes(StandardCharsets.UTF_8));
		legacyUsers.save(new LegacyUser(request.username(), HexFormat.of().formatHex(digest)));
		return new RegisterResponse(request.username());
	}

}
