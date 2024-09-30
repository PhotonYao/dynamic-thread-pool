package top.kangyaocoding;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Configurable
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(ExecutorService threadPoolExecutor01, ExecutorService threadPoolExecutor02) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) {
                Random random = new Random();
                while (true) {
                    // 创建一个随机时间生成器
                    int initialDelay = random.nextInt(50) + 1; // 1到10秒之间
                    int sleepTime = random.nextInt(50) + 1; // 1到10秒之间

                    // 提交任务到线程池1
                    threadPoolExecutor01.submit(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(initialDelay);
                            System.out.println("Task 1 started after " + initialDelay + " seconds.");

                            TimeUnit.SECONDS.sleep(sleepTime);
                            System.out.println("Task 1 executed for " + sleepTime + " seconds.");
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

                    // 提交任务到线程池2
                    threadPoolExecutor02.submit(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(initialDelay);
                            System.out.println("Task 2 started after " + initialDelay + " seconds.");

                            TimeUnit.SECONDS.sleep(sleepTime);
                            System.out.println("Task 2 executed for " + sleepTime + " seconds.");
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

                    try {
                        Thread.sleep(random.nextInt(50) + 1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
    }
}
