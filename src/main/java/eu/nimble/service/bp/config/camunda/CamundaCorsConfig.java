package eu.nimble.service.bp.config.camunda;

import org.camunda.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.ws.rs.ApplicationPath;

/**
 * Created by Johannes Innerbichler on 11/07/17.
 */
@Component
@Configuration
@ApplicationPath("/rest")
public class CamundaCorsConfig extends CamundaJerseyResourceConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${nimble.cors_enabled}")
    private String corsEnabled;

    @Override
    protected void registerAdditionalResources() {
        if( corsEnabled.equals("true"))
            register(this);
            logger.info("Enabled cors...");
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}