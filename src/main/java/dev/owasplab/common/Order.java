package dev.owasplab.common;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
// "order" es palabra reservada en SQL.
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@Column(nullable = false)
	private String product;

	@Column(nullable = false)
	private BigDecimal total;

	protected Order() {
	}

	public Order(User owner, String product, BigDecimal total) {
		this.owner = owner;
		this.product = product;
		this.total = total;
	}

	public Long getId() {
		return id;
	}

	public User getOwner() {
		return owner;
	}

	public String getProduct() {
		return product;
	}

	public BigDecimal getTotal() {
		return total;
	}

}
