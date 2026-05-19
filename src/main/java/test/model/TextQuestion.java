package test.model;
import java.util.Collections;
import java.util.List;
public class TextQuestion implements Question {
    private final String text;
    private final String correctAnswer;
    public TextQuestion(String text, String correctAnswer) {
        this.text = text;
        this.correctAnswer = correctAnswer;
    }
    @Override public String getText()            { return text; }
    @Override public List<String> getOptions()   { return Collections.emptyList(); }
    @Override public String getType()            { return "TEXT"; }
    @Override
    public boolean checkAnswer(Answer answer) {
        return correctAnswer.equalsIgnoreCase(answer.getText().trim());
    }
}

