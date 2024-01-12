package com.alkl1m.taskmanager.service.jwt;

import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.UserRole;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.util.JwtUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public JwtUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findFirstByEmail(email);
        if (userOptional.isEmpty()) throw new UsernameNotFoundException("User not found", null);
        User user = userOptional.get();
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority(UserRole.CLIENT.name()));
        return new JwtUserDetails(user.getId(), user.getName(), user.getPassword(), user.getEmail(), grantedAuthorityList);
    }
}
