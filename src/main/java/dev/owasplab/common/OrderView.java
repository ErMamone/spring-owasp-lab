package dev.owasplab.common;

import java.math.BigDecimal;

/** Lo que ve el cliente de un pedido. Evita serializar la entidad, que arrastraría el hash del password del dueño. */
public record OrderView(Long id, String owner, String product, BigDecimal total) {

	public static OrderView from(Order order) {
		return new OrderView(order.getId(), order.getOwner().getUsername(), order.getProduct(), order.getTotal());
	}

}
