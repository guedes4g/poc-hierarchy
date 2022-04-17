package com.personio.hierarchy.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Hierarchy API", version = "1.0", description = "Hierarchy Information"))
@SecurityScheme(
    name = "openApi",
    type = SecuritySchemeType.OPENIDCONNECT,
    openIdConnectUrl = "${springdoc.oAuthFlow.wellknown}")
public class SwaggerConfiguration {}
