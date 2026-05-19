package test.builder;
import test.model.Question;
import test.observer.TestObserver;
import test.strategy.ScoringStrategy;
import test.strategy.StrictScoringStrategy;
import java.util.ArrayList;
import java.util.List;
public class TestBuilder {
    private String title = "Без названия";
    private final List<Question> questions = new ArrayList<>();
    private ScoringStrategy scoringStrategy = new StrictScoringStrategy();
    private final List<TestObserver> observers = new ArrayList<>();
    public TestBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    public TestBuilder addQuestion(Question question) {
        this.questions.add(question);
        return this;
    }
    public TestBuilder withScoringStrategy(ScoringStrategy strategy) {
        this.scoringStrategy = strategy;
        return this;
    }
    public TestBuilder addObserver(TestObserver observer) {
        this.observers.add(observer);
        return this;
    }
    public Test build() {
        if (questions.isEmpty()) {
            throw new IllegalStateException("Тест должен содержать хотя бы один вопрос!");
        }
        return new Test(title, new ArrayList<>(questions), scoringStrategy, new ArrayList<>(observers));
    }
}

