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

        if (claims.containsKey("preferred_username")) {
            user.setUsername(claims.get("preferred_username").toString());
        }

        if (claims.containsKey("email")) {
            user.setEmail(claims.get("email").toString());
        }

        if (claims.containsKey("given_name")) {
            user.setFirstName(claims.get("given_name").toString());
        }

        if (claims.containsKey("family_name")) {
            user.setLastName(claims.get("family_name").toString());
        }

        return user;
    }
}
