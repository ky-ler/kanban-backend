package com.kylerriggs.kanban.project;

import com.kylerriggs.kanban.exception.ResourceNotFoundException;
import com.kylerriggs.kanban.issue.IssueRepository;
import com.kylerriggs.kanban.project.dto.ProjectDto;
import com.kylerriggs.kanban.project.dto.ProjectSummary;
import com.kylerriggs.kanban.user.User;
import com.kylerriggs.kanban.user.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final IssueRepository issueRepository;
    private final ProjectMapper projectMapper;


    @Transactional
    public ProjectDto createProject(String name, String description) {
        String requestUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestUserId));

        Project project = Project.builder()
                .name(name)
                .description(description)
                .createdBy(owner)
                .build();

        ProjectUser ownerMembership = ProjectUser.builder()
                .project(project)
                .user(owner)
                .role(ProjectRole.ADMIN)
                .build();

        project.getCollaborators().add(ownerMembership);
        Project savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    @Transactional(readOnly = true)
    public ProjectDto getById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        return projectMapper.toDto(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummary> getAllForUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Project> projects = projectRepository.findAllByCollaboratorsUserId(userId);

        return projects.stream()
                .map(projectMapper::toSummaryDto)
                .toList();
    }

    @Transactional
    public ProjectDto updateProject(Long projectId, String name, String description) {
        Project projectToUpdate = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        projectToUpdate.setName(name);
        projectToUpdate.setDescription(description);

        projectRepository.save(projectToUpdate);

        return projectMapper.toDto(projectToUpdate);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project projectToDelete = projectRepository.findById(projectId)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        projectRepository.delete(projectToDelete);
    }

    @Transactional
    public void addCollaborator(Long projectId, String userId, ProjectRole role) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        boolean alreadyCollaborator = project.getCollaborators().stream()
                .anyMatch(c -> c.getUser().getId().equals(userId));

        if (alreadyCollaborator) {
            throw new IllegalArgumentException("User is already a collaborator in this project.");
        }

        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        ProjectUser newCollaborator = ProjectUser.builder()
                .project(project)
                .user(userToAdd)
                .role(role)
                .build();

        project.getCollaborators().add(newCollaborator);
        projectRepository.save(project);
    }

    @Transactional
    public void removeCollaborator(Long projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        ProjectUser collaboratorToRemove = project.getCollaborators().stream()
                .filter(c -> c.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found with ID: " + userId));

        if (project.getCollaborators().size() == 1) {
            throw new IllegalArgumentException("Cannot remove the only collaborator. Please delete the project instead.");
        }

        long adminCount = project.getCollaborators().stream()
                .filter(c -> c.getRole() == ProjectRole.ADMIN)
                .count();

        if (adminCount == 1 && collaboratorToRemove.getRole() == ProjectRole.ADMIN) {
            throw new IllegalArgumentException("Cannot remove the last admin from the project.");
        }

        // Unassign any issues assigned to the user being removed
        project.getIssues().forEach(issue -> {
            if (issue.getAssignedTo() != null && issue.getAssignedTo().getId().equals(userId)) {
                issue.setAssignedTo(null);
            }
        });

        project.getCollaborators().remove(collaboratorToRemove);

        // If no admins remain, promote the first found collaborator to admin
        if (project.getCollaborators().stream().noneMatch(c -> c.getRole() == ProjectRole.ADMIN)) {
            project.getCollaborators().stream()
                    .findFirst()
                    .ifPresent(newAdmin -> newAdmin.setRole(ProjectRole.ADMIN));
        }

        projectRepository.save(project);
    }

    @Transactional
    public void updateCollaboratorRole(Long projectId, String userId, ProjectRole newRole) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        ProjectUser collaboratorToUpdate = project.getCollaborators().stream()
                .filter(c -> c.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found with ID: " + userId));

        // If this user is the only collaborator, they MUST be an ADMIN.
        if (project.getCollaborators().size() == 1) {
            if (newRole != ProjectRole.ADMIN) {
                throw new IllegalArgumentException("Cannot change the role of the only collaborator. They must remain an ADMIN.");
            }
            return;
        }

        // Check if the user is the last admin, and prevent demotion if so
        long adminCount = project.getCollaborators().stream()
                .filter(c -> c.getRole() == ProjectRole.ADMIN)
                .count();

        if (adminCount == 1 && collaboratorToUpdate.getRole() == ProjectRole.ADMIN && newRole != ProjectRole.ADMIN) {
            throw new IllegalArgumentException("Cannot demote the last admin of the project. Please assign another admin first.");
        }

        collaboratorToUpdate.setRole(newRole);

        projectRepository.save(project);
    }
}
