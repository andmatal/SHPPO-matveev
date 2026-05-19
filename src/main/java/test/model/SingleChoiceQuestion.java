package test.model;
import java.util.List;
public class SingleChoiceQuestion implements Question {
    private final String text;
    private final List<String> options;
    private final int correctIndex;
    public SingleChoiceQuestion(String text, List<String> options, int correctIndex) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }
    @Override public String getText()            { return text; }
    @Override public List<String> getOptions()   { return options; }
    @Override public String getType()            { return "SINGLE_CHOICE"; }
    @Override
    public boolean checkAnswer(Answer answer) {
        return answer.getSingleIndex() == correctIndex;
    }
}

