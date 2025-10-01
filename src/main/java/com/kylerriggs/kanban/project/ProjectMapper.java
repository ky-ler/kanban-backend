package com.kylerriggs.kanban.project;

import com.kylerriggs.kanban.issue.dto.IssueSummaryDto;
import com.kylerriggs.kanban.project.dto.CollaboratorDto;
import com.kylerriggs.kanban.project.dto.ProjectDto;
import com.kylerriggs.kanban.project.dto.ProjectSummary;
import com.kylerriggs.kanban.user.dto.UserSummaryDto;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProjectMapper {
    public ProjectDto toDto(Project project) {
        UserSummaryDto creatorSummary = new UserSummaryDto(
                project.getCreatedBy().getId(),
                project.getCreatedBy().getUsername(),
                project.getCreatedBy().getEmail(),
                project.getCreatedBy().getFirstName(),
                project.getCreatedBy().getLastName()
        );

        CollaboratorDto[] collaborators = project.getCollaborators().stream()
                .map(c -> new CollaboratorDto(
                        new UserSummaryDto(
                                c.getUser().getId(),
                                c.getUser().getUsername(),
                                c.getUser().getEmail(),
                                c.getUser().getFirstName(),
                                c.getUser().getLastName()
                        ),
                        c.getRole()
                ))
                .toArray(CollaboratorDto[]::new);

        IssueSummaryDto[] issues = project.getIssues().stream()
                .map(i -> new IssueSummaryDto(
                        i.getId(),
                        i.getTitle(),
                        i.getStatus(),
                        i.getPriority(),
                        i.getAssignedTo() != null ? i.getAssignedTo().getUsername() : null
                ))
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
