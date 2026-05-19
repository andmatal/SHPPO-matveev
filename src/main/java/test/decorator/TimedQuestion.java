package test.decorator;
import test.model.Question;
public class TimedQuestion extends QuestionDecorator {
    private final int timeLimitSeconds;
    public TimedQuestion(Question wrapped, int timeLimitSeconds) {
        super(wrapped);
        this.timeLimitSeconds = timeLimitSeconds;
    }
    @Override
    public String getText() {
        return wrapped.getText() + " [Время: " + timeLimitSeconds + " сек]";
    }
    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }
}

