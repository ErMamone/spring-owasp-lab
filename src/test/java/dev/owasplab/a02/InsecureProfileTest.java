package dev.owasplab.a02;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import dev.owasplab.common.DataSeeder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("insecure")
class InsecureProfileTest {

	@LocalServerPort
	private int port;

	private RealServerClient client;

	@BeforeEach
	void setUp() {
		client = new RealServerClient(port);
	}

	@Test
	void actuatorEnvIsOpenToAnonymousUsers() throws Exception {
		HttpResponse<String> response = client.getAnonymous("/actuator/env");

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).contains("propertySources");
	}

	@Test
	void errorsExposeStacktraceAndMessage() throws Exception {
		// /secure/register solo acepta POST: la excepción ocurre antes de elegir controller, así que llega hasta /error.
		HttpResponse<String> response = client.getAs(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD, "/secure/register");

		assertThat(response.statusCode()).isEqualTo(405);
		assertThat(response.body()).contains("\"trace\"", "\"message\"", "HttpRequestMethodNotSupportedException");
	}

}
