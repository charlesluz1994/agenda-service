package cluz.com.agenda.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomAuthorizationFilterConfigTest {

	private static final String SECRET = "configured-test-secret";

	@AfterEach
	void clearContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("Given a token signed with the configured secret when filtering then authentication is set")
	void givenTokenSignedWithConfiguredSecret_whenFilter_thenAuthenticationIsSet() throws Exception {
		String token = JWT.create().withSubject("charles").sign(Algorithm.HMAC256(SECRET));

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		new CustomAuthorizationFilterConfig(SECRET).doFilterInternal(request, response, chain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "authentication should be set");
		assertEquals("charles", SecurityContextHolder.getContext().getAuthentication().getName());
		verify(chain).doFilter(request, response);
	}

	@Test
	@DisplayName("Given a token signed with a different secret when filtering then request is forbidden")
	void givenTokenSignedWithWrongSecret_whenFilter_thenForbidden() throws Exception {
		String token = JWT.create().withSubject("charles").sign(Algorithm.HMAC256("some-other-secret"));

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		new CustomAuthorizationFilterConfig(SECRET).doFilterInternal(request, response, chain);

		assertNull(SecurityContextHolder.getContext().getAuthentication(), "authentication must not be set");
		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		verify(chain, never()).doFilter(request, response);
	}
}
