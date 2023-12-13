package tech.saas.tasks.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TaskAssignmentDto {

    private UUID task;
    private String actor;
    private OffsetDateTime instant;
}
