package dev.owasplab.vulnerable;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.owasplab.common.OrderRepository;
import dev.owasplab.common.OrderView;

@RestController
@RequestMapping("/vuln/orders")
public class VulnerableOrderController {

	private final OrderRepository orders;

	private final JdbcTemplate jdbc;

	public VulnerableOrderController(OrderRepository orders, JdbcTemplate jdbc) {
		this.orders = orders;
		this.jdbc = jdbc;
	}

	/**
	 * A05 SQL injection. INSEGURO a propósito: el input se concatena al SQL, así que el motor no distingue dato de
	 * código. Con {@code x' OR '1'='1} el WHERE pasa a ser {@code (dueño AND producto) OR true}, porque AND tiene mayor
	 * precedencia que OR, y el filtro por dueño deja de aplicar.
	 */
	@GetMapping("/search")
	List<OrderView> search(@RequestParam String product, Principal principal) {
		String sql = "SELECT o.id, u.username, o.product, o.total FROM orders o JOIN app_users u ON u.id = o.owner_id "
				+ "WHERE u.username = '" + principal.getName() + "' AND o.product = '" + product + "'";
		return jdbc.query(sql, (rs, rowNum) -> new OrderView(rs.getLong("id"), rs.getString("username"),
				rs.getString("product"), rs.getBigDecimal("total")));
	}

	/**
	 * A01 IDOR. INSEGURO a propósito: comprueba quién sos (autenticación) pero no si el pedido es tuyo (autorización).
	 * Como los ids son secuenciales, cualquier usuario puede recorrer los pedidos de todos.
	 */
	@GetMapping("/{id}")
	ResponseEntity<OrderView> byId(@PathVariable Long id) {
		return orders.findById(id)
			.map(OrderView::from)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

}
