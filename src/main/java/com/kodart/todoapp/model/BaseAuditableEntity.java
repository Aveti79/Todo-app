package com.kodart.todoapp.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
abstract class BaseAuditableEntity {
    // It's used by SpringMethods
    @SuppressWarnings("unused")
    private LocalDateTime createdOn;
    @SuppressWarnings("unused")
    private LocalDateTime updatedOn;

    // Operacja, która wykona się przed zapisem do bazy
    @PrePersist
    void prePersist() {
        createdOn = LocalDateTime.now();
    }

    @PreUpdate
    void preMerge() {
        updatedOn = LocalDateTime.now();
    }
}
