package com.kylerriggs.kanban.project.dto;

import com.kylerriggs.kanban.project.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull(message = "A new role must be provided")
        ProjectRole newRole
) { }
