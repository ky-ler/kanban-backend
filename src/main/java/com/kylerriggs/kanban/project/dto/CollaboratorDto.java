package com.kylerriggs.kanban.project.dto;

import com.kylerriggs.kanban.project.ProjectRole;

public record CollaboratorDto (UserSummaryDto user, ProjectRole role){
}
