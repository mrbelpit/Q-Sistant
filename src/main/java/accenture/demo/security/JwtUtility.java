package accenture.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtUtility {

  public String extractEmailAddress(String token) {
    return extractClaims(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaims(token, Claims::getExpiration);
  }

  public <T> T extractClaims(String token,
                             Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(
            SecurityConstants.SECRET).parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername());
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  private String createToken(Map<String, Object> claims,
                             String subject) {
    return Jwts.builder().setClaims(claims).setSubject(
            subject).setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis()
                                    + SecurityConstants.EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS256,
                    SecurityConstants.SECRET).compact();
  }

  public Boolean validateToken(String token,
                               UserDetails userDetails) {
    final String username = extractEmailAddress(token);
    return (username.equals(userDetails.getUsername())
            && !isTokenExpired(token));
  }

}
