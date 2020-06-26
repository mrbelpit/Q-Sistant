package accenture.demo.security;

import accenture.demo.user.AppUser;
import accenture.demo.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CurrentSecurityUser implements UserDetails {
  String ROLE_PREFIX = "ROLE_";

  String userName;
  String password;
  UserRole role;

  public CurrentSecurityUser(String email, String password, UserRole role) {
    this.userName = email;
    this.password = password;
    this.role = role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

    list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role));

    return list;
  }

  public static CurrentSecurityUser create(AppUser entity) {
    return new CurrentSecurityUser(entity.getEmail(), entity.getPassword(), entity.getUserRole());
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.userName;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}