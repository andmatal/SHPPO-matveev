package test.model;
import java.util.List;
import java.util.Set;
public class MultipleChoiceQuestion implements Question {
    private final String text;
    private final List<String> options;
    private final Set<Integer> correctIndices;
    public MultipleChoiceQuestion(String text, List<String> options, Set<Integer> correctIndices) {
        this.text = text;
        this.options = options;
        this.correctIndices = correctIndices;
    }
    @Override public String getText()            { return text; }
    @Override public List<String> getOptions()   { return options; }
    @Override public String getType()            { return "MULTIPLE_CHOICE"; }
    @Override
    public boolean checkAnswer(Answer answer) {
        return correctIndices.equals(answer.getMultiIndices());
    }
    public Set<Integer> getCorrectIndices()      { return correctIndices; }
}

