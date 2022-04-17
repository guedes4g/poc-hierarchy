package com.personio.hierarchy.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerUiWebMvcConfigurer implements WebMvcConfigurer {
  private final String baseUrl = "";

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry
        .addViewController(baseUrl + "/swagger-ui/")
        .setViewName("forward:" + baseUrl + "/swagger-ui/index.html");
  }
}
