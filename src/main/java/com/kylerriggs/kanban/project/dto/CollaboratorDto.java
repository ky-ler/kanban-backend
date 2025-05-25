package com.kylerriggs.kanban.project.dto;

import com.kylerriggs.kanban.project.ProjectRole;
import com.kylerriggs.kanban.user.dto.UserSummaryDto;

public record CollaboratorDto (UserSummaryDto user, ProjectRole role){
}
