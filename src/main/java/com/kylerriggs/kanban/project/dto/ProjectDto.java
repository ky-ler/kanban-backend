package com.kylerriggs.kanban.project.dto;

import com.kylerriggs.kanban.issue.dto.IssueSummaryDto;
import com.kylerriggs.kanban.user.dto.UserSummaryDto;

public record ProjectDto(Long id,
                         String name,
                         String description,
                         UserSummaryDto createdBy,
                         CollaboratorDto[] collaborators,
                         IssueSummaryDto[] issues,
                         String dateCreated,
                         String dateModified) { }
