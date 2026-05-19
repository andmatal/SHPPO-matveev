package test.strategy;
import test.model.Answer;
import test.model.Question;
import java.util.List;
import java.util.Map;
public interface ScoringStrategy {
    double calculateScore(List<Question> questions, Map<Integer, Answer> answers);
    String getStrategyName();
}

