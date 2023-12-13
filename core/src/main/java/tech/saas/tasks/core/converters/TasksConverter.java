package tech.saas.tasks.core.converters;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.Qualifier;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.Task;
import tech.saas.tasks.core.models.TaskDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface TasksConverter {
    Task coreToApi(TaskDto x);

}
