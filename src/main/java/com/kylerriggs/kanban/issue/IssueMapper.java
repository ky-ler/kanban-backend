package com.kylerriggs.kanban.issue;

import com.kylerriggs.kanban.issue.dto.CreateIssueRequest;
import com.kylerriggs.kanban.issue.dto.IssueDto;
import com.kylerriggs.kanban.priority.Priority;
import com.kylerriggs.kanban.project.Project;
import com.kylerriggs.kanban.status.Status;
import com.kylerriggs.kanban.user.User;
import com.kylerriggs.kanban.user.dto.UserSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class IssueMapper {
    public IssueDto toDto(Issue issue) {
        UserSummaryDto createdBy = issue.getCreatedBy() != null ?
                new UserSummaryDto(
                issue.getCreatedBy().getId(),
                issue.getCreatedBy().getUsername(),
                issue.getCreatedBy().getEmail(),
                issue.getCreatedBy().getFirstName(),
                issue.getCreatedBy().getLastName()
        ) : null;

        UserSummaryDto assignedTo = issue.getAssignedTo() != null ?
                new UserSummaryDto(
                        issue.getAssignedTo().getId(),
                        issue.getAssignedTo().getUsername(),
                        issue.getAssignedTo().getEmail(),
                        issue.getAssignedTo().getFirstName(),
                        issue.getAssignedTo().getLastName()
                ) : null;

        return new IssueDto(
                issue.getId(),
                createdBy,
                assignedTo,
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getProject().getId(),
                issue.getDateCreated().toString(),
                issue.getDateModified() != null ? issue.getDateModified().toString() : null
        );
    }

    public Issue toEntity(CreateIssueRequest req,
                          Project project,
                          User createdBy,
                          User assignedTo,
                          Status status,
                          Priority priority) {
        Issue issue = Issue.builder()
                .project(project)
                .createdBy(createdBy)
                .title(req.title())
                .description(req.description())
                .status(status)
                .priority(priority)
                .build();

        if (assignedTo != null) {
            issue.setAssignedTo(assignedTo);
        }

        return issue;
    }
}
