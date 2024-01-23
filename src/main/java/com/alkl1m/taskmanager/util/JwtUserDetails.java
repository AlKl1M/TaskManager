package com.alkl1m.taskmanager.util;

import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.UserRole;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class JwtUserDetails implements UserDetails {
    public Long id;
    private String name;
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(Long id, String name, String password, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }

    public static JwtUserDetails create(User user) {
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority(UserRole.CLIENT.name()));
        return new JwtUserDetails(user.getId(), user.getName(), user.getPassword(), user.getEmail(), grantedAuthorityList);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
