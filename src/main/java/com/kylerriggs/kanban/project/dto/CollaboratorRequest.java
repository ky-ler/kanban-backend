package com.kylerriggs.kanban.project.dto;

import com.kylerriggs.kanban.project.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CollaboratorRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotNull(message = "Role must be provided")
        ProjectRole role
) { }

