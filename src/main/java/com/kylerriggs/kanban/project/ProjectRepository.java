package com.kylerriggs.kanban.project;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByCollaboratorsUserId(String userId);
}
