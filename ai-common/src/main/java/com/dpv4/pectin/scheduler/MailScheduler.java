package com.dpv4.pectin.scheduler;

import com.dpv4.pectin.config.MailConfig;
import com.dpv4.pectin.model.MailMessage;
import com.dpv4.pectin.model.MailTask;
import com.dpv4.pectin.model.SendResult;
import com.dpv4.pectin.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class MailScheduler {

    private final BlockingQueue<MailTask> taskQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final ExecutorService workerPool;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailConfig mailConfig;

    public MailScheduler() {
        this.workerPool = Executors.newFixedThreadPool(4);
    }

    public void start() {
        if (!mailConfig.isMailEnabled()) {
            System.out.println("Mail scheduler is disabled");
            return;
        }

        scheduler.scheduleAtFixedRate(() -> processQueue(), 0, 1, TimeUnit.SECONDS);
        System.out.println("Mail scheduler started");
    }

    public void scheduleMail(MailMessage mailMessage, long delayMs) {
        MailTask task = new MailTask(mailMessage, delayMs, 3);
        taskQueue.offer(task);
        System.out.println("Mail scheduled for delivery in " + delayMs + "ms to: " + mailMessage.getTo());
    }

    public void scheduleMailWithFixedDelay(MailMessage mailMessage, long initialDelayMs, long periodMs) {
        scheduler.scheduleAtFixedRate(() -> {
            SendResult result = mailService.sendWithRetry(mailMessage, 3);
            if (!result.isSuccess()) {
                System.err.println("Failed to send scheduled mail: " + result.getErrorMessage());
            }
        }, initialDelayMs, periodMs, TimeUnit.MILLISECONDS);
    }

    private void processQueue() {
        long currentTime = System.currentTimeMillis();

        for (MailTask task : taskQueue) {
            if (task.getDelayMs() <= currentTime - task.getMailMessage().getCreateTime().toInstant(java.time.ZoneOffset.UTC).toEpochMilli()) {
                if (taskQueue.remove(task)) {
                    workerPool.submit(() -> processTask(task));
                }
            }
        }
    }

    private void processTask(MailTask task) {
        MailMessage mailMessage = task.getMailMessage();
        SendResult result = mailService.sendWithRetry(mailMessage, task.getMaxRetries());

        if (!result.isSuccess() && task.canRetry()) {
            task.incrementRetryCount();
            taskQueue.offer(task);
            System.err.println("Retrying mail to: " + mailMessage.getTo() + ", attempt: " + task.getRetryCount());
        }
    }

    public void shutdown() {
        workerPool.shutdown();
        scheduler.shutdown();
        try {
            if (!workerPool.awaitTermination(60, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int getPendingTaskCount() {
        return taskQueue.size();
    }
}