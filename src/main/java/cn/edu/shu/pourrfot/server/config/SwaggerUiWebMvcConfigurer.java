package cn.edu.shu.pourrfot.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * @author spencercjh
 */
@EnableSwagger2
@Configuration
public class SwaggerUiWebMvcConfigurer implements WebMvcConfigurer {
  private final String baseUrl;
  private final String webUrl;

  public SwaggerUiWebMvcConfigurer(@Value("${springfox.documentation.swagger-ui.base-url:}") String baseUrl,
                                   @Value("${pourrfot.web.host}") String webUrl) {
    this.baseUrl = baseUrl;
    this.webUrl = webUrl;
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
      .securityContexts(Collections.singletonList(SecurityContext.builder()
        .securityReferences(Collections.singletonList(new SecurityReference("JWT",
          new AuthorizationScope[]{new AuthorizationScope("global", "accessEverything")})))
        .build()))
      .securitySchemes(Collections.singletonList(
        new ApiKey("JWT", HttpHeaders.AUTHORIZATION, "header")))
      .select()
      .apis(RequestHandlerSelectors.any())
      .paths(PathSelectors.any())
      .build();
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    final String baseUrl = StringUtils.trimTrailingCharacter(this.baseUrl, '/');
    registry.addResourceHandler(baseUrl + "/swagger-ui/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
      .resourceChain(false);
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController(baseUrl + "/swagger-ui/")
      .setViewName("forward:" + baseUrl + "/swagger-ui/index.html");
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
      .addMapping("/api")
      .allowedOrigins(webUrl);
  }
}
