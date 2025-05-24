package com.kylerriggs.kanban.project;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUserId> {
    boolean existsByProjectIdAndUserId(Long projectId, String userId);
    boolean existsByProjectIdAndUserIdAndRole(Long projectId, String userId, ProjectRole role);
}
