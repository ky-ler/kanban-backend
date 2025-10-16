package com.kylerriggs.kanban.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.kylerriggs.kanban.common.BaseAccess;

@Component("userAccess")
@AllArgsConstructor
@Slf4j
public class UserAccess extends BaseAccess {
    private final UserRepository userRepository;

    public boolean canModify(String userId) {
        String requestUserId = currentUserId();
        return requestUserId.equals(userId) && userRepository.existsById(userId);
    }
}
