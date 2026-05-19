package test.observer;
public interface TestObserver {
    void onTestStarted(String testTitle, int questionCount);
    void onTestFinished(String testTitle, double score);
}

