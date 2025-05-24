package com.kylerriggs.kanban.project;

import com.kylerriggs.kanban.user.User;
import com.kylerriggs.kanban.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    // Create a new project and assign the creator as ADMIN
    @Transactional
    public Project createProject(String name, String description) {
        String requestUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findById(requestUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + requestUserId));

        Project project = Project.builder()
                .name(name)
                .description(description)
                .createdBy(owner)
                .build();

        projectRepository.save(project);

        ProjectUser membership = ProjectUser.builder()
                .project(project)
                .user(owner)
                .role(ProjectRole.ADMIN)
                .build();

        projectUserRepository.save(membership);

        return project;
    }

    // Retrieve project by ID
    @Transactional(readOnly = true)
    @PreAuthorize("@projectAccess.canView(#projectId)")
    public Project getById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
    }

    // Get all projects where the user is a collaborator
    @Transactional(readOnly = true)
//    @PreAuthorize("@projectAccess.canView()")
    public List<Project> getAllForUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return projectRepository.findAllByCollaboratorsUserId(userId);
    }

    // Update the name or description of an existing project
    @Transactional
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public Project updateProject(Long projectId, String name, String description) {
        Project project = getById(projectId);
        project.setName(name);
        project.setDescription(description);
        return projectRepository.save(project);
    }

    // Delete a project and its collaborators
    @Transactional
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        projectRepository.deleteById(projectId);
    }

    // Add a collaborator with the given role
    @Transactional
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public void addCollaborator(Long projectId, String userId, ProjectRole role) {
        Project project = getById(projectId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        ProjectUserId key = new ProjectUserId(project.getId(), user.getId());

        if (projectUserRepository.existsById(key)) {
            throw new IllegalArgumentException("User is already a collaborator");
        }

        ProjectUser collaborator = ProjectUser.builder()
                .project(project)
                .user(user)
                .role(role)
                .build();

        projectUserRepository.save(collaborator);
    }

    // Remove a collaborator
    @Transactional
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public void removeCollaborator(Long projectId, String userId) {
        ProjectUserId key = new ProjectUserId(projectId, userId);

        if (!projectUserRepository.existsById(key)) {
            throw new IllegalArgumentException("User is not a collaborator: " + userId);
        }

        // If the user is the only collaborator, delete the project
        ProjectUser membership = projectUserRepository.findById(key)
                .orElseThrow(() -> new IllegalArgumentException("Collaborator not found"));
        if (membership.getProject().getCollaborators().size() == 1) {
            projectRepository.delete(membership.getProject());
            return;
        }

        // If removing the user would leave only 1 person as a collaborator, set their role to ADMIN
        if (membership.getProject().getCollaborators().size() == 2) {
            ProjectUser otherMember = membership.getProject().getCollaborators().stream()
                    .filter(m -> !m.getUser().getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Other collaborator not found"));
            otherMember.setRole(ProjectRole.ADMIN);
            projectUserRepository.save(otherMember);
        }

        projectUserRepository.deleteById(key);
    }

    // Update collaborator
    @Transactional
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public void updateCollaboratorRole(Long projectId, String userId, ProjectRole newRole) {
        ProjectUserId key = new ProjectUserId(projectId, userId);
        ProjectUser membership = projectUserRepository.findById(key)
                .orElseThrow(() ->
                        new IllegalArgumentException("Collaborator not found"));

        if (membership.getProject().getCollaborators().size() == 1) {
            if (newRole != ProjectRole.ADMIN) {
                membership.setRole(ProjectRole.ADMIN);
                projectUserRepository.save(membership);
                throw new IllegalArgumentException("User has been set to admin due to being the only collaborator");
            }
            throw new IllegalArgumentException("Cannot change role of the only collaborator");
        }

        membership.setRole(newRole);
        projectUserRepository.save(membership);
    }
}
