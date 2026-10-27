package pe.edu.agrosierra.agroapi_Baldeon.seguridad;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class TokenJwtService {

    public static final Duration VIGENCIA = Duration.ofHours(1);

    private final JwtEncoder jwtEncoder;
    private final Clock clock;
    private final String issuer;

    public TokenJwtService(
            JwtEncoder jwtEncoder,
            Clock clock,
            @Value("${agrosierra.security.jwt.issuer}") String issuer) {
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
        this.issuer = issuer;
    }

    public String emitir(Usuario usuario, List<String> roles, List<String> privilegios) {
        Instant ahora = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(ahora)
                .expiresAt(ahora.plus(VIGENCIA))
                .subject(usuario.getId().toString())
                .claim("username", usuario.getUsername())
                .claim("nombre", usuario.getNombre())
                .claim("roles", roles)
                .claim("privilegios", privilegios)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
