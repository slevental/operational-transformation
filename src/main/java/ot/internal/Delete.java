package ot.internal;

import java.util.SortedSet;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Stas on 3/12/16.
 */
class Delete extends TextChange {
    final int len;

    public Delete(int len) {
        this.len = len;
    }

    @Override
    Text apply(int lo, Text text) throws ValidationException {
        int hi = lo + len;
        text.buffer.delete(lo, hi);
        for (Integer p : newArrayList((text.markup.asMap().tailMap(lo, false).keySet()))) {
            SortedSet<Markup> m = text.markup.removeAll(p);
            int d = p >= hi ? lo - hi : lo - p;
            m.forEach(e -> e.fireShift(d, p + d));
            text.markup.putAll(p + d, m);
        }
        return text;
    }

    @Override
    int offset() {
        return 0;
    }

    @Override
    public String toString() {
        return "DEL: " + len;
    }
}
