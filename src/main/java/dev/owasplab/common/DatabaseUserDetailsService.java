package dev.owasplab.common;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Conecta HTTP Basic con la tabla app_users. Spring Security lo toma solo por ser el único UserDetailsService. */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

	private final UserRepository users;

	public DatabaseUserDetailsService(UserRepository users) {
		this.users = users;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return users.findByUsername(username)
			.map((user) -> org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
				.password(user.getPassword())
				.roles("USER")
				.build())
			.orElseThrow(() -> new UsernameNotFoundException("Unknown user"));
	}

}
