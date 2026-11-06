package dev.owasplab.a05;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import dev.owasplab.common.DataSeeder;

@SpringBootTest
@AutoConfigureMockMvc
class SqlInjectionTest {

	private static final String PAYLOAD = "x' OR '1'='1";

	@Autowired
	private MockMvc mvc;

	@Test
	void exploitWorksOnVulnerableEndpoint() throws Exception {
		mvc.perform(get("/vuln/orders/search").param("product", PAYLOAD)
			.with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(4)))
			.andExpect(jsonPath("$[*].product", containsInAnyOrder("Laptop", "Mouse", "Keyboard", "Monitor")));
	}

	@Test
	void exploitIsBlockedOnSecureEndpoint() throws Exception {
		mvc.perform(get("/secure/orders/search").param("product", PAYLOAD)
			.with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
	}

	// Control positivo: el 0 de arriba no vale nada si la búsqueda segura nunca devuelve resultados.
	@Test
	void legitimateSearchStillWorksOnSecureEndpoint() throws Exception {
		mvc.perform(get("/secure/orders/search").param("product", "Keyboard")
			.with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].owner").value(DataSeeder.BOB));
	}

	@Test
	void secureSearchDoesNotReturnOtherUsersOrders() throws Exception {
		mvc.perform(get("/secure/orders/search").param("product", "Laptop")
			.with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
	}

}
