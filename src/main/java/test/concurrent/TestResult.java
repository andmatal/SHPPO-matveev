package test.concurrent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class TestResult {
    private final String testTitle;
    private final String userId;
    private final double score;
    private final long executionTimeMs;
    private final LocalDateTime completedAt;
    private final Thread executedThread;
    public TestResult(String testTitle, String userId, double score, long executionTimeMs) {
        this.testTitle = testTitle;
        this.userId = userId;
        this.score = score;
        this.executionTimeMs = executionTimeMs;
        this.completedAt = LocalDateTime.now();
        this.executedThread = Thread.currentThread();
    }
    public String getTestTitle() { return testTitle; }
    public String getUserId() { return userId; }
    public double getScore() { return score; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Thread getExecutedThread() { return executedThread; }
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.format("[%s] Пользователь: %s | Тест: %s | Результат: %.1f баллов | " +
                           "Время: %dms | Поток: %s",
                completedAt.format(formatter), userId, testTitle, score,
                executionTimeMs, executedThread.getName());
    }
}

