package com.kylerriggs.kanban.issue.dto;

import com.kylerriggs.kanban.priority.Priority;
import com.kylerriggs.kanban.user.dto.UserSummaryDto;
import com.kylerriggs.kanban.status.Status;

public record IssueDto(
        Long id,
        UserSummaryDto createdBy,
        UserSummaryDto assignedTo,
        String title,
        String description,
        Status status,
        Priority priority,
        Long project,
        String dateCreated,
        String dateModified
) { }
