package tech.saas.tasks.api.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.saas.tasks.core.controllers.TasksApi;
import tech.saas.tasks.core.converters.TasksConverter;
import tech.saas.tasks.core.models.Task;
import tech.saas.tasks.core.uc.CompleteTasksUC;
import tech.saas.tasks.core.uc.GetTasksUC;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class TasksController implements TasksApi {

    private final GetTasksUC getTasksUC;
    private final CompleteTasksUC completeTasksUC;
    private final TasksConverter tasksConverter;


    @PreAuthorize("hasAuthority('SCOPE_driver')")
    public ResponseEntity<Task> completeTask(
            UUID id,
            OffsetDateTime instant,
            String mode
    ) {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var jwt = auth.getToken();
        var phone = jwt.getClaimAsString("phone");

        return ResponseEntity.ok(tasksConverter.coreToApi(completeTasksUC.apply(phone, id, mode, instant)));
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
