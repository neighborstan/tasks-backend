package tech.saas.tasks.core.uc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.exceptions.BadRequestException;
import tech.saas.tasks.core.exceptions.ForbiddenException;
import tech.saas.tasks.core.exceptions.NotFoundException;
import tech.saas.tasks.core.models.TaskAssignmentDto;
import tech.saas.tasks.core.models.TaskDto;
import tech.saas.tasks.core.models.TaskEntity;
import tech.saas.tasks.core.models.TaskPayload;
import tech.saas.tasks.core.services.AssignmentService;
import tech.saas.tasks.core.services.TasksService;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class CompleteTasksUC {

    private final Clock clock;
    private final TasksService tasksService;
    private final AssignmentService assignmentService;

    public TaskDto<? extends TaskEntity, ? extends TaskPayload> apply(
            String actor,
            UUID id,
            String mode,
            OffsetDateTime instant) {
        log.info("actor: {}; id: {}; mode: {}; instant: {}", actor, id, mode, instant);

        var exists = tasksService.get(id);
        if (exists.isEmpty())
            throw new NotFoundException("задача не найдена");

        var task = exists.get();


        var assignment = assignmentService.assignment(task.getId(), actor);

        if (assignment.isEmpty())
            throw new ForbiddenException("нет прав выполнять это задание. Вероятно, перевозка отменена");

        if (Objects.equals(task.getStatus(), TaskDto.Status.CANCELED))
            throw new BadRequestException("задача отменена");

        var story = task.getStory();
        if (story.stream().anyMatch(s -> Objects.equals(s.getAuthor(), actor) && Objects.equals(s.getStatus(), TaskDto.Status.DONE)))
            return task;

        story.add(new TaskDto.Story(actor, TaskDto.Status.DONE, mode, instant));
        task.setStory(story);
        task.setStatus(TaskDto.Status.DONE);
        tasksService.persist(task);


        var other =
                tasksService.pipeline(task.getPipeline()).stream()
                        .filter(v ->
                                switch (v.getType()) {
                                    case READINESS_CHECK -> false;
                                    case DOCKING_END, DOCKING_START, MOVEMENT_START, WAYPOINT_REACH, SHIPPING_COMPLETE ->
                                            true;
                                }
                        )
                        .toList();

        for (var o : other) {
            assignmentService.persist(
                    new TaskAssignmentDto(
                            o.getId(),
                            actor,
                            o.getPipeline(),
                            OffsetDateTime.now(clock)
                    )
            );
        }

        return task;
    }
}
