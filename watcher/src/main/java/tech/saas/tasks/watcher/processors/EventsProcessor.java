package tech.saas.tasks.watcher.processors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.Shipping;
import tech.saas.tasks.core.uc.CancelTasksUC;
import tech.saas.tasks.core.uc.GenerateTasksUC;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class EventsProcessor {

    private final ObjectMapper mapper;
    private final GenerateTasksUC generateTasksUC;
    private final CancelTasksUC cancelTasksUC;

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(
                                    name = "${services.rabbit.queue}",
                                    durable = "true",
                                    autoDelete = "false",
                                    ignoreDeclarationExceptions = "true",
                                    arguments = @Argument(
                                            name = "x-queue-type",
                                            value = "classic"
                                    )
                            ),
                            exchange = @Exchange(
                                    name = "${services.rabbit.exchange}",
                                    type = "topic"
                            ),
                            key = {
                                    "crud.driver.create",
                                    "crud.driver.update",
                                    "crud.shipping.update"
                            },
                            ignoreDeclarationExceptions = "true"
                    )
            }
    )
    public void handleEvent(Message message) throws IOException {

        var body = new String(message.getBody(), StandardCharsets.UTF_8);
        var event = mapper.readValue(body, new TypeReference<Map<String, ?>>() {});
        var shipping = mapper.convertValue(event.get("payload"), Shipping.class);
        var status = shipping.getStatus();

        switch (status.getCodeName()) {
            case DONE, APPROVAL_WAITING, RESOURCES_WAITING -> {
            }

            case IN_WAY, TRIP_WAITING ->
                    generateTasksUC.apply(shipping, mapper.convertValue(event.get("payload"), new TypeReference<Map<String, ?>>() {}));

            case CANCELED,
                    CANCELED_BY_CARGO_OWNING_COMPANY,
                    CANCELED_BY_TRANSPORT_COMPANY,
                    FAILED_BY_CARGO_OWNING_COMPANY,
                    FAILED_BY_TRANSPORT_COMPANY -> cancelTasksUC.apply(shipping);
        }

    }
}