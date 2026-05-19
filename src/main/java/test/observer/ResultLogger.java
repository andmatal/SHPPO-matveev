package test.observer;
public class ResultLogger implements TestObserver {
    @Override
    public void onTestStarted(String testTitle, int questionCount) {
        System.out.println("[Лог] Начат тест: \"" + testTitle + "\", вопросов: " + questionCount);
    }
    @Override
    public void onTestFinished(String testTitle, double score) {
        int percent = (int) Math.round(score * 100);
        System.out.println("[Лог] Тест завершён: \"" + testTitle + "\", результат: " + percent + "%");
    }
}

