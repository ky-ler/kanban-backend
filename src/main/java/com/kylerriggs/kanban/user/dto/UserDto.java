package com.kylerriggs.kanban.user.dto;

public record UserDto(
        String id,
        String username,
        String email,
        String profileImageUrl,
        String defaultProjectId) { }
