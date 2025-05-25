package com.kylerriggs.kanban.issue;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kylerriggs.kanban.common.BaseEntity;
import com.kylerriggs.kanban.priority.Priority;
import com.kylerriggs.kanban.project.Project;
import com.kylerriggs.kanban.status.Status;
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
@Table(name = "issues")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id"
)
public class Issue extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_issue_project"))
    private Project project;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_id", foreignKey = @ForeignKey(name = "fk_issue_creator"))
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id", foreignKey = @ForeignKey(name = "fk_issue_assignee"))
    private User assignedTo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "fk_issue_status"))
    private Status status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "priority_id", foreignKey = @ForeignKey(name = "fk_issue_priority"))
    private Priority priority;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
}
