package com.kylerriggs.kanban.user;

import com.kylerriggs.kanban.common.BaseEntity;
import com.kylerriggs.kanban.issue.Issue;
import com.kylerriggs.kanban.project.Project;
import com.kylerriggs.kanban.project.ProjectUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "users")
@NamedQuery(name = UserConstants.FIND_USER_BY_EMAIL, query = "SELECT u FROM User u WHERE u.email = :email")
public class User extends BaseEntity {
    @Id
    private String id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_project_id")
    private Project defaultProject;

    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private Set<Project> projectsCreated = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private Set<Issue> issuesCreated = new HashSet<>();

    @OneToMany(mappedBy = "assignedTo")
    @Builder.Default
    private Set<Issue> issuesAssigned = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<ProjectUser> projectMemberships = new HashSet<>();
}
