package cn.edu.shu.pourrfot.server.filter;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author spencercjh
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private static final String BEARER = "Bearer ";
  private JsonWebKey rsaJsonWebPublicKey;
  private ObjectMapper objectMapper;

  private Environment environment;

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager, Environment environment) {
    super(authenticationManager);
    this.environment = environment;
    init();
  }

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint, Environment environment) {
    super(authenticationManager, authenticationEntryPoint);
    init();
  }

  private void init() {
    this.objectMapper = new ObjectMapper();
    final RestTemplate restTemplate = new RestTemplate();
    final String casHost = environment.getProperty("pourrfot.cas.host");
    while (true) {
      final String publicKeyJson = restTemplate.getForEntity(casHost + "/jwt/public-key", String.class).getBody();
      log.info("Get public key from CAS now");
      try {
        rsaJsonWebPublicKey = JsonWebKey.Factory.newJwk(publicKeyJson);
        break;
      } catch (JoseException e) {
        log.error("Parse public key failed", e);
      }
    }
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws IOException, ServletException {
    final UsernamePasswordAuthenticationToken authentication = parseToken(request);

    if (authentication != null) {
      // Use SecurityContextHolder.getContext().getAuthentication() to get current user info
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } else {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private UsernamePasswordAuthenticationToken parseToken(HttpServletRequest request) {
    final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    log.info("Receive bearer token: {}", bearerToken);
    if (StringUtils.isBlank(bearerToken)) {
      return null;
    }
    if (!bearerToken.startsWith(BEARER)) {
      return null;
    }
    final String token = bearerToken.substring(BEARER.length());
    JwtContext jwt;
    try {
      jwt = new JwtConsumerBuilder()
        .setVerificationKey(rsaJsonWebPublicKey.getKey())
        .setRequireExpirationTime()
        .setAllowedClockSkewInSeconds(30)
        .build()
        .process(token);
    } catch (InvalidJwtException e) {
      log.error("Wrong jwt token: {}", token, e);
      return null;
    }
    final Map<String, Object> claimsMap = jwt.getJwtClaims().getClaimsMap();
    final long expireAt = (long) claimsMap.get("exp");
    if (expireAt <= 0) {
      log.warn("Token has no expire time: {}", claimsMap);
      return null;
    }
    if (expireAt < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) {
      log.warn("Token expire: {}", claimsMap);
      return null;
    }
    final String username = (String) claimsMap.get("username");
    final String role = (String) claimsMap.get("role");
    if (StringUtils.isBlank(username) || StringUtils.isBlank(role)) {
      log.warn("Empty username or role: {}", claimsMap);
      return null;
    }
    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
      List.of(new SimpleGrantedAuthority(RoleEnum.valueOf(role))));
    authentication.setDetails(claimsMap);
    return authentication;
  }
}
