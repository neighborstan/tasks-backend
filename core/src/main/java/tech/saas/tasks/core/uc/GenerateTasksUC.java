package tech.saas.tasks.core.uc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.converters.PointConverter;
import tech.saas.tasks.core.models.Shipping;
import tech.saas.tasks.core.models.ShippingAssignedResourcesInner;
import tech.saas.tasks.core.models.TaskAssignmentDto;
import tech.saas.tasks.core.models.TaskDto;
import tech.saas.tasks.core.services.AssignmentService;
import tech.saas.tasks.core.services.CoreService;
import tech.saas.tasks.core.services.TasksService;
import tech.saas.tasks.core.services.UUIDGen;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public List<TaskDto> apply(Shipping shipping, Map<String,?> raw) {
        var request =
                shipping.getShippingRequestInfo();

        var route =
                request.getRoutePoints();

        var resources =
                shipping.getAssignedResources();

        var actors =
                Stream.concat(
                                resources.stream()
                                        .flatMap(i -> i.getDriversIds().stream())
                                        .map(d -> coreService.driver(shipping.getCompanyId(), d))
                                        .flatMap(d ->
                                                d.getDriverSecretInfo().getPhones()
                                                        .stream()
                                        ),
                                resources.stream()
                                        .map(ShippingAssignedResourcesInner::getDriverContactInfo)
                        )
                        .distinct()
                        .toList();

        var story =
                List.of(
                        new TaskDto.Story(
                                "tasks-service",
                                TaskDto.Status.ACTIVE,
                                OffsetDateTime.now(clock)
                        )
                );

        var info = shipping.getShippingRequestInfo();

        var assignments =
                actors.stream()
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
                                                raw,
                                                raw,
                                                Objects.requireNonNullElse(info.getComment(), "")
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
                                        OffsetDateTime.now(clock).plusMinutes(5),
                                        raw,
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
                                        OffsetDateTime.now(clock).plusMinutes(10),
                                        raw,
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
                                        OffsetDateTime.now(clock).plusMinutes(15),
                                        raw,
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
                                        OffsetDateTime.now(clock).plusMinutes(20),
                                        raw,
                                        pointConverter.apiToCore(p),
                                        ""
                                )
                        )
                )
                .map(tasksService::persist)
                .toList();
    }
}
