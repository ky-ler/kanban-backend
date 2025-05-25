package com.kylerriggs.kanban.issue.dto;

public record CreateIssueRequest(
        Long projectId,
        String assignedToUsername,
        String title,
        String description,
        Long statusId,
        Long priorityId
) { }
