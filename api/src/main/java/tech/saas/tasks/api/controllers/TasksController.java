package tech.saas.tasks.api.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import tech.saas.tasks.core.converters.TasksConverter;
import tech.saas.tasks.core.controllers.TasksApi;
import tech.saas.tasks.core.models.Task;
import tech.saas.tasks.core.uc.GetTasksUC;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
public class TasksController implements TasksApi {

    private final GetTasksUC getTasksUC;
    private final TasksConverter tasksConverter;

    private final Clock clock;


    @Override
    @PreAuthorize("hasAuthority('SCOPE_driver')")
    public ResponseEntity<Task> completeTask(String id, OffsetDateTime instant) {
        return null;
    }

    @Override
    @PreAuthorize("hasAuthority('SCOPE_driver')")
    public ResponseEntity<List<Task>> getTasks() {

        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var jwt = auth.getToken();
        var phone = jwt.getClaimAsString("phone");
        var tasks = getTasksUC.tasks(phone);

        return ResponseEntity.ok(
                tasks.stream()
                        .map(tasksConverter::coreToApi)
                        .toList()
        );
    }
}
