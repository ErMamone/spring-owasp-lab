package dev.owasplab.vulnerable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.owasplab.common.BrokenReportQuery;

@RestController
public class VulnerableErrorController {

	private final BrokenReportQuery brokenReportQuery;

	public VulnerableErrorController(BrokenReportQuery brokenReportQuery) {
		this.brokenReportQuery = brokenReportQuery;
	}

	@GetMapping("/vuln/errors/db")
	void forceDatabaseError() {
		brokenReportQuery.run();
	}

}
