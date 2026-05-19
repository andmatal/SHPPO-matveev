package test.composite;
import test.model.Question;
import java.util.Collections;
import java.util.List;
public class TestLeaf implements TestComponent {
    private final Question question;
    public TestLeaf(Question question) {
        this.question = question;
    }
    @Override
    public String getTitle() { return question.getText(); }
    @Override
    public List<Question> getAllQuestions() {
        return Collections.singletonList(question);
    }
    @Override
    public int getQuestionCount() { return 1; }
}

