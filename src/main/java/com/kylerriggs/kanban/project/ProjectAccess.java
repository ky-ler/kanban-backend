package com.kylerriggs.kanban.project;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.kylerriggs.kanban.common.BaseAccess;

@Component("projectAccess")
@AllArgsConstructor
@Slf4j
public class ProjectAccess extends BaseAccess {
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
}
