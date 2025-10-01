package com.kylerriggs.kanban.issue;

import com.kylerriggs.kanban.exception.ResourceNotFoundException;
import com.kylerriggs.kanban.issue.dto.CreateIssueRequest;
import com.kylerriggs.kanban.issue.dto.IssueDto;
import com.kylerriggs.kanban.priority.Priority;
import com.kylerriggs.kanban.priority.PriorityRepository;
import com.kylerriggs.kanban.project.Project;
import com.kylerriggs.kanban.project.ProjectRepository;
import com.kylerriggs.kanban.status.Status;
import com.kylerriggs.kanban.status.StatusRepository;
import com.kylerriggs.kanban.user.User;
import com.kylerriggs.kanban.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final PriorityRepository priorityRepository;
    private final IssueMapper issueMapper;

    @Transactional(readOnly = true)
    public List<IssueDto> getAllForProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }

        List<Issue> issues = issueRepository.findAllByProjectId(projectId);
        return issues.stream()
                .map(issueMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public IssueDto getById(Long projectId, Long issueId) {
        Issue issue = issueRepository.findByProjectIdAndId(projectId, issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId + " for project: " + projectId));

        return issueMapper.toDto(issue);
    }

    @Transactional
    public IssueDto createIssue(CreateIssueRequest req) {
        String requestUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        User createdBy = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestUserId));

        Project project = projectRepository.findById(req.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + req.projectId()));

        Status status = statusRepository.findByName(req.statusName())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found: " + req.statusName()));

        Priority priority = priorityRepository.findByName(req.priorityName())
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found: " + req.priorityName()));

        User assignedTo = null;
        if (StringUtils.hasText(req.assignedToUsername())) {
            project.getCollaborators().stream()
                    .filter(c -> c.getUser().getUsername().equals(req.assignedToUsername()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "User is not a collaborator on the project: " + req.assignedToUsername()
                    ));

            assignedTo = userRepository.findByUsername(req.assignedToUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.assignedToUsername()));
        }

        Issue newIssue = issueMapper.toEntity(req, project, createdBy, assignedTo, status, priority);
        project.getIssues().add(newIssue);

        // TODO: Fix having to set the date modified manually
        project.setDateModified(Instant.now());
        projectRepository.save(project);
        return issueMapper.toDto(newIssue);
    }

    @Transactional
    public IssueDto updateIssue(Long issueId, CreateIssueRequest req) {
        Project projectToUpdate = projectRepository.findById(req.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + req.projectId()));

        Issue issueToUpdate = projectToUpdate.getIssues().stream()
                .filter(i -> Objects.equals(i.getId(), issueId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));

        issueToUpdate.setTitle(req.title());
        issueToUpdate.setDescription(req.description());

        if (!issueToUpdate.getStatus().getName().equals(req.statusName())) {
            Status newStatus = statusRepository.findByName(req.statusName())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found: " + req.statusName()));
            issueToUpdate.setStatus(newStatus);
        }

        if (!issueToUpdate.getPriority().getName().equals(req.priorityName())) {
            Priority newPriority = priorityRepository.findByName(req.priorityName())
                    .orElseThrow(() -> new ResourceNotFoundException("Priority not found: " + req.priorityName()));
            issueToUpdate.setPriority(newPriority);
        }

        String currentAssigneeUsername = Optional.ofNullable(issueToUpdate.getAssignedTo())
                .map(User::getUsername)
                .orElse(null);

        if (!Objects.equals(currentAssigneeUsername, req.assignedToUsername())) {
            if (StringUtils.hasText(req.assignedToUsername())) {
                projectToUpdate.getCollaborators().stream()
                        .filter(c -> c.getUser().getUsername().equals(req.assignedToUsername()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "User is not a collaborator on the project: " + req.assignedToUsername()
                        ));

                User newAssignee = userRepository.findByUsername(req.assignedToUsername())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.assignedToUsername()));
                issueToUpdate.setAssignedTo(newAssignee);
            } else {
                issueToUpdate.setAssignedTo(null);
            }
        }

        // TODO: Fix having to set the date modified manually
        projectToUpdate.setDateModified(Instant.now());
        projectRepository.save(projectToUpdate);
        return issueMapper.toDto(issueToUpdate);
    }

    @Transactional
    public void deleteIssue(Long projectId, Long issueId) {
        Project projectToDeleteFrom = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        Issue issueToRemove = projectToDeleteFrom.getIssues().stream()
                .filter(i -> Objects.equals(i.getId(), issueId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));

        projectToDeleteFrom.getIssues().remove(issueToRemove);
        // TODO: Fix having to set the date modified manually
        projectToDeleteFrom.setDateModified(Instant.now());
        projectRepository.save(projectToDeleteFrom);
    }
}
