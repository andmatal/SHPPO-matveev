package test.strategy;
import test.model.Answer;
import test.model.Question;
import java.util.List;
import java.util.Map;
public class StrictScoringStrategy implements ScoringStrategy {
    @Override
    public double calculateScore(List<Question> questions, Map<Integer, Answer> answers) {
        if (questions.isEmpty()) return 0.0;
        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            Answer answer = answers.get(i);
            if (answer != null && questions.get(i).checkAnswer(answer)) {
                correct++;
            }
        }
        return (double) correct / questions.size();
    }
    @Override
    public String getStrategyName() { return "Строгая оценка"; }
}

