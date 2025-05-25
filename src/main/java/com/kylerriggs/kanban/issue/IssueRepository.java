package com.kylerriggs.kanban.issue;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAllByProjectId(Long projectId);
}
