package test.model;
import java.util.List;
public interface Question {
    String getText();
    List<String> getOptions();
    boolean checkAnswer(Answer answer);
    String getType();
}

