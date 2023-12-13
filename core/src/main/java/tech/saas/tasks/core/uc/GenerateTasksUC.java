package tech.saas.tasks.core.uc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.converters.PointConverter;
import tech.saas.tasks.core.models.Shipping;
import tech.saas.tasks.core.models.TaskAssignmentDto;
import tech.saas.tasks.core.models.TaskDto;
import tech.saas.tasks.core.services.AssignmentService;
import tech.saas.tasks.core.services.CoreService;
import tech.saas.tasks.core.services.TasksService;
import tech.saas.tasks.core.services.UUIDGen;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class GenerateTasksUC {

    private final PointConverter pointConverter;
    private final TasksService tasksService;
    private final AssignmentService assignmentService;
    private final CoreService coreService;
    private final UUIDGen uuidGen;
    private final Clock clock;

    public List<TaskDto> apply(Shipping shipping) {
        var request =
                shipping.getShippingRequestInfo();

        var route =
                request.getRoutePoints();

        var resources =
                shipping.getAssignedResources();

        var drivers =
                resources.stream()
                        .flatMap(i -> i.getDriversIds().stream())
                        .map(coreService::driver)
                        .toList();
        var story =
                List.of(
                        new TaskDto.Story(
                                "tasks-service",
                                TaskDto.Status.ACTIVE,
                                OffsetDateTime.now(clock)
                        )
                );

        var assignments =
                drivers.stream()
                        .flatMap(d ->
                                d.getDriverSecretInfo().getPhones()
                                        .stream()
                        )
                        .distinct()
                        .map(p ->
                                Map.entry(
                                        new TaskAssignmentDto(
                                                uuidGen.gen(p, shipping.getId()),
                                                p,
                                                OffsetDateTime.now(clock)
                                        ),
                                        new TaskDto(
                                                uuidGen.gen(p, shipping.getId()),
                                                TaskDto.Type.READINESS_CHECK,
                                                TaskDto.Status.ACTIVE,
                                                String.valueOf(shipping.getId()),
                                                "tasks-service",
                                                story,
                                                OffsetDateTime.now(clock),
                                                shipping,
                                                shipping,
                                                ""
                                        )
                                )
                        )
                        .toList();

        for (var a : assignments) {
            var task = a.getValue();
            var assignment = a.getKey();
            tasksService.persist(task);
            assignmentService.persist(assignment);
        }

        return route.stream()
                .flatMap(p ->
                        Stream.of(
                                new TaskDto(
                                        uuidGen.gen(p.getId(), shipping.getId(), TaskDto.Type.MOVEMENT_START),
                                        TaskDto.Type.MOVEMENT_START,
                                        TaskDto.Status.ACTIVE,
                                        String.valueOf(shipping.getId()),
                                        "tasks-service",
                                        story,
                                        OffsetDateTime.now(clock),
                                        shipping,
                                        pointConverter.apiToCore(p),
                                        ""
                                ),
                                new TaskDto(
                                        uuidGen.gen(p.getId(), shipping.getId(), TaskDto.Type.WAYPOINT_REACH),
                                        TaskDto.Type.WAYPOINT_REACH,
                                        TaskDto.Status.ACTIVE,
                                        String.valueOf(shipping.getId()),
                                        "tasks-service",
                                        story,
                                        OffsetDateTime.now(clock),
                                        shipping,
                                        pointConverter.apiToCore(p),
                                        ""
                                ),
                                new TaskDto(
                                        uuidGen.gen(p.getId(), shipping.getId(), TaskDto.Type.DOCKING_START),
                                        TaskDto.Type.DOCKING_START,
                                        TaskDto.Status.ACTIVE,
                                        String.valueOf(shipping.getId()),
                                        "tasks-service",
                                        story,
                                        OffsetDateTime.now(clock),
                                        shipping,
                                        pointConverter.apiToCore(p),
                                        ""
                                ),
                                new TaskDto(
                                        uuidGen.gen(p.getId(), shipping.getId(), TaskDto.Type.DOCKING_END),
                                        TaskDto.Type.DOCKING_END,
                                        TaskDto.Status.ACTIVE,
                                        String.valueOf(shipping.getId()),
                                        "tasks-service",
                                        story,
                                        OffsetDateTime.now(clock),
                                        shipping,
                                        pointConverter.apiToCore(p),
                                        ""
                                )
                        )
                )
                .map(tasksService::persist)
                .toList();
    }
}
