package tech.saas.tasks.core.uc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.TaskDto;
import tech.saas.tasks.core.services.TasksService;

import java.util.List;

@Component
@AllArgsConstructor
public class GetTasksUC {

    private final TasksService tasksService;

    public List<TaskDto<?,?>> tasks(String phone) {
        return tasksService.actor(phone);
    }
}
