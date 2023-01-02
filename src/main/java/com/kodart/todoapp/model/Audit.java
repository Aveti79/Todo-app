package com.kodart.todoapp.model;

import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Embeddable
class Audit {
    // Używane przez Springa
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
