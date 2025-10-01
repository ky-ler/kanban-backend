package com.kylerriggs.kanban.project.dto;

public record ProjectSummary(
        Long id,
        String name,
        String description,
        String dateModified,
        int doneIssues,
        int totalIssues
) {
}
