package accenture.demo.security;

import accenture.demo.user.AppUser;
import accenture.demo.user.UserRepository;
import accenture.demo.user.UserRole;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailService implements UserDetailsService {

  @Autowired
  UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    AppUser appUser = userRepository.findByEmail(email).orElse(null);
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    String ROLE_PREFIX = "ROLE_";

    UserRole userRole = appUser.getUserRole();
    if (userRole == UserRole.ADMIN){
      grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + userRole.toString()));
      grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + UserRole.EMPLOYEE.toString()));
    }else {
      grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + userRole.toString()));
    }

    return new User(appUser.getEmail(), appUser.getPassword(),
            grantedAuthorities);
  }

}
