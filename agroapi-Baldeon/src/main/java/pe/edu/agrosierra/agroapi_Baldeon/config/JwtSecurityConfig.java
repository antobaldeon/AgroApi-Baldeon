package pe.edu.agrosierra.agroapi_Baldeon.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import pe.edu.agrosierra.agroapi_Baldeon.seguridad.CodigosPrivilegio;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;

@Configuration
@EnableMethodSecurity
public class JwtSecurityConfig {

    private final byte[] secretBytes;
    private final String issuer;

    public JwtSecurityConfig(
            @Value("${agrosierra.security.jwt.secret:}") String secret,
            @Value("${agrosierra.security.jwt.issuer}") String issuer) {
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.issuer = issuer;
        if (secretBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET debe contener al menos 32 bytes. Configura una clave local segura.");
        }
    }

    @Bean
    public SecretKey jwtSigningKey() {
        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSigningKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSigningKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSigningKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSigningKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }

    @Bean
    public Clock relojUtc() {
        return Clock.systemUTC();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("privilegios");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter,
            ApiSecurityErrorHandler apiSecurityErrorHandler) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/lotes/*/labores", "/api/v1/lotes/*/labores/**")
                        .hasAuthority(CodigosPrivilegio.LABOR_LEER)
                        .requestMatchers(HttpMethod.GET, "/api/v1/lotes", "/api/v1/lotes/**")
                        .hasAuthority(CodigosPrivilegio.LOTE_LEER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/labores")
                        .hasAuthority(CodigosPrivilegio.LABOR_CREAR)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/labores/*", "/api/v1/labores/**")
                        .hasAuthority(CodigosPrivilegio.LABOR_EDITAR)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/labores/*", "/api/v1/labores/**")
                        .hasAuthority(CodigosPrivilegio.LABOR_ELIMINAR)
                        .requestMatchers(HttpMethod.POST, "/api/v1/lotes/*/cerrar-campania")
                        .hasAuthority(CodigosPrivilegio.CAMPANIA_CERRAR)
                        .requestMatchers("/api/v1/usuarios/me").authenticated()
                        .requestMatchers("/api/v1/usuarios/**")
                        .hasAuthority(CodigosPrivilegio.USUARIO_GESTIONAR)
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(apiSecurityErrorHandler)
                        .accessDeniedHandler(apiSecurityErrorHandler))
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .authenticationEntryPoint(apiSecurityErrorHandler)
                        .accessDeniedHandler(apiSecurityErrorHandler)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }
}
