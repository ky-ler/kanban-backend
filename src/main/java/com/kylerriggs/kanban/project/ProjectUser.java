package com.kylerriggs.kanban.project;

import com.kylerriggs.kanban.common.BaseEntity;
import com.kylerriggs.kanban.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "project_users")
@IdClass(ProjectUserId.class)
public class ProjectUser extends BaseEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_pu_project"))
    private Project project;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_pu_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ProjectRole role;
}

