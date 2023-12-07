package tech.saas.tasks.api.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import tech.saas.tasks.api.models.Task;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class TasksController implements TasksApi {

    private final Clock clock;

    @Override
    @PreAuthorize("hasAuthority('SCOPE_driver')")
    public ResponseEntity<Task> completeTask(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    @PreAuthorize("hasAuthority('SCOPE_driver')")
    public ResponseEntity<List<Task>> getTasks() {

        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var jwt = auth.getToken();

        var vv =
                List.of(
                        new Task()
                                .id(UUID.randomUUID())
                                .type(Task.TypeEnum.READINESS_CHECK)
                                .status(Task.StatusEnum.ACTIVE)
                                .startInstant(OffsetDateTime.now(clock))
                                .payload(Collections.emptyMap()),
                        new Task()
                                .id(UUID.randomUUID())
                                .type(Task.TypeEnum.MOVEMENT_START)
                                .status(Task.StatusEnum.PENDING)
                                .startInstant(OffsetDateTime.now(clock))
                                .payload(Collections.emptyMap()),
                        new Task()
                                .id(UUID.randomUUID())
                                .type(Task.TypeEnum.WAYPOINT_REACH)
                                .status(Task.StatusEnum.PENDING)
                                .startInstant(OffsetDateTime.now(clock))
                                .payload(Collections.emptyMap()),
                        new Task()
                                .id(UUID.randomUUID())
                                .type(Task.TypeEnum.DOCKING_START)
                                .status(Task.StatusEnum.PENDING)
                                .startInstant(OffsetDateTime.now(clock))
                                .payload(Collections.emptyMap()),
                        new Task()
                                .id(UUID.randomUUID())
                                .type(Task.TypeEnum.DOCKING_END)
                                .status(Task.StatusEnum.PENDING)
                                .startInstant(OffsetDateTime.now(clock))
                                .payload(Collections.emptyMap())
                );
        return ResponseEntity.ok(vv);
    }
}
