package cn.edu.shu.pourrfot.server.config;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
    "/swagger-ui/**"
    // other public endpoints of your API may be appended to this array
  };
  @Autowired
  private Environment environment;

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
    http.cors()
      .and()
      .csrf()
      .and()
      .addFilter(new JwtAuthorizationFilter(authenticationManager(), environment))
      .authorizeRequests()
      // permit Swagger UI resources
      .antMatchers(AUTH_WHITELIST)
      .permitAll()
      // Do not add content-path as uri prefix
      .antMatchers(HttpMethod.GET, "/courses/**")
      .hasAnyAuthority(RoleEnum.ALL_ROLE_VALUES.toArray(new String[]{}))
      .antMatchers(HttpMethod.GET, "/courses/*/groups/**")
      .hasAnyAuthority(RoleEnum.ALL_ROLE_VALUES.toArray(new String[]{}))
      .anyRequest()
      .authenticated();
  }
}
