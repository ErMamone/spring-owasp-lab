package dev.owasplab.secure;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * A10. El detalle se queda en el servidor y el cliente recibe un mensaje genérico con un correlation id, que permite a
 * soporte encontrar el stacktrace sin exponerlo.
 * <p>
 * Heredar de ResponseEntityExceptionHandler mantiene los 4xx de Spring MVC (validación, tipo de contenido, etc.) como
 * ProblemDetail, en vez de convertirlos en 500. También evita que un body inválido llegue a
 * DefaultHandlerExceptionResolver, que loguea los valores rechazados: incluido el password en texto plano.
 */
@RestControllerAdvice(basePackages = "dev.owasplab.secure")
public class SecureExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(SecureExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	ProblemDetail handleUnexpected(Exception ex) {
		String correlationId = UUID.randomUUID().toString();
		log.error("Unhandled exception, correlationId={}", correlationId, ex);
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred.");
		problem.setProperty("correlationId", correlationId);
		return problem;
	}

}
