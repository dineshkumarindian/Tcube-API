package com.tcube.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
@SuppressWarnings("deprecation")
@Configuration
@ComponentScan
@EnableWebMvc
public class ApplicationConfig extends WebMvcConfigurerAdapter {
	
    @Value("${app.cors.allowedOrigins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
//    	System.out.println(allowedOrigins[0]);  // Can just allow `methods` that you need.
    	registry.addMapping("/**")
    	.allowedOrigins(allowedOrigins)
    	.allowedMethods("PUT", "GET", "DELETE", "OPTIONS", "PATCH", "POST")
    	.allowedHeaders("*");
   }
}
