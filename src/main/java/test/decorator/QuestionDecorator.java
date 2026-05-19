package test.decorator;
import test.model.Answer;
import test.model.Question;
import java.util.List;
public abstract class QuestionDecorator implements Question {
    protected final Question wrapped;
    public QuestionDecorator(Question wrapped) {
        this.wrapped = wrapped;
    }
    @Override public String getText()              { return wrapped.getText(); }
    @Override public List<String> getOptions()     { return wrapped.getOptions(); }
    @Override public boolean checkAnswer(Answer a) { return wrapped.checkAnswer(a); }
    @Override public String getType()              { return wrapped.getType(); }
}

