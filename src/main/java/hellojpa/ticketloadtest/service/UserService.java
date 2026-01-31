package hellojpa.ticketloadtest.service;

import hellojpa.ticketloadtest.domain.User;
import hellojpa.ticketloadtest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(String name, String email, String password) {
        validateDuplicateUser(email);
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(name, email, encodedPassword);
        userRepository.save(user);
        return user.getId();
    }

    private void validateDuplicateUser(String email) {
        userRepository.findByEmail(email)
                .ifPresent(u -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }
}
