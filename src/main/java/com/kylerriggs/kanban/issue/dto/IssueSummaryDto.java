package com.kylerriggs.kanban.issue.dto;

import com.kylerriggs.kanban.priority.Priority;
import com.kylerriggs.kanban.status.Status;

public record IssueSummaryDto (
        Long id,
        String title,
        Status status,
        Priority priority,
        String assignedToUsername
) {
}
