
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "config",
    "entity",
    "exception",
    "log",
    "repository",
    "reqDTO",
    "resDTO",
    "responseHandler",
    "security",
    "service",
    "util",
    "workflow"
})
@EntityScan(basePackages = {
    "entity"
})
@EnableJpaRepositories(basePackages = {
    "repository"
})
public class FldcProcessApplication {

    public static void main(String[] args) {
        SpringApplication.run(FldcProcessApplication.class, args);
    }
}
