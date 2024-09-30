package top.kangyaocoding;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TaskSubmitter {

    private final ExecutorService threadPoolExecutor01;
    private final ExecutorService threadPoolExecutor02;

    public TaskSubmitter(ExecutorService threadPoolExecutor01, ExecutorService threadPoolExecutor02) {
        this.threadPoolExecutor01 = threadPoolExecutor01;
        this.threadPoolExecutor02 = threadPoolExecutor02;
    }

    public void submitTasks() {
        Random random = new Random();

        while (true) {
            int initialDelay = random.nextInt(10) + 1; // 1到10秒之间的延迟
            int sleepTime = random.nextInt(10) + 1; // 1到10秒的模拟任务执行时间

            // 提交任务到线程池 1
            threadPoolExecutor01.submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(initialDelay);
                    TimeUnit.SECONDS.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // 提交任务到线程池 2
            threadPoolExecutor02.submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(initialDelay);
                    TimeUnit.SECONDS.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            try {
                Thread.sleep(random.nextInt(50) + 1); // 模拟任务提交间隔
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
