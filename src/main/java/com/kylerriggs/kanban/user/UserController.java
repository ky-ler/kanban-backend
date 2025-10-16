package com.kylerriggs.kanban.user;

import com.kylerriggs.kanban.user.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/default-project")
    @PreAuthorize("@userAccess.canModify(authentication)")
    public ResponseEntity<Void> setDefaultProject(@Valid @RequestBody Long projectId) {
        userService.setDefaultProject(projectId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @PreAuthorize("@userAccess.canModify(authentication)")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}
