package com.kylerriggs.kanban.project.dto;

import com.kylerriggs.kanban.project.ProjectRole;

public record CollaboratorRequest(String userId, ProjectRole role) { }

