package com.kylerriggs.kanban.project.dto;

public record ProjectDto(Long id,
                         String name,
                         String description,
                         UserSummaryDto createdBy,
                         CollaboratorDto[] collaborators,
                         String dateCreated,
                         String dateModified) { }
