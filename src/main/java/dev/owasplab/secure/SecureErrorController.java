package dev.owasplab.secure;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.owasplab.common.BrokenReportQuery;

@RestController
public class SecureErrorController {

	private final BrokenReportQuery brokenReportQuery;

	public SecureErrorController(BrokenReportQuery brokenReportQuery) {
		this.brokenReportQuery = brokenReportQuery;
	}

	@GetMapping("/secure/errors/db")
	void forceDatabaseError() {
		brokenReportQuery.run();
	}

}
