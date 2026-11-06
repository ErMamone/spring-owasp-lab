package dev.owasplab.common;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByIdAndOwnerUsername(Long id, String username);

	List<Order> findByOwnerUsernameAndProduct(String username, String product);

	List<Order> findByOwnerUsername(String username);

}
