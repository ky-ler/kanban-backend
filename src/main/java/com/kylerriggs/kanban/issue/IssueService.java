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

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final PriorityRepository priorityRepository;
    private final IssueMapper issueMapper;

    @Transactional
    public List<IssueDto> getAllForProject(Long projectId) {

        List<Issue> issues = issueRepository.findAllByProjectId(projectId);

        return issues.stream()
                .map(issueMapper::toDto)
                .toList();
    }

    @Transactional
    public IssueDto getById(Long projectId, Long issueId) {
        Issue issue = issueRepository.findByProjectIdAndId(projectId, issueId);

        if (issue == null) {
            throw new ResourceNotFoundException("Issue not found: " + issueId + " for project: " + projectId);
        }

        return issueMapper.toDto(issue);
    }

    @Transactional
    public IssueDto createIssue(CreateIssueRequest req) {
        String requestUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        User createdBy = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestUserId));

        Project project = projectRepository.findById(req.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + req.projectId()));

        User assignedTo = null;
        if (req.assignedToUsername() != null) {
            if (project.getCollaborators().stream()
                    .noneMatch(c -> c.getUser().getUsername().equals(req.assignedToUsername()))) {

                throw new IllegalArgumentException(
                        "User is not a collaborator on the project: " + req.assignedToUsername()
                );
            }

            assignedTo = userRepository.findByUsername(req.assignedToUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.assignedToUsername()));
        }

        Status status = statusRepository.findById(req.statusId())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found: " + req.statusId()));

        Priority priority = priorityRepository.findById(req.priorityId())
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found: " + req.priorityId()));


        Issue issue = issueMapper.toEntity(req, project, createdBy, assignedTo, status, priority);

        Issue saved = issueRepository.save(issue);

        return issueMapper.toDto(saved);
    }

    @Transactional
    public IssueDto updateIssue(Long issueId, CreateIssueRequest req) {
        Issue issue = issueRepository.findByProjectIdAndId(req.projectId(), issueId);

        if (issue == null) {
            throw new ResourceNotFoundException("Issue not found: " + issueId + " for project: " + req.projectId());
        }

        Project project = projectRepository.findById(req.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + req.projectId()));

        if (req.title() != null && !req.title().equals(issue.getTitle())) {
            issue.setTitle(req.title());
        }

        if (req.description() != null && !req.description().equals(issue.getDescription())) {
            issue.setDescription(req.description());
        }

        if (req.assignedToUsername() != null && !req.assignedToUsername().equals(issue.getAssignedTo().getUsername())) {
            if (project.getCollaborators().stream()
                    .noneMatch(c -> c.getUser().getUsername().equals(req.assignedToUsername()))) {

                throw new IllegalArgumentException(
                        "User is not a collaborator on the project: " + req.assignedToUsername()
                );
            }

            User assignedTo = userRepository.findByUsername(req.assignedToUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.assignedToUsername()));

            issue.setAssignedTo(assignedTo);
        }

        if (req.statusId() != null && !req.statusId().equals(issue.getStatus().getId())) {
            Status status = statusRepository.findById(req.statusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found: " + req.statusId()));
            issue.setStatus(status);
        }

        if (req.priorityId() != null && !req.projectId().equals(issue.getPriority().getId())) {
            Priority priority = priorityRepository.findById(req.priorityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Priority not found: " + req.priorityId()));
            issue.setPriority(priority);
        }

        Issue saved = issueRepository.save(issue);

        return issueMapper.toDto(saved);
    }

    @Transactional
    public void deleteIssue(Long projectId, Long issueId) {
        if (!issueRepository.existsByProjectIdAndId(projectId, issueId)) {
            throw new ResourceNotFoundException("Issue not found: " + issueId + " for project: " + projectId);
        }

        issueRepository.deleteById(issueId);
    }
}
