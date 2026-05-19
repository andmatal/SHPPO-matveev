package test.model;
import java.util.Collections;
import java.util.Set;
public class Answer {
    private final int singleIndex;
    private final Set<Integer> multiIndices;
    private final String text;
    private Answer(int singleIndex, Set<Integer> multiIndices, String text) {
        this.singleIndex = singleIndex;
        this.multiIndices = multiIndices;
        this.text = text;
    }
    public static Answer ofSingle(int index) {
        return new Answer(index, Collections.emptySet(), "");
    }
    public static Answer ofMulti(Set<Integer> indices) {
        return new Answer(-1, indices, "");
    }
    public static Answer ofText(String text) {
        return new Answer(-1, Collections.emptySet(), text);
    }
    public int getSingleIndex()           { return singleIndex; }
    public Set<Integer> getMultiIndices() { return multiIndices; }
    public String getText()               { return text; }
}

