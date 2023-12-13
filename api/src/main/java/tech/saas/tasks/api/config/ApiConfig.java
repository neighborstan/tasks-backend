package tech.saas.tasks.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import tech.saas.tasks.core.config.CoreConfig;

@Configuration
@ComponentScan({
        "tech.saas.tasks.api.controllers",
        "tech.saas.tasks.api.exceptions",
        "tech.saas.tasks.api.converters"
})
@Import(CoreConfig.class)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class ApiConfig {


}
