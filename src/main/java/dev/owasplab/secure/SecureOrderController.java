package dev.owasplab.secure;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.owasplab.common.OrderRepository;
import dev.owasplab.common.OrderView;

@RestController
@RequestMapping("/secure/orders")
public class SecureOrderController {

	private final OrderRepository orders;

	public SecureOrderController(OrderRepository orders) {
		this.orders = orders;
	}

	/**
	 * A01. El dueño es parte de la consulta, así que no hay forma de olvidarse de chequearlo después.
	 * Un pedido ajeno y uno inexistente dan el mismo 404: un 403 le confirmaría al atacante que el id existe.
	 */
	@GetMapping("/{id}")
	ResponseEntity<OrderView> byId(@PathVariable Long id, Principal principal) {
		return orders.findByIdAndOwnerUsername(id, principal.getName())
			.map(OrderView::from)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

}
