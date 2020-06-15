package accenture.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private CustomUserDetailService userDetailsService;

  @Autowired
  private JwtUtility jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
          throws ServletException, IOException {
    final String authorizationHeader = request.getHeader(
            SecurityConstants.HEADER_STRING);
    String email = null;
    String jwtToken = null;

    if (authorizationHeader != null && authorizationHeader.startsWith(
            SecurityConstants.TOKEN_PREFIX)) {
      jwtToken = authorizationHeader.substring(7);
      email = jwtUtil.extractEmailAddress(jwtToken);
    }
    if (email != null
        && SecurityContextHolder.getContext().getAuthentication()
           == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
      if (jwtUtil.validateToken(jwtToken, userDetails)) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(
                        request));
        SecurityContextHolder.getContext().setAuthentication(
                usernamePasswordAuthenticationToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}