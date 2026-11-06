package dev.owasplab.a01;

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
import dev.owasplab.common.OrderRepository;

@SpringBootTest
@AutoConfigureMockMvc
class IdorTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private OrderRepository orders;

	private Long aliceOrderId() {
		return orders.findByOwnerUsername(DataSeeder.ALICE).getFirst().getId();
	}

	@Test
	void exploitWorksOnVulnerableEndpoint() throws Exception {
		mvc.perform(get("/vuln/orders/{id}", aliceOrderId()).with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.owner").value(DataSeeder.ALICE));
	}

	@Test
	void exploitIsBlockedOnSecureEndpoint() throws Exception {
		mvc.perform(get("/secure/orders/{id}", aliceOrderId()).with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isNotFound());
	}

	@Test
	void foreignOrderIsIndistinguishableFromMissingOrder() throws Exception {
		mvc.perform(get("/secure/orders/{id}", 999_999).with(httpBasic(DataSeeder.BOB, DataSeeder.BOB_LAB_PASSWORD)))
			.andExpect(status().isNotFound());
	}

	// Control positivo: el 404 de arriba no vale nada si el endpoint seguro le da 404 a todo el mundo.
	@Test
	void ownerCanReadOwnOrderOnSecureEndpoint() throws Exception {
		mvc.perform(get("/secure/orders/{id}", aliceOrderId()).with(httpBasic(DataSeeder.ALICE, DataSeeder.ALICE_LAB_PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.owner").value(DataSeeder.ALICE));
	}

	@Test
	void bothEndpointsRequireAuthentication() throws Exception {
		mvc.perform(get("/vuln/orders/{id}", aliceOrderId())).andExpect(status().isUnauthorized());
		mvc.perform(get("/secure/orders/{id}", aliceOrderId())).andExpect(status().isUnauthorized());
	}

}
