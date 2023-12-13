package tech.saas.tasks.watcher.processors;

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
import tech.saas.tasks.core.models.ShippingStatus;
import tech.saas.tasks.core.uc.GenerateTasksUC;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class EventsProcessor {

    private final GenerateTasksUC generateTasksUC;
    private final ObjectMapper mapper;
    private final Clock clock;

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
//                                    "crud.shipping.create",
                                    "crud.shipping.update"
                            },
                            ignoreDeclarationExceptions = "true"
                    )
            }
    )
    public void handleEvent(Message message) throws IOException {

        var body = new String(message.getBody(), StandardCharsets.UTF_8);
        var shipping = mapper.readValue(body, Shipping.class);
        var status = shipping.getStatus();

        if (!Objects.equals(status.getCodeName(), ShippingStatus.CodeNameEnum.TRIP_WAITING))
            return;

        generateTasksUC.apply(shipping);

    }
}