package accenture.demo.security;

public class SecurityConstants {

  public static final String SECRET = System.getenv("JWT_SECRET");
  public static final String TOKEN_PREFIX = System.getenv("TOKEN_PREFIX");
  public static final String HEADER_STRING = System.getenv("TOKEN_HEADER_STRING");
  public static final long EXPIRATION_TIME = Long.parseLong(System.getenv("JWT_TOKEN_EXPIRATION_TIME"));
}
