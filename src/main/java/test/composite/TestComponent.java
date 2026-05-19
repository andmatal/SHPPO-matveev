package test.composite;
import test.model.Question;
import java.util.List;
public interface TestComponent {
    String getTitle();
    List<Question> getAllQuestions();
    int getQuestionCount();
}

