package cn.edu.shu.pourrfot.server.config;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.filter.JwtAuthorizationFilter;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author spencercjh
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private static final String[] AUTH_WHITELIST = {
    // -- Swagger UI v2
    "/v2/api-docs",
    "/swagger-resources",
    "/swagger-resources/**",
    "/configuration/ui",
    "/configuration/security",
    "/swagger-ui.html",
    "/webjars/**",
    // -- Swagger UI v3 (OpenAPI)
    "/v3/api-docs/**",
    "/swagger-ui/**",
    // other public endpoints of your API may be appended to this array
    "/users/create",
    "/users/update/**"
  };
  @Autowired
  private Environment environment;
  @Autowired
  private ObjectMapper objectMapper;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    if (!Boolean.parseBoolean(environment.getProperty("pourrfot.protect"))) {
      http
        .cors().disable()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/**")
        .permitAll();
      return;
    }
    http
      .cors()
      .and()
      .csrf()
      .disable()
      .exceptionHandling()
      .accessDeniedHandler((httpServletRequest, httpServletResponse, e) -> {
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        final PrintWriter writer = httpServletResponse.getWriter();
        writer.append(objectMapper.writeValueAsString(Result.of(HttpStatus.FORBIDDEN,
          "Access denied: " + e.getMessage())));
        writer.flush();
      })
      .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> {
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        final PrintWriter writer = httpServletResponse.getWriter();
        writer.append(objectMapper.writeValueAsString(Result.of(HttpStatus.UNAUTHORIZED,
          "Access denied: " + e.getMessage())));
        writer.flush();
      })
      .and()
      .addFilter(new JwtAuthorizationFilter(authenticationManager(), environment))
      .authorizeRequests()
      // permit Swagger UI resources
      .antMatchers(AUTH_WHITELIST)
      .permitAll()
      // Do not add content-path as uri prefix
      .antMatchers("/actuator")
      .hasAuthority(RoleEnum.admin.getValue())
      .antMatchers(HttpMethod.GET, "/**")
      .hasAnyAuthority(RoleEnum.ALL_ROLE_VALUES.toArray(new String[]{}))
      .antMatchers("/files/**")
      .hasAnyAuthority(RoleEnum.ALL_ROLE_VALUES.toArray(new String[]{}))
      .antMatchers("/messages/**", "/inbox-messages/**")
      .hasAnyAuthority(RoleEnum.ALL_ROLE_VALUES.toArray(new String[]{}))
      .antMatchers("/transactions/**")
      .hasAnyAuthority(RoleEnum.ALL_ROLE_VALUES.toArray(new String[]{}))
      .antMatchers("/**")
      .hasAnyAuthority(RoleEnum.admin.getValue())
      .anyRequest()
      .authenticated()
      .and()
      .rememberMe();
  }
}
