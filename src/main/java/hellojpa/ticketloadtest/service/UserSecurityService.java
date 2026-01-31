package hellojpa.ticketloadtest.service;

import hellojpa.ticketloadtest.domain.User;
import hellojpa.ticketloadtest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. 이메일로 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // 2. Spring Security가 이해할 수 있는 UserDetails 객체로 변환하여 반환
        // 비밀번호 검증은 Spring Security가 이 객체의 비밀번호(DB값)와 사용자가 입력한 비밀번호를 비교하여 자동으로 수행함
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER") // 권한 설정 (임시로 USER 권한 부여)
                .build();
    }
}
