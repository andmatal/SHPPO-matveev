package test.builder;
import test.model.Answer;
import test.model.Question;
import test.observer.TestObserver;
import test.strategy.ScoringStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class Test {
    private final String title;
    private final List<Question> questions;
    private final ScoringStrategy scoringStrategy;
    private final List<TestObserver> observers;
    public Test(String title, List<Question> questions,
                ScoringStrategy scoringStrategy, List<TestObserver> observers) {
        this.title = title;
        this.questions = questions;
        this.scoringStrategy = scoringStrategy;
        this.observers = observers;
    }
    public String getTitle()                  { return title; }
    public List<Question> getQuestions()      { return questions; }
    public ScoringStrategy getScoringStrategy() { return scoringStrategy; }
    public double run(Map<Integer, Answer> answers) {
        for (TestObserver o : observers) o.onTestStarted(title, questions.size());
        double score = scoringStrategy.calculateScore(questions, answers);
        for (TestObserver o : observers) o.onTestFinished(title, score);
        return score;
    }
}

