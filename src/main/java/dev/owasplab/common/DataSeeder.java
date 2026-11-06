package dev.owasplab.common;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Usuarios semilla: alice y bob, con 2 pedidos cada uno.
 * Los passwords son públicos a propósito (es un lab sobre H2 en memoria que no se deploya), pero igual se guardan
 * hasheados: la autenticación real no depende de los casos vulnerables.
 */
@Component
public class DataSeeder implements CommandLineRunner {

	public static final String ALICE = "alice";

	public static final String ALICE_LAB_PASSWORD = "alice-lab-pass";

	public static final String BOB = "bob";

	public static final String BOB_LAB_PASSWORD = "bob-lab-pass";

	private final UserRepository users;

	private final OrderRepository orders;

	private final PasswordEncoder passwordEncoder;

	public DataSeeder(UserRepository users, OrderRepository orders, PasswordEncoder passwordEncoder) {
		this.users = users;
		this.orders = orders;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		User alice = users.save(new User(ALICE, passwordEncoder.encode(ALICE_LAB_PASSWORD)));
		User bob = users.save(new User(BOB, passwordEncoder.encode(BOB_LAB_PASSWORD)));
		orders.save(new Order(alice, "Laptop", new BigDecimal("1500.00")));
		orders.save(new Order(alice, "Mouse", new BigDecimal("25.00")));
		orders.save(new Order(bob, "Keyboard", new BigDecimal("80.00")));
		orders.save(new Order(bob, "Monitor", new BigDecimal("300.00")));
	}

}
