package com.kylerriggs.kanban.project;

import com.kylerriggs.kanban.issue.IssueMapper;
import com.kylerriggs.kanban.issue.dto.IssueSummaryDto;
import com.kylerriggs.kanban.project.dto.CollaboratorDto;
import com.kylerriggs.kanban.project.dto.ProjectDto;
import com.kylerriggs.kanban.project.dto.ProjectSummary;
import com.kylerriggs.kanban.user.UserMapper;
import com.kylerriggs.kanban.user.dto.UserSummaryDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@AllArgsConstructor
@Service
public class ProjectMapper {
    private final UserMapper userMapper;
    private final IssueMapper issueMapper;

    public ProjectDto toDto(Project project) {
        UserSummaryDto creatorSummary = userMapper.toDto(project.getCreatedBy());

        CollaboratorDto[] collaborators = project.getCollaborators().stream()
                .map(c -> new CollaboratorDto(
                        userMapper.toDto(c.getUser()),
                        c.getRole()
                ))
                .toArray(CollaboratorDto[]::new);

        IssueSummaryDto[] issues = project.getIssues().stream()
                .map(issueMapper::toIssueSummaryDto)
                .toArray(IssueSummaryDto[]::new);

        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                creatorSummary,
                collaborators,
                issues,
                project.getDateCreated().toString(),
                project.getDateModified().toString()
        );
    }

    public ProjectSummary toSummaryDto(Project project) {
        int doneIssues = (int) project.getIssues().stream()
                .filter(issue -> Objects.equals(issue.getStatus().getName(), "Done"))
                .count();

        return new ProjectSummary(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getDateModified().toString(),
                doneIssues,
                project.getIssues().size());
    }
}
