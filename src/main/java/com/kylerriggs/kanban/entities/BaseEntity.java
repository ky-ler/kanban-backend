package com.kylerriggs.kanban.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class BaseEntity {
    @CreatedDate
    @Column(name = "date_created", updatable = false, nullable = false)
    private Instant dateCreated;

    @LastModifiedDate
    @Column(name = "date_modified", nullable = false)
    private Instant dateModified;
}
