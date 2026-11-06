package dev.owasplab.common;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Falla siempre con una excepción de DB real. Es la misma falla en vuln y secure: solo cambia cómo se le responde al cliente (A10). */
@Component
public class BrokenReportQuery {

	private final JdbcTemplate jdbc;

	public BrokenReportQuery(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void run() {
		jdbc.queryForList("SELECT secret_column FROM internal_reports");
	}

}
