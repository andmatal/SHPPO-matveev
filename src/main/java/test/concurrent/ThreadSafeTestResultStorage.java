package test.concurrent;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
public class ThreadSafeTestResultStorage {
    private final List<TestResult> results = Collections.synchronizedList(new ArrayList<>());
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private volatile int totalTestsCompleted = 0;
    public void addResult(TestResult result) {
        lock.writeLock().lock();
        try {
            results.add(result);
            totalTestsCompleted++;
            System.out.println("[STORAGE] ✓ Результат добавлен: " + result.getUserId() + 
                             " | Всего тестов: " + totalTestsCompleted);
        } finally {
            lock.writeLock().unlock();
        }
    }
    public List<TestResult> getAllResults() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(results);
        } finally {
            lock.readLock().unlock();
        }
    }
    public List<TestResult> getResultsByTest(String testTitle) {
        lock.readLock().lock();
        try {
            List<TestResult> filtered = new ArrayList<>();
            for (TestResult result : results) {
                if (result.getTestTitle().equals(testTitle)) {
                    filtered.add(result);
                }
            }
            return filtered;
        } finally {
            lock.readLock().unlock();
        }
    }
    public double getAverageScore(String testTitle) {
        lock.readLock().lock();
        try {
            List<TestResult> testResults = new ArrayList<>();
            for (TestResult result : results) {
                if (result.getTestTitle().equals(testTitle)) {
                    testResults.add(result);
                }
            }
            if (testResults.isEmpty()) return 0.0;
            double sum = 0;
            for (TestResult result : testResults) {
                sum += result.getScore();
            }
            return sum / testResults.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    public synchronized String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append("СТАТИСТИКА ТЕСТИРОВАНИЯ (многопоточная система)\n");
        sb.append("=".repeat(70)).append("\n");
        lock.readLock().lock();
        try {
            sb.append("Всего завершено тестов: ").append(totalTestsCompleted).append("\n");
            sb.append("Всего пользователей: ").append(results.size()).append("\n");
            if (results.isEmpty()) {
                sb.append("Нет данных для анализа.\n");
            } else {
                double totalScore = 0;
                long totalTime = 0;
                Map<String, List<TestResult>> byTest = new HashMap<>();
                for (TestResult result : results) {
                    totalScore += result.getScore();
                    totalTime += result.getExecutionTimeMs();
                    byTest.computeIfAbsent(result.getTestTitle(), k -> new ArrayList<>())
                          .add(result);
                }
                sb.append("\nСредний результат всех тестов: ")
                  .append(String.format("%.1f", totalScore / results.size()))
                  .append(" баллов\n");
                sb.append("Среднее время выполнения: ")
                  .append(totalTime / results.size())
                  .append(" мс\n");
                sb.append("\nРезультаты по каждому тесту:\n");
                for (Map.Entry<String, List<TestResult>> entry : byTest.entrySet()) {
                    double avg = entry.getValue().stream()
                                     .mapToDouble(TestResult::getScore)
                                     .average()
                                     .orElse(0);
                    sb.append("  • ").append(entry.getKey()).append(": ")
                      .append(entry.getValue().size()).append(" пользователей, ")
                      .append("средний результат ").append(String.format("%.1f", avg))
                      .append("\n");
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        sb.append("=".repeat(70)).append("\n");
        return sb.toString();
    }
    public int getTotalTestsCompleted() {
        return totalTestsCompleted;
    }
    public synchronized void clear() {
        lock.writeLock().lock();
        try {
            results.clear();
            totalTestsCompleted = 0;
            System.out.println("[STORAGE] Хранилище очищено");
        } finally {
            lock.writeLock().unlock();
        }
    }
}

