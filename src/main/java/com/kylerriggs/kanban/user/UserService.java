package com.kylerriggs.kanban.user;

import com.kylerriggs.kanban.exception.ResourceNotFoundException;
import com.kylerriggs.kanban.project.Project;
import com.kylerriggs.kanban.project.ProjectRepository;
import com.kylerriggs.kanban.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserMapper userMapper;

    public void setDefaultProject(Long projectId) {
        String requestUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestUserId));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        user.setDefaultProject(project);
        userRepository.save(user);
    }

    public UserDto getCurrentUser() {
        String requestUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestUserId));

        return userMapper.toUserDto(user);
    }
}
