package dev.owasplab.a02;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import dev.owasplab.common.DataSeeder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DefaultProfileTest {

	@LocalServerPort
	private int port;

	private RealServerClient client;

	@BeforeEach
	void setUp() {
		client = new RealServerClient(port);
	}

	@Test
	void actuatorEnvRequiresAuthentication() throws Exception {
		assertThat(client.getAnonymous("/actuator/env").statusCode()).isEqualTo(401);
	}

	@Test
	void actuatorEnvIsNotExposedEvenWhenAuthenticated() throws Exception {
		assertThat(client.getAs(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD, "/actuator/env").statusCode()).isEqualTo(404);
	}

	@Test
	void healthIsPublicButWithoutDetails() throws Exception {
		HttpResponse<String> response = client.getAnonymous("/actuator/health");

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).contains("UP").doesNotContain("components", "H2");
	}

	@Test
	void errorsDoNotExposeStacktraceOrMessage() throws Exception {
		// Misma request que en InsecureProfileTest, donde esta respuesta sí trae el stacktrace.
		HttpResponse<String> response = client.getAs(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD, "/secure/register");

		assertThat(response.statusCode()).isEqualTo(405);
		assertThat(response.body()).doesNotContain("\"trace\"", "\"message\"", "HttpRequestMethodNotSupportedException");
	}

	@Test
	void anyOtherPathRequiresAuthentication() throws Exception {
		assertThat(client.getAnonymous("/anything-else").statusCode()).isEqualTo(401);
	}

}
