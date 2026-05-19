package test.factory;
import test.model.Question;
import java.util.List;
import java.util.Set;
public interface QuestionFactory {
    Question createSingleChoice(String text, List<String> options, int correctIndex);
    Question createMultipleChoice(String text, List<String> options, Set<Integer> correctIndices);
    Question createText(String text, String correctAnswer);
}

