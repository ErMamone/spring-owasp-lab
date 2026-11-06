package dev.owasplab.a10;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import dev.owasplab.common.DataSeeder;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class ExceptionHandlingTest {

	@Autowired
	private MockMvc mvc;

	@Test
	void vulnerableEndpointLeaksDatabaseDetails() throws Exception {
		String body = mvc.perform(get("/vuln/errors/db").with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isInternalServerError())
			.andReturn().getResponse().getContentAsString();

		assertThat(body).contains("JdbcSQL", "SQL", "internal_reports", "secret_column");
	}

	@Test
	void secureEndpointOnlyReturnsGenericMessageAndCorrelationId(CapturedOutput output) throws Exception {
		String body = mvc.perform(get("/secure/errors/db").with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isInternalServerError())
			.andReturn().getResponse().getContentAsString();

		assertThat(body).doesNotContain("SQL", "Jdbc", "internal_reports", "secret_column", "Exception");
		assertThat(JsonPath.<String>read(body, "$.detail")).isEqualTo("An unexpected error occurred.");

		// El detalle no se pierde: queda en el log del servidor, localizable por el id que recibió el cliente.
		String correlationId = JsonPath.read(body, "$.correlationId");
		assertThat(correlationId).isNotBlank();
		assertThat(output).contains("correlationId=" + correlationId).contains("JdbcSQLSyntaxErrorException");
	}

	// Sin el advice, DefaultHandlerExceptionResolver loguea "rejected value [...]" con el password en texto plano.
	@Test
	void rejectedPasswordIsNeverWrittenToLogs(CapturedOutput output) throws Exception {
		mvc.perform(post("/secure/register").contentType(MediaType.APPLICATION_JSON)
			.content("{\"username\":\"log-check\",\"password\":\"Sh0rt!\"}"))
			.andExpect(status().isBadRequest());

		assertThat(output).doesNotContain("Sh0rt!");
	}

	// El advice atrapa Exception: no tiene que convertir los 4xx de Spring MVC en 500.
	@Test
	void clientErrorsStayClientErrors() throws Exception {
		mvc.perform(get("/secure/orders/not-a-number").with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isBadRequest());
	}

}
