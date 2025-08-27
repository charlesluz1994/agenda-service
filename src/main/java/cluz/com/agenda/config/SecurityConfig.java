package cluz.com.agenda.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Value("${jwt.secret.key}")
	private String jwtSecret;

	private static final String[] AUTH_WHITELIST_DEV = {
			"/login",
			"/actuator/health",
			"/actuator/health/readiness",
			"/actuator/health/liveness",
			"/actuator/prometheus",
			"/actuator/metrics",
			"/v2/api-docs",
			"/swagger-resources/**",
			"/webjars/**",
			"/v3/api-docs/**",
			"/swagger-ui/**"
	};

	private static final String[] AUTH_WHITELIST_PRD = {
			"/login",
			"/actuator/health/readiness",
			"/actuator/health/liveness",
			"/v2/api-docs",
			"/swagger-resources/**",
			"/webjars/**",
			"/v3/api-docs/**",
			"/swagger-ui/**"
	};

	/**
	 * Provider configuration for authentication with  UserDetailsService + BCrypt
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(bCryptPasswordEncoder);
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	// Set filters of login and JWT authorization
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager, Environment env) throws Exception {

		String[] whitelist = env.acceptsProfiles(Profiles.of("prd"))
				? AUTH_WHITELIST_PRD
				: AUTH_WHITELIST_DEV;

		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authz -> authz
						.requestMatchers(whitelist).permitAll()
						.anyRequest().authenticated()
				)
				.authenticationProvider(authenticationProvider())
				.addFilter(getCustomAuthenticationFilterConfig(authManager))
				.addFilterBefore(new CustomAuthorizationFilterConfig(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	private CustomAuthenticationFilterConfig getCustomAuthenticationFilterConfig(AuthenticationManager authManager) {
		CustomAuthenticationFilterConfig customAuthFilter = new CustomAuthenticationFilterConfig(authManager, jwtSecret);
		customAuthFilter.setFilterProcessesUrl("/login");
		return customAuthFilter;
	}
}