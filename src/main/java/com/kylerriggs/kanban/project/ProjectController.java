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
        ProjectDto project = projectService.createProject(
                req.name(), req.description()
        );

        return ResponseEntity.ok(project);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long projectId) {
        ProjectDto project = projectService.getById(projectId);
        return ResponseEntity.ok(project);
    }

    @GetMapping
    public ResponseEntity<List<ProjectSummary>> getProjectsForUser() {
        List<ProjectSummary> projects = projectService.getAllForUser();

        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long projectId,
            @RequestBody UpdateProjectRequest req
    ) {
        ProjectDto project = projectService.getById(projectId);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        ProjectDto updated = projectService.updateProject(
                projectId, req.name(), req.description()
        );

        return ResponseEntity.ok(updated);
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
}
