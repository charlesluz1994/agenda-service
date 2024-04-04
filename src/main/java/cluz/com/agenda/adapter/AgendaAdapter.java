package cluz.com.agenda.adapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class AgendaAdapter extends WebMvcConfigurationSupport {
    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver pageableHandlerResolver = new PageableHandlerMethodArgumentResolver();
        pageableHandlerResolver.setFallbackPageable(PageRequest.of(0, 5));
        argumentResolvers.add(pageableHandlerResolver);
    }
}
