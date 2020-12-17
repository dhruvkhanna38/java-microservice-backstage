package {{cookiecutter.group_id}}.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import {{cookiecutter.group_id}}.example.config.ExampleApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ExampleApplicationProperties.class})
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
