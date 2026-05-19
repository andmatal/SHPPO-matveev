package test.concurrent;
import test.builder.Test;
import test.model.Answer;
import java.util.*;
import java.util.concurrent.*;
public class TestExecutorPool {
    private final ExecutorService executorService;
    private final ThreadSafeTestResultStorage resultStorage;
    private final Object synchronizationLock = new Object();
    private volatile boolean isRunning = false;
    private final CountDownLatch completionLatch;
    public TestExecutorPool(int poolSize, int queueCapacity) {
        this.executorService = Executors.newFixedThreadPool(poolSize);
        this.resultStorage = new ThreadSafeTestResultStorage();
        this.completionLatch = new CountDownLatch(queueCapacity);
    }
    public void start() {
        if (isRunning) {
            System.out.println("[POOL] Пул потоков уже запущен");
            return;
        }
        isRunning = true;
        System.out.println("[POOL] ✓ Пул потоков запущен с " + getPoolSize() + " потоками");
    }
    public boolean submitTest(String userId, Test test, Map<Integer, Answer> answers) {
        if (!isRunning) {
            System.out.println("[POOL] Ошибка: пул потоков не запущен");
            return false;
        }
        TestExecutionTask task = new TestExecutionTask(userId, test, answers, 
                                                       resultStorage, synchronizationLock);
        System.out.println("[QUEUE] Задача для " + userId + " добавлена в пул");
        executorService.execute(() -> {
            try {
                task.run();
            } finally {
                completionLatch.countDown();
            }
        });
        return true;
    }
    public void shutdown(long timeoutSeconds) {
        if (!isRunning) return;
        System.out.println("\n[POOL] Завершение работы пула потоков...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                System.out.println("[POOL] ⚠ Таймаут при завершении, принудительное выключение");
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("[POOL] ✗ Пул потоков не завершился");
                }
            } else {
                System.out.println("[POOL] ✓ Все потоки завершены корректно");
            }
        } catch (InterruptedException e) {
            System.err.println("[POOL] Ошибка при завершении: " + e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        isRunning = false;
    }
    public int getPoolSize() {
        return ((ThreadPoolExecutor) executorService).getCorePoolSize();
    }
    public int getActiveThreads() {
        return ((ThreadPoolExecutor) executorService).getActiveCount();
    }
    public ThreadSafeTestResultStorage getResultStorage() {
        return resultStorage;
    }
    public List<TestResult> getAllResults() {
        return resultStorage.getAllResults();
    }
    public String getStatistics() {
        return resultStorage.getStatistics();
    }
    public boolean isRunning() {
        return isRunning;
    }
    public void waitForCompletion() {
        try {
            System.out.println("[POOL] Ожидание завершения всех задач...");
            completionLatch.await();
            System.out.println("[POOL] ✓ Все задачи завершены");
        } catch (InterruptedException e) {
            System.err.println("[POOL] Прерывание при ожидании: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

