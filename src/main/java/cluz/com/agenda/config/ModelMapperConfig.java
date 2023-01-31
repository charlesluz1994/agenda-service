package cluz.com.agenda.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
	//Always that be necessary use ModelMapper, Spring will provide the injection dependence automatically.
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
