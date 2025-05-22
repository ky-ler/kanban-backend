package com.kylerriggs.kanban.user;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserMapper {
    public User fromTokenAttributes(Map<String, Object> claims) {
        User user = new User();

        if (claims.containsKey("sub")) {
            user.setId(claims.get("sub").toString());
        }

        if (claims.containsKey("username")) {
            user.setUsername(claims.get("username").toString());
        }

        if (claims.containsKey("email")) {
            user.setEmail(claims.get("email").toString());
        }

        return user;
    }
}
