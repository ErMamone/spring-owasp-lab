package dev.owasplab.vulnerable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.owasplab.common.OrderRepository;
import dev.owasplab.common.OrderView;

@RestController
@RequestMapping("/vuln/orders")
public class VulnerableOrderController {

	private final OrderRepository orders;

	public VulnerableOrderController(OrderRepository orders) {
		this.orders = orders;
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
