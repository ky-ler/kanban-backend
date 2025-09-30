package com.kylerriggs.kanban.project;

import com.kylerriggs.kanban.project.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectRequest req) {
        ProjectDto project = projectService.createProject(req.name(), req.description());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{projectId}")
                .buildAndExpand(project.id())
                .toUri();

        return ResponseEntity.created(location).body(project);
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("@projectAccess.canView(#projectId)")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long projectId) {
        ProjectDto project = projectService.getById(projectId);
        return ResponseEntity.ok(project);
    }

    @GetMapping
//    @PreAuthorize("@projectAccess.canView()")
    public ResponseEntity<List<ProjectSummary>> getProjectsForUser() {
        List<ProjectSummary> projects = projectService.getAllForUser();
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRequest req
    ) {
        ProjectDto updatedProject = projectService.updateProject(projectId, req.name(), req.description());
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/collaborators")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<Void> addCollaborator(
            @PathVariable Long projectId,
            @Valid @RequestBody CollaboratorRequest req
    ) {
        projectService.addCollaborator(projectId, req.userId(), req.role());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{projectId}/collaborators/{userId}")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<Void> removeCollaborator(
            @PathVariable Long projectId,
            @PathVariable String userId
    ) {
        projectService.removeCollaborator(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}/collaborators/{userId}")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<Void> updateCollaboratorRole(
            @PathVariable Long projectId,
            @PathVariable String userId,
            @Valid @RequestBody RoleUpdateRequest req
    ) {
        projectService.updateCollaboratorRole(projectId, userId, req.newRole());
        return ResponseEntity.ok().build();
    }
}
