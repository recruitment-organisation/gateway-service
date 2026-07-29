package recruitment.dev.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import recruitment.dev.gatewayservice.security.JwtAuthConverter;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatewayServiceApplicationTests {

	@Test
	void convertsScopesAndKeycloakRolesToAuthorities() {
		Jwt jwt = Jwt.withTokenValue("token")
				.header("alg", "none")
				.claim("scope", "read")
				.claim("preferred_username", "ada")
				.claim("realm_access", Map.of("roles", List.of("ADMIN", "RECRUITER")))
				.build();

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) new JwtAuthConverter()
				.convert(jwt)
				.block();

		assertEquals("ada", authentication.getName());
		assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("SCOPE_read")));
		assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_RECRUITER")));
	}

	@Test
	void convertsJwtWithoutRealmRoles() {
		Jwt jwt = Jwt.withTokenValue("token")
				.header("alg", "none")
				.claim("preferred_username", "candidate")
				.build();

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) new JwtAuthConverter()
				.convert(jwt)
				.block();

		assertEquals("candidate", authentication.getName());
		assertTrue(authentication.getAuthorities().isEmpty());
	}

}
