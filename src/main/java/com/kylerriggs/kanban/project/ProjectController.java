package com.kylerriggs.kanban.project;

import com.kylerriggs.kanban.project.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody CreateProjectRequest req) {
        Project project = projectService.createProject(
                req.name(), req.description()
        );

        return ResponseEntity.ok(toDto(project));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long projectId) {
        Project project = projectService.getById(projectId);
        return ResponseEntity.ok(toDto(project));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getProjectsForUser() {
        List<Project> projects = projectService.getAllForUser();
        List<ProjectDto> projectDtos = projects.stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(projectDtos);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long projectId,
            @RequestBody UpdateProjectRequest req
    ) {
        Project project = projectService.getById(projectId);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        Project updated = projectService.updateProject(
                projectId, req.name(), req.description()
        );
        log.error(project.getName());

        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/collaborators")
    public ResponseEntity<Void> addCollaborator(
            @PathVariable Long projectId,
            @RequestBody CollaboratorRequest req
    ) {
        projectService.addCollaborator(
                projectId, req.userId(), req.role()
        );

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{projectId}/collaborators/{userId}")
    public ResponseEntity<Void> updateCollaboratorRole(
            @PathVariable Long projectId,
            @PathVariable String userId,
            @RequestBody RoleUpdateRequest req
    ) {
        projectService.updateCollaboratorRole(projectId, userId, req.newRole());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/collaborators/{userId}")
    public ResponseEntity<Void> removeCollaborator(
            @PathVariable Long projectId,
            @PathVariable String userId
    ) {
        projectService.removeCollaborator(projectId, userId);

        return ResponseEntity.noContent().build();
    }

    private ProjectDto toDto(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                new UserSummaryDto(
                        project.getCreatedBy().getId(),
                        project.getCreatedBy().getUsername(),
                        project.getCreatedBy().getEmail(),
                        project.getCreatedBy().getFirstName(),
                        project.getCreatedBy().getLastName()
                ),
                project.getCollaborators().stream()
                        .map(c -> new CollaboratorDto(
                                new UserSummaryDto(
                                        c.getUser().getId(),
                                        c.getUser().getUsername(),
                                        c.getUser().getEmail(),
                                        c.getUser().getFirstName(),
                                        c.getUser().getLastName()
                                ),
                                c.getRole()
                        ))
                        .toArray(CollaboratorDto[]::new),
                project.getDateCreated().toString(),
                project.getDateModified().toString()
        );
    }
}
