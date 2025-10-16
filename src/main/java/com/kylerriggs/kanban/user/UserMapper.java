package com.kylerriggs.kanban.user;

import com.kylerriggs.kanban.user.dto.UserDto;
import com.kylerriggs.kanban.user.dto.UserSummaryDto;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserMapper {
    private static final String AUTH0_ACTION_CLAIMS_NAMESPACE = "https://kanban.kylerriggs.com/";

    public User mapUserFromToken(Jwt jwt) {
        User user = new User();

        user.setId(jwt.getClaimAsString("sub"));

        String username = jwt.getClaimAsString(AUTH0_ACTION_CLAIMS_NAMESPACE + "username");

        if (username == null) {
            username = jwt.getClaimAsString(AUTH0_ACTION_CLAIMS_NAMESPACE + "nickname");
        }

        user.setUsername(username);

        user.setEmail(jwt.getClaimAsString(AUTH0_ACTION_CLAIMS_NAMESPACE + "email"));

        user.setProfileImageUrl(jwt.getClaimAsString(AUTH0_ACTION_CLAIMS_NAMESPACE + "picture"));

        return user;
    }

    public void updateUserFromToken(User user, Jwt jwt) {
        String username = jwt.getClaimAsString(AUTH0_ACTION_CLAIMS_NAMESPACE + "nickname");
        if (StringUtils.hasText(username) && !username.equals(user.getUsername())) {
            user.setUsername(username);
        }

        String email = jwt.getClaimAsString(AUTH0_ACTION_CLAIMS_NAMESPACE + "email");
        if (StringUtils.hasText(email) && !email.equals(user.getEmail())) {
            user.setEmail(email);
        }

        String profileImageUrl = jwt.getClaimAsString(AUTH0_ACTION_CLAIMS_NAMESPACE + "picture");
        if (StringUtils.hasText(profileImageUrl) && !profileImageUrl.equals(user.getProfileImageUrl())) {
            user.setProfileImageUrl(profileImageUrl);
        }
    }

    public UserSummaryDto toSummaryDto(User user) {
        return new UserSummaryDto(
                user.getId(),
                user.getUsername(),
                user.getProfileImageUrl()
        );
    }

    public UserDto toUserDto(User user) {
        String defaultProjectId = user.getDefaultProject() != null ? user.getDefaultProject().getId().toString() : null;
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageUrl(),
                defaultProjectId
        );
    }

    public User toEntity(String email, String username, String profileImageUrl) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setProfileImageUrl(profileImageUrl);
        return user;
    }
}
