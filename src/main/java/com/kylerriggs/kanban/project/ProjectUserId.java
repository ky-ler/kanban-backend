package com.kylerriggs.kanban.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

// Composite Key for ProjectUser
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserId implements Serializable {
    private Long project;
    private String user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectUserId that)) return false;
        return Objects.equals(project, that.project) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, user);
    }
}
