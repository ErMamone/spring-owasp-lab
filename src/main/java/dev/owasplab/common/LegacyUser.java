package dev.owasplab.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Destino del registro vulnerable (A04). Tabla aparte de app_users: estos usuarios nunca pueden autenticarse. */
@Entity
@Table(name = "legacy_users")
public class LegacyUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String passwordHash;

	protected LegacyUser() {
	}

	public LegacyUser(String username, String passwordHash) {
		this.username = username;
		this.passwordHash = passwordHash;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

}
