package cluz.com.agenda.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI agendaOpenApi() {
		var contact = new Contact();
		contact.setEmail("charlesluz.94@gmail.com");
		contact.setName("Charles Luz");
		contact.setUrl("https://github.com/charlesluz1994/agenda-service");

		var info = new Info();
		info.title("Agenda Service - Spring Boot REST API");
		info.version("1.3.2");
		info.contact(contact);
		info.description("Application for Schedule Appointments");

		var localServer = new Server();
		localServer.setUrl("http://localhost:8087");
		localServer.setDescription("Server URL local environment");

		var devServer = new Server();
		devServer.setUrl("http://dev.com");
		devServer.setDescription("Server URL DEV environment");

		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("bearerAuth",
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
				.info(info)
				.servers(List.of(localServer, devServer));
	}
}