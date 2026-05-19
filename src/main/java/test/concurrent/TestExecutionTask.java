package test.concurrent;
import test.builder.Test;
import test.model.Answer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class TestExecutionTask implements Runnable {
    private final String userId;
    private final Test test;
    private final Map<Integer, Answer> answers;
    private final ThreadSafeTestResultStorage resultStorage;
    private final Object synchronizationLock;
    public TestExecutionTask(String userId, Test test, Map<Integer, Answer> answers,
                            ThreadSafeTestResultStorage resultStorage, Object lock) {
        this.userId = userId;
        this.test = test;
        this.answers = new ConcurrentHashMap<>(answers);
        this.resultStorage = resultStorage;
        this.synchronizationLock = lock;
    }
    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        System.out.println("[THREAD] ▶ Поток " + currentThread.getName() + 
                         " начал тест для пользователя: " + userId);
        long startTime = System.currentTimeMillis();
        try {
            synchronized (synchronizationLock) {
                System.out.println("[SYNC] Пользователь " + userId + 
                                 " получил доступ к критической секции");
                Thread.sleep(500);
            }
            double score = test.run(answers);
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            TestResult result = new TestResult(test.getTitle(), userId, score, executionTime);
            resultStorage.addResult(result);
            System.out.println("[THREAD] ✓ Поток " + currentThread.getName() + 
                             " завершил тест для " + userId + 
                             " (Результат: " + String.format("%.1f", score) + " баллов)");
        } catch (InterruptedException e) {
            System.err.println("[ERROR] Поток " + currentThread.getName() + 
                             " был прерван: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("[ERROR] Ошибка при выполнении теста для " + userId + 
                             ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    public String getUserId() { return userId; }
    public Test getTest() { return test; }
    public Map<Integer, Answer> getAnswers() { return answers; }
}

