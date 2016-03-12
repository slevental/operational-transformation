package ot.internal;

import java.util.LinkedList;

import static java.util.stream.Collectors.toList;
import static ot.internal.DiffUtils.INSTANCE;

/**
 * Created by Stas on 3/12/16.
 */
public class Text {
    final GapBuffer buffer;

    private Text(GapBuffer buffer) {
        this.buffer = buffer;
    }

    public static Text wrap(String str) {
        return new Text(new GapBuffer(str));
    }

    public Change diff(Text that) {
        LinkedList<DiffUtils.Diff> res = INSTANCE.diff_main(
                this.buffer.toString(),
                that.buffer.toString()
        );
        INSTANCE.diff_cleanupSemantic(res);
        return new Changes(res.stream().map(Text::convert).collect(toList()));
    }

    private static Change convert(DiffUtils.Diff diff) {
        switch (diff.operation) {
            case DELETE:
                return new Delete(diff.text);
            case INSERT:
                return new Insert(diff.text);
            case EQUAL:
                return new Equal(diff.text.length());
            default:
                throw new IllegalArgumentException("Unsupported diff type: " + diff.operation);
        }
    }

    public static Text copy(Text original) {
        return new Text(new GapBuffer(original.buffer.toString()));
    }

    public Text apply(Change change) throws ValidationException {
        return change.apply(0, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Text text = (Text) o;
        return buffer != null ? buffer.equals(text.buffer) : text.buffer == null;
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return buffer != null ? buffer.hashCode() : 0;
    }
}
