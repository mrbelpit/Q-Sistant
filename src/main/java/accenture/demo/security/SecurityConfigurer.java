package accenture.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled=true)
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

  private final CustomUserDetailService customUserDetailService;

  private final JwtRequestFilter jwtRequestFilter;

  @Autowired
  public SecurityConfigurer(
          CustomUserDetailService customUserDetailService, JwtRequestFilter jwtRequestFilter) {
    this.customUserDetailService = customUserDetailService;
    this.jwtRequestFilter = jwtRequestFilter;
  }


  @Override
  protected void configure(AuthenticationManagerBuilder auth)
          throws Exception {
    auth.userDetailsService(customUserDetailService);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/", "/login", "/register",
                    "/office/entry/**", "/office/entry**",
                    "/office/exit**","/office/exit/**",
                    "/office/entry/","/office/exit/",
                    "/swagger-resources/**",
                    "/swagger-ui.html",
                    "/v2/api-docs",
                    "/webjars/**")
            .permitAll()
            .antMatchers("/admin/","/admin/**").hasAnyRole("ADMIN")
            .anyRequest()
            .authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS);
    http.addFilterBefore(jwtRequestFilter,
            UsernamePasswordAuthenticationFilter.class);
  }

  @Autowired
  public void configAuthentication(AuthenticationManagerBuilder auth)
          throws Exception {
    auth.userDetailsService(customUserDetailService);
  }


  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean()
          throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}