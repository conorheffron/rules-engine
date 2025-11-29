package ie.example.flag.evaluator;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableProcessApplication
public class ApiApplication {

    private final RuntimeService runtimeService;

    @Autowired
    public ApiApplication(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    static void main(String... args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @EventListener
    public void processPostDeploy(PostDeployEvent event) {
        runtimeService.startProcessInstanceByKey("loanApproval");
    }
}
