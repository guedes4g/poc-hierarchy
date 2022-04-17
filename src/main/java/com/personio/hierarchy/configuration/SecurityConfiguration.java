package com.personio.hierarchy.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final String[] AUTH_WHITELIST = {
    "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/**", "/webjars/**"
  };

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(AUTH_WHITELIST)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2ResourceServer()
        .jwt();
  }
}
