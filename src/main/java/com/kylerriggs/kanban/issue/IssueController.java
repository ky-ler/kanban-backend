package com.kylerriggs.kanban.issue;

import com.kylerriggs.kanban.issue.dto.CreateIssueRequest;
import com.kylerriggs.kanban.issue.dto.IssueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/projects/{projectId}/issues")
@RequiredArgsConstructor
public class IssueController {
    private final IssueService issueService;

    @GetMapping
    @PreAuthorize("@projectAccess.canView(#projectId)")
    public ResponseEntity<List<IssueDto>> list(
            @PathVariable Long projectId
    ) {
        List<IssueDto> issues = issueService.getAllForProject(projectId);
        return ResponseEntity.ok(issues);
    }

    @PostMapping("/create")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<IssueDto> create(
            @PathVariable Long projectId,
            @RequestBody CreateIssueRequest req
    ) {
        CreateIssueRequest createIssueRequest = new CreateIssueRequest(
                projectId,
                req.assignedToUsername(),
                req.title(),
                req.description(),
                req.statusId(),
                req.priorityId()
        );

        IssueDto issue = issueService.createIssue(createIssueRequest);

        return ResponseEntity.ok(issue);
    }

    @GetMapping("/{issueId}")
    @PreAuthorize("@projectAccess.canView(#projectId)")
    public ResponseEntity<IssueDto> get(
            @PathVariable Long projectId,
            @PathVariable Long issueId
    ) {
        IssueDto issue = issueService.getById(issueId);
        return ResponseEntity.ok(issue);
    }

    @PutMapping("/{issueId}")
    @PreAuthorize("@projectAccess.canModify(#projectId)")
    public ResponseEntity<IssueDto> update(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @RequestBody CreateIssueRequest req
    ) {
        IssueDto issue = issueService.getById(issueId);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        }

        IssueDto updated = issueService.updateIssue(issueId, req);

        return ResponseEntity.ok(updated);
    }

}
