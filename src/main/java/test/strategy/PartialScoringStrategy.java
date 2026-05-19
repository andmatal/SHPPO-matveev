package test.strategy;
import test.model.Answer;
import test.model.MultipleChoiceQuestion;
import test.model.Question;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class PartialScoringStrategy implements ScoringStrategy {
    @Override
    public double calculateScore(List<Question> questions, Map<Integer, Answer> answers) {
        if (questions.isEmpty()) return 0.0;
        double totalScore = 0.0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            Answer answer = answers.get(i);
            if (answer == null) continue;
            if ("MULTIPLE_CHOICE".equals(q.getType())) {
                totalScore += partialMultiple((MultipleChoiceQuestion) q, answer.getMultiIndices());
            } else {
                totalScore += q.checkAnswer(answer) ? 1.0 : 0.0;
            }
        }
        return totalScore / questions.size();
    }
    private double partialMultiple(MultipleChoiceQuestion q, Set<Integer> userAnswer) {
        if (q.checkAnswer(Answer.ofMulti(userAnswer))) return 1.0;
        return 0.0;
    }
    @Override
    public String getStrategyName() { return "Частичная оценка"; }
}

