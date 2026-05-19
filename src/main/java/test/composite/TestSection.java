package test.composite;
import test.model.Question;
import java.util.ArrayList;
import java.util.List;
public class TestSection implements TestComponent {
    private final String title;
    private final List<TestComponent> children = new ArrayList<>();
    public TestSection(String title) {
        this.title = title;
    }
    public void add(TestComponent component) {
        children.add(component);
    }
    @Override
    public String getTitle() { return title; }
    @Override
    public List<Question> getAllQuestions() {
        List<Question> result = new ArrayList<>();
        for (TestComponent child : children) {
            result.addAll(child.getAllQuestions());
        }
        return result;
    }
    @Override
    public int getQuestionCount() {
        return children.stream().mapToInt(TestComponent::getQuestionCount).sum();
    }
}

