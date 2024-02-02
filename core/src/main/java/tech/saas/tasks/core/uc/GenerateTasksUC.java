package tech.saas.tasks.core.uc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.converters.PointConverter;
import tech.saas.tasks.core.models.Author;
import tech.saas.tasks.core.models.PolymorphMap;
import tech.saas.tasks.core.models.SelectedCarSupply;
import tech.saas.tasks.core.models.Shipping;
import tech.saas.tasks.core.models.ShippingAssignedResourcesInner;
import tech.saas.tasks.core.models.ShippingRequest;
import tech.saas.tasks.core.models.ShippingStatus;
import tech.saas.tasks.core.models.TaskAssignmentDto;
import tech.saas.tasks.core.models.TaskDto;
import tech.saas.tasks.core.models.TaskEntity;
import tech.saas.tasks.core.models.TaskPayload;
import tech.saas.tasks.core.services.AssignmentService;
import tech.saas.tasks.core.services.CoreService;
import tech.saas.tasks.core.services.PeopleService;
import tech.saas.tasks.core.services.TasksService;
import tech.saas.tasks.core.services.UUIDGen;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class GenerateTasksUC {

    private final UUIDGen uuidGen;
    private final Clock clock;

    private final PointConverter pointConverter;

    private final CoreService coreService;
    private final TasksService tasksService;
    private final PeopleService peopleService;
    private final AssignmentService assignmentService;

    public List<TaskDto<?, ?>> apply(Shipping shipping, Map<String, ?> raw, Author author) {

        var request =
                shipping.getShippingRequestInfo();

        if (!Objects.equals(request.getType(), ShippingRequest.TypeEnum.SHIPPING_REQUEST))
            return Collections.emptyList();

        var shippingStatus = shipping.getStatus();
        if (!List.of(ShippingStatus.CodeNameEnum.TRIP_WAITING, ShippingStatus.CodeNameEnum.IN_WAY).contains(shippingStatus.getCodeName()))
            return Collections.emptyList();

        var route =
                request.getRoutePoints();

        var resources =
                shipping.getAssignedResources();

        var actors =
                Stream.concat(
                                resources.stream()
                                        .flatMap(i -> i.getDriversIds().stream())
                                        .map(d -> coreService.driver(shipping.getCompanyId(), d))
                                        .flatMap(Optional::stream)
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
                                "online",
                                OffsetDateTime.now(clock)
                        )
                );

        var now = OffsetDateTime.now(clock);
        var info = shipping.getShippingRequestInfo();
        var supply = shipping.getSelectedCarSupply();
        var times =
                supply.stream()
                        .collect(
                                Collectors.toMap(
                                        SelectedCarSupply::getRoutePointId,
                                        SelectedCarSupply::getCarSupplyAt
                                )
                        );
        var min =
                times.values().stream()
                        .min(Comparator.comparing(x -> x))
                        .orElse(OffsetDateTime.now(clock));

        var barrier = now.plusDays(7);

        var status =
                min.isBefore(barrier)
                        ? TaskDto.Status.ACTIVE
                        : TaskDto.Status.PENDING;
        var contacts =
                Stream.concat(
                                tasksService.pipeline(String.valueOf(shipping.getId()))
                                        .stream()
                                        .map(TaskDto::getContacts)
                                        .flatMap(List::stream),
                                Optional.ofNullable(author)
                                        .flatMap(a -> peopleService.user(a.getId()))
                                        .filter(u -> Objects.nonNull(u.getPhone()))
                                        .map(u -> new TaskDto.Contact(u.getFullName(), TaskDto.Contact.Role.LOGIST, u.getPhone()))
                                        .stream()
                        )
                        .toList();

        var assignments =
                actors.stream()
                        .map(actor ->
                                Map.entry(
                                        new TaskAssignmentDto(
                                                uuidGen.gen(actor, shipping.getId()),
                                                actor,
                                                String.valueOf(shipping.getId()),
                                                OffsetDateTime.now(clock)
                                        ),
                                        new TaskDto<TaskEntity, TaskPayload>(
                                                uuidGen.gen(actor, shipping.getId()),
                                                TaskDto.Type.READINESS_CHECK,
                                                status,
                                                String.valueOf(shipping.getId()),
                                                "tasks-service",
                                                story,
                                                min,
                                                contacts,
                                                new PolymorphMap<>(raw),
                                                new PolymorphMap<>(raw),
                                                Objects.requireNonNullElse(info.getComment(), "")
                                        )
                                )
                        )
                        .toList();

        var agreements =
                tasksService.pipeline(String.valueOf(shipping.getId())).stream()
                        .filter(t -> Objects.equals(t.getType(), TaskDto.Type.READINESS_CHECK))
                        .filter(t -> Objects.equals(t.getStatus(), TaskDto.Status.DONE))
                        .map(t -> assignmentService.assignment(t.getId()))
                        .flatMap(List::stream)
                        .map(TaskAssignmentDto::getActor)
                        .toList();

        for (var a : assignments) {
            var task = a.getValue();
            var assignment = a.getKey();
            if (agreements.contains(assignment.getActor()))
                continue;

            tasksService.persist(task);
            assignmentService.persist(assignment);
        }

        var delete =
                assignmentService.pipeline(String.valueOf(shipping.getId())).stream()
                        .filter(a -> !actors.contains(a.getActor()))
                        .toList();
        for (var can : delete)
            assignmentService.delete(can);

        var plan =
                route.stream()
                        .flatMap(point -> {
                                    var location = point.getLocation();
                                    var time = times.getOrDefault(point.getId(), OffsetDateTime.now(clock));

                                    return Stream.of(
                                            new TaskDto<TaskEntity, TaskPayload>(
                                                    uuidGen.gen(point.getId(), shipping.getId(), TaskDto.Type.MOVEMENT_START),
                                                    TaskDto.Type.MOVEMENT_START,
                                                    TaskDto.Status.ACTIVE,
                                                    String.valueOf(shipping.getId()),
                                                    "tasks-service",
                                                    story,
                                                    time.plusMinutes(5),
                                                    contacts,
                                                    new PolymorphMap<>(raw),
                                                    pointConverter.apiToCore(point),
                                                    ""
                                            ),
                                            new TaskDto<TaskEntity, TaskPayload>(
                                                    uuidGen.gen(point.getId(), shipping.getId(), TaskDto.Type.WAYPOINT_REACH),
                                                    TaskDto.Type.WAYPOINT_REACH,
                                                    TaskDto.Status.ACTIVE,
                                                    String.valueOf(shipping.getId()),
                                                    "tasks-service",
                                                    story,
                                                    time.plusMinutes(10),
                                                    contacts,
                                                    new PolymorphMap<>(raw),
                                                    pointConverter.apiToCore(point),
                                                    ""
                                            ),
                                            new TaskDto<TaskEntity, TaskPayload>(
                                                    uuidGen.gen(point.getId(), shipping.getId(), TaskDto.Type.DOCKING_START),
                                                    TaskDto.Type.DOCKING_START,
                                                    TaskDto.Status.ACTIVE,
                                                    String.valueOf(shipping.getId()),
                                                    "tasks-service",
                                                    story,
                                                    time.plusMinutes(15),
                                                    contacts,
                                                    new PolymorphMap<>(raw),
                                                    pointConverter.apiToCore(point),
                                                    ""
                                            ),
                                            new TaskDto<TaskEntity, TaskPayload>(
                                                    uuidGen.gen(point.getId(), shipping.getId(), TaskDto.Type.DOCKING_END),
                                                    TaskDto.Type.DOCKING_END,
                                                    TaskDto.Status.ACTIVE,
                                                    String.valueOf(shipping.getId()),
                                                    "tasks-service",
                                                    story,
                                                    time.plusMinutes(20),
                                                    contacts,
                                                    new PolymorphMap<>(raw),
                                                    pointConverter.apiToCore(point),
                                                    ""
                                            )
                                    );
                                }
                        )
                        .toList();

        var max =
                times.values().stream()
                        .max(Comparator.comparing(x -> x))
                        .orElse(OffsetDateTime.now(clock));
        plan = Stream.concat(
                        plan.stream(),
                        Stream.of(
                                new TaskDto<TaskEntity, TaskPayload>(
                                        uuidGen.gen(shipping.getId()),
                                        TaskDto.Type.SHIPPING_COMPLETE,
                                        TaskDto.Status.ACTIVE,
                                        String.valueOf(shipping.getId()),
                                        "tasks-service",
                                        story,
                                        max.plusMinutes(60),
                                        contacts,
                                        new PolymorphMap<>(raw),
                                        new PolymorphMap<>(raw),
                                        ""
                                )
                        )
                )
                .toList();


        var current =
                tasksService.pipeline(String.valueOf(shipping.getId()))
                        .stream()
                        .filter(t -> !Objects.equals(t.getType(), TaskDto.Type.READINESS_CHECK))
                        .toList();

        // удаляем ненужные
        for (var can : current) {
            if (plan.stream().noneMatch(p -> Objects.equals(p.getId(), can.getId()))) {
                assignmentService.delete(can.getId(), can.getPipeline());
                tasksService.delete(can);
            }
        }

        // добавляем новые


        for (var can : plan) {
            if (current.stream().noneMatch(p -> Objects.equals(p.getId(), can.getId()))) {
                tasksService.persist(can);
                actors.stream()
                        .filter(agreements::contains)
                        .map(actor -> new TaskAssignmentDto(
                                        can.getId(),
                                        actor,
                                        can.getPipeline(),
                                        OffsetDateTime.now(clock)
                                )
                        )
                        .forEach(assignmentService::persist);
            }
        }

        return tasksService.pipeline(String.valueOf(shipping.getId()));
    }

}
