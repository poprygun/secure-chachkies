package io.microsamples.security.securechachkies;

import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootApplication
public class SecureChachkiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureChachkiesApplication.class, args);
	}

}

@RestController
class ChachkiesController {

	private EasyRandom easyRandom = new EasyRandom();

	@GetMapping("/hello")
	public Map<String, String> hello(final @AuthenticationPrincipal Jwt jwt) {
		System.out.println("headers:\n" + jwt.getHeaders());
		System.out.println("\nclaims:\n" + jwt.getClaims());
		return Collections.singletonMap("message", "Hello " + jwt.getClaimAsString("name"));
	}

	@GetMapping(value = "/api/chachkies", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<Chachkie>> chachkies(){
		return ResponseEntity.ok(easyRandom.objects(Chachkie.class, 13).collect(Collectors.toList()));
	}
}

@Data
class Chachkie{
	private UUID id;
	private Double latitude;
	private Double longitude;
}