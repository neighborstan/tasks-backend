package tech.saas.tasks.watcher.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tech.saas.tasks.core.config.CoreConfig;

@Configuration
@Import(CoreConfig.class)
@ComponentScan({
        "tech.saas.tasks.watcher.config",
        "tech.saas.tasks.watcher.processors"
})
public class WatcherConfig {


}
