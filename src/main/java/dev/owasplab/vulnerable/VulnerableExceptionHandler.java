package dev.owasplab.vulnerable;

import java.util.Map;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A10. INSEGURO a propósito: le entrega al cliente el mensaje y la clase de la excepción. Eso revela el motor de base de
 * datos, nombres de tablas y columnas y el SQL ejecutado: justo lo que necesita un atacante para afinar una inyección.
 */
@RestControllerAdvice(basePackages = "dev.owasplab.vulnerable")
public class VulnerableExceptionHandler {

	@ExceptionHandler(Exception.class)
	ResponseEntity<Map<String, String>> handle(Exception ex) {
		Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(Map.of("exception", rootCause.getClass().getName(), "message", String.valueOf(ex.getMessage())));
	}

}
