package top.kangyaocoding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

    private final TaskSubmitter taskSubmitter;

    public Application(TaskSubmitter taskSubmitter) {
        this.taskSubmitter = taskSubmitter;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    // 监听 ApplicationReadyEvent 确保任务在 Spring Boot 启动完成后才提交
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        taskSubmitter.submitTasks();
    }
}
