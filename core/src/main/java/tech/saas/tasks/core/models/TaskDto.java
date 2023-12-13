package tech.saas.tasks.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private UUID id;
    private Type type;
    private Status status;
    private String pipeline;
    private String author;

    private List<Story> story;
    private OffsetDateTime transition;

    private Object entity;
    private Object payload;

    private String comment;

    public enum Type {
        READINESS_CHECK,
        MOVEMENT_START,
        WAYPOINT_REACH,
        DOCKING_START,
        DOCKING_END;
    }


    public enum Status {
        PENDING,
        ACTIVE,
        DONE,
        CANCELED;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Story {

        private String author;
        private Status status;
        private OffsetDateTime instant;


    }
}
