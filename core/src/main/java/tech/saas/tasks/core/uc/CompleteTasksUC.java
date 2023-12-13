package tech.saas.tasks.core.uc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.exceptions.BadRequestException;
import tech.saas.tasks.core.exceptions.NotFoundException;
import tech.saas.tasks.core.models.TaskAssignmentDto;
import tech.saas.tasks.core.models.TaskDto;
import tech.saas.tasks.core.services.AssignmentService;
import tech.saas.tasks.core.services.TasksService;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CompleteTasksUC {

    private final Clock clock;
    private final TasksService tasksService;
    private final AssignmentService assignmentService;

    public List<TaskDto> tasks(String actor, UUID id, OffsetDateTime instant) {
        var exists = tasksService.get(id);
        if (exists.isEmpty())
            throw new NotFoundException("задача не найдена");

        var task = exists.get();

        if (Objects.equals(task.getStatus(), TaskDto.Status.CANCELED))
            throw new BadRequestException("задача отменена");

        var story = task.getStory();
        story.add(new TaskDto.Story(actor, TaskDto.Status.DONE, OffsetDateTime.now(clock)));
        task.setStory(story);
        tasksService.persist(task);


        var other =
                tasksService.pipeline(task.getPipeline()).stream()
                        .filter(v ->
                                switch (v.getType()) {
                                    case READINESS_CHECK -> false;
                                    case DOCKING_END, DOCKING_START, MOVEMENT_START, WAYPOINT_REACH -> true;
                                }
                        )
                        .toList();

        for (var o : other) {
            assignmentService.persist(
                    new TaskAssignmentDto(
                            o.getId(),
                            actor,
                            OffsetDateTime.now(clock)
                    )
            );
        }

        return other;
    }
}
