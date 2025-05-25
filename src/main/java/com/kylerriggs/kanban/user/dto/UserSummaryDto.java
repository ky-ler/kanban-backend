package com.kylerriggs.kanban.user.dto;

public record UserSummaryDto(String id,
                             String username,
                             String email,
                             String firstName,
                             String lastName) { }
