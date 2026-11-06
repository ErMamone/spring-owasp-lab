package dev.owasplab.a02;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Cliente HTTP contra el servidor real. MockMvc no hace el ERROR dispatch hacia /error, justo donde se decide si la
 * respuesta lleva stacktrace, así que no serviría para probar este caso.
 */
final class RealServerClient {

	private final HttpClient client = HttpClient.newHttpClient();

	private final int port;

	RealServerClient(int port) {
		this.port = port;
	}

	HttpResponse<String> getAnonymous(String path) throws IOException, InterruptedException {
		return send(builder(path).build());
	}

	HttpResponse<String> getAs(String username, String password, String path) throws IOException, InterruptedException {
		String credentials = Base64.getEncoder()
			.encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
		return send(builder(path).header("Authorization", "Basic " + credentials).build());
	}

	private HttpRequest.Builder builder(String path) {
		return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).header("Accept", "application/json");
	}

	private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

}
