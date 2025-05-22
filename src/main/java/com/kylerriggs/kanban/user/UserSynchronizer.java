package com.kylerriggs.kanban.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void syncWithIdp(Jwt token) {
        log.info("Synchronizing user with idp");

        getUserEmail(token).ifPresent(userEmail -> {
            log.info("Getting user email");
            Optional<User> optionalUser = userRepository.findByEmail(userEmail);
            User user = userMapper.fromTokenAttributes(token.getClaims());
            optionalUser.ifPresent(value -> user.setId(optionalUser.get().getId()));

            userRepository.save(user);
        });
    }

    private Optional<String> getUserEmail(final Jwt token) {
        Map<String, Object> claims = token.getClaims();

        if (claims.containsKey("email")) {
            return Optional.of((String) claims.get("email"));
        }

        return Optional.empty();
    }
}
