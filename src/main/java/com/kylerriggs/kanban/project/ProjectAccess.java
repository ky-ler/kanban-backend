package com.kylerriggs.kanban.project;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("projectAccess")
@AllArgsConstructor
@Slf4j
public class ProjectAccess {
    private final ProjectUserRepository projectUserRepository;

    public boolean canView(Long projectId) {
        String requestUserId = currentUserId();
        return projectUserRepository.existsByProjectIdAndUserId(projectId, requestUserId);
    }

    public boolean canModify(Long projectId) {
        String requestUserId = currentUserId();

        return projectUserRepository.existsByProjectIdAndUserIdAndRole(
                projectId, requestUserId, ProjectRole.ADMIN
        );
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        return auth.getName();
    }
}
