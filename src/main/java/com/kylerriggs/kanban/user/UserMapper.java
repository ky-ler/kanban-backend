package com.kylerriggs.kanban.user;

import com.kylerriggs.kanban.user.dto.UserSummaryDto;
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

    public UserSummaryDto toSummary(User user) {
        return new UserSummaryDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public User toEntity(String email, String username, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }


}
