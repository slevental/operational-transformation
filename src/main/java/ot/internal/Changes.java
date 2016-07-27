package ot.internal;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Stas on 3/12/16.
 */
public class Changes extends Change {
    final List<Change> changes;

    Changes(Change... changes) {
        this(Arrays.asList(changes));
    }

    Changes(Iterable<Change> changes) {
        this.changes = Lists.newArrayList(changes);
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        for (Change change : changes) {
            change.apply(pos, text);
            pos += change.offset();
        }
        return text;
    }

    @Override
    int offset() {
        int pos = 0;
        for (Change change : changes) pos += change.offset();
        return pos;
    }
}
