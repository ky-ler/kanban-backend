package com.kylerriggs.kanban.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

        User user = userMapper.mapUserFromToken(token);
        if (!StringUtils.hasText(user.getEmail())) {
            return;
        }

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            userMapper.updateUserFromToken(optionalUser.get(), token);
        } else {
            log.info("Creating new user with email: {}", user.getEmail());
            userRepository.save(user);
        }
    }

    private Optional<String> getUserEmail(final Jwt token) {
        Map<String, Object> claims = token.getClaims();

        if (claims.containsKey("email")) {
            return Optional.of((String) claims.get("email"));
        }

        return Optional.empty();
    }
}
