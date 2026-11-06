package dev.owasplab.a04;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import dev.owasplab.common.LegacyUserRepository;
import dev.owasplab.common.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PasswordStorageTest {

	private static final String SHARED_PASSWORD = "password";

	// MD5 de "password": aparece en cualquier tabla precalculada.
	private static final String KNOWN_MD5 = "5f4dcc3b5aa765d61d8327deb882cf99";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private UserRepository users;

	@Autowired
	private LegacyUserRepository legacyUsers;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private void register(String path, String username, String password) throws Exception {
		mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON)
			.content("{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password)))
			.andExpect(status().isCreated());
	}

	private String legacyHash(String username) {
		return legacyUsers.findByUsername(username).orElseThrow().getPasswordHash();
	}

	private String secureHash(String username) {
		return users.findByUsername(username).orElseThrow().getPassword();
	}

	@Test
	void vulnerableEndpointStoresIdenticalKnownMd5Hashes() throws Exception {
		register("/vuln/register", "legacy-1", SHARED_PASSWORD);
		register("/vuln/register", "legacy-2", SHARED_PASSWORD);

		assertThat(legacyHash("legacy-1")).isEqualTo(KNOWN_MD5);
		assertThat(legacyHash("legacy-2")).isEqualTo(legacyHash("legacy-1"));
	}

	@Test
	void secureEndpointStoresDistinctBcryptHashes() throws Exception {
		register("/secure/register", "secure-1", SHARED_PASSWORD);
		register("/secure/register", "secure-2", SHARED_PASSWORD);

		String first = secureHash("secure-1");
		String second = secureHash("secure-2");
		assertThat(first).startsWith("{bcrypt}").isNotEqualTo(second);
		assertThat(second).startsWith("{bcrypt}");
		assertThat(passwordEncoder.matches(SHARED_PASSWORD, first)).isTrue();
		assertThat(passwordEncoder.matches(SHARED_PASSWORD, second)).isTrue();
	}

	@Test
	void onlySecureRegistrationCanAuthenticate() throws Exception {
		register("/secure/register", "real-user", SHARED_PASSWORD);
		register("/vuln/register", "legacy-user", SHARED_PASSWORD);

		mvc.perform(get("/secure/orders/search").param("product", "x").with(httpBasic("real-user", SHARED_PASSWORD)))
			.andExpect(status().isOk());
		mvc.perform(get("/secure/orders/search").param("product", "x").with(httpBasic("legacy-user", SHARED_PASSWORD)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void secureEndpointRejectsShortPasswordAndDuplicateUsername() throws Exception {
		mvc.perform(post("/secure/register").contentType(MediaType.APPLICATION_JSON)
			.content("{\"username\":\"short-pass\",\"password\":\"123\"}"))
			.andExpect(status().isBadRequest());

		register("/secure/register", "taken", SHARED_PASSWORD);
		mvc.perform(post("/secure/register").contentType(MediaType.APPLICATION_JSON)
			.content("{\"username\":\"taken\",\"password\":\"another-password\"}"))
			.andExpect(status().isConflict());
	}

}
