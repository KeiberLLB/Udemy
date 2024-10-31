package org.keiber.test.springboot.app;

// import org.springdoc.core.models.GroupedOpenApi;
// import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Api Test Mockito and JUnit, SpringBoot HibernateJpa", version = "1.0", description = "Api documentation"))
public class OpenApiConfig {

  // Ctrl + k luego Ctrl + c para comentar en bloque
  // @Bean
  // GroupedOpenApi publicApi() {
  //   return GroupedOpenApi.builder()
  //       .group("springshop-public")
  //       .pathsToMatch("/public/**")
  //       .build();
  // }
}
