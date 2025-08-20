package com.project.team5backend.global.apiPayload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.project.team5backend.domain.user.user.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.project.team5backend.domain.user.user.entity.User; // User 엔티티 위치에 맞게 수정

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {


    private Long userId;
    private String email;
    private String password;
    private String name; // 사용자 이름

    private List<String> roles;

    // User 엔티티를 CustomUserDetails 객체로 변환
    public static CustomUserDetails fromUser(User user) {
        Role role = (user.getRole() != null) ? user.getRole() : Role.USER;

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                List.of(user.getRole().name()) // User 엔티티에 역할(Role)이 있다고 가정
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email; // 사용자 ID로 사용할 필드를 이메일로 설정
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (기본값 true)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (기본값 true)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부 (기본값 true)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (기본값 true)
    }
}
