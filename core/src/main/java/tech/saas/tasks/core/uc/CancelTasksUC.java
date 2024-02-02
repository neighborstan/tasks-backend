package tech.saas.tasks.core.uc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.Shipping;
import tech.saas.tasks.core.models.TaskDto;
import tech.saas.tasks.core.services.AssignmentService;
import tech.saas.tasks.core.services.TasksService;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class CancelTasksUC {

    private final TasksService tasksService;
    private final AssignmentService assignmentService;
    private final Clock clock;

    public List<TaskDto<?,?>> apply(Shipping shipping) {

        var tasks = tasksService.pipeline(String.valueOf(shipping.getId()));
        for (var task : tasks) {
            var assignment = assignmentService.assignment(task.getId());

            task.setStatus(TaskDto.Status.CANCELED);
            var story = task.getStory();
            story.add(new TaskDto.Story("tasks-service", TaskDto.Status.CANCELED, "online", OffsetDateTime.now(clock)));
            task.setStory(story);
            tasksService.persist(task);

            for (var a : assignment)
                assignmentService.delete(a);
        }

        return tasksService.pipeline(String.valueOf(shipping.getId()));
    }
}
