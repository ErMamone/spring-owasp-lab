package dev.owasplab.common;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyUserRepository extends JpaRepository<LegacyUser, Long> {

	Optional<LegacyUser> findByUsername(String username);

}
