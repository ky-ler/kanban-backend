package com.kylerriggs.kanban.issue;

import com.kylerriggs.kanban.issue.dto.CreateIssueRequest;
import com.kylerriggs.kanban.issue.dto.IssueDto;
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
@RequestMapping("/api/projects/{projectId}/issues")
@RequiredArgsConstructor
public class IssueController {
    private final IssueService issueService;

    @GetMapping
    @PreAuthorize("@projectAccess.canView(#projectId)")
    public ResponseEntity<List<IssueDto>> list(@PathVariable Long projectId) {
        List<IssueDto> issues = issueService.getAllForProject(projectId);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/{issueId}")
    @PreAuthorize("@projectAccess.canView(#projectId)")
    public ResponseEntity<IssueDto> get(
            @PathVariable Long projectId,
            @PathVariable Long issueId
    ) {
        IssueDto issue = issueService.getById(projectId, issueId);
        return ResponseEntity.ok(issue);
    }

    @PostMapping("/create")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<IssueDto> create(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateIssueRequest req
    ) {
        CreateIssueRequest createIssueRequest = new CreateIssueRequest(
                projectId,
                req.assignedToUsername(),
                req.title(),
                req.description(),
                req.statusName(),
                req.priorityName()
        );

        IssueDto createdIssue = issueService.createIssue(createIssueRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/projects/{projectId}/issues/{issueId}")
                .buildAndExpand(projectId, createdIssue.id())
                .toUri();

        return ResponseEntity.created(location).body(createdIssue);
    }

    @PutMapping("/{issueId}")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<IssueDto> update(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @Valid @RequestBody CreateIssueRequest req
    ) {
        CreateIssueRequest updateIssueRequest = new CreateIssueRequest(
                projectId,
                req.assignedToUsername(),
                req.title(),
                req.description(),
                req.statusName(),
                req.priorityName()
        );

        IssueDto updatedIssue = issueService.updateIssue(issueId, updateIssueRequest);

        return ResponseEntity.ok(updatedIssue);
    }

    @DeleteMapping("/{issueId}")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long issueId
    ) {
        issueService.deleteIssue(projectId, issueId);
        return ResponseEntity.noContent().build();
    }

}
