package test.factory;
import test.model.*;
import java.util.List;
import java.util.Set;
public class QuestionFactoryImpl implements QuestionFactory {
    @Override
    public Question createSingleChoice(String text, List<String> options, int correctIndex) {
        return new SingleChoiceQuestion(text, options, correctIndex);
    }
    @Override
    public Question createMultipleChoice(String text, List<String> options, Set<Integer> correctIndices) {
        return new MultipleChoiceQuestion(text, options, correctIndices);
    }
    @Override
    public Question createText(String text, String correctAnswer) {
        return new TextQuestion(text, correctAnswer);
    }
}

