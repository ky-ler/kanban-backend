package com.kylerriggs.kanban.issue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateIssueRequest(
        @NotNull(message = "Project ID cannot be null")
        Long projectId,

        String assignedToUsername,

        @NotBlank(message = "Title cannot be blank")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        String description,

        @NotNull(message = "Status ID cannot be null")
        Long statusId,

        @NotNull(message = "Priority ID cannot be null")
        Long priorityId
) { }
