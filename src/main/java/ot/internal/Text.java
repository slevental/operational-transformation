package ot.internal;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Stas on 3/12/16.
 */
public class Text {
    final GapBuffer buffer;
    final TreeMultimap<Integer, Markup> markup = TreeMultimap.create(Ordering.natural(), Ordering.from(
            Comparator.comparing(m -> m.getClass().getName(), Ordering.natural().reversed())
    ));

    private Text(GapBuffer buffer) {
        this.buffer = buffer;
    }

    public static Text wrap(String str) {
        return new Text(new GapBuffer(str));
    }

    public Changes diff(Text that) {
        return DiffUtils.diff(this, that);
    }

    public static Text copy(Text original) {
        return new Text(new GapBuffer(original.buffer.toString()));
    }

    public Text apply(Change change) throws ValidationException {
        return change.apply(0, this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(buffer.toString());
        int offset = 0;
        for (Map.Entry<Integer, Markup> each : markup.entries()) {
            if (each.getValue() instanceof InsertAnnotationEnd) {
                sb.insert(each.getKey() + offset++, "]");
            } else if (each.getValue() instanceof InsertAnnotationStart) {
                sb.insert(each.getKey() + offset++, "[");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Text text = (Text) o;
        if (!buffer.equals(text.buffer)) return false;
        return markup.equals(text.markup);
    }

    @Override
    public int hashCode() {
        int result = buffer.hashCode();
        result = 31 * result + markup.hashCode();
        return result;
    }
}
