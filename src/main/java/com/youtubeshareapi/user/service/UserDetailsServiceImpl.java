package com.youtubeshareapi.user.service;

import com.youtubeshareapi.user.entity.User;
import com.youtubeshareapi.user.entity.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
    List<String> roles = List.of(user.getUserRole());
    return org.springframework.security.core.userdetails.User.builder()
        .username(email)
        .password(user.getPassword())
        .roles(roles.toArray(new String[0]))
        .build();
  }
}
