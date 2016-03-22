package ot.internal;

import java.util.List;
import java.util.SortedSet;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Stas on 3/12/16.
 */
class Insert extends TextChange {
    final String text;

    public Insert(String text) {
        this.text = text;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        text.buffer.insert(pos, this.text);
        List<Integer> list = newArrayList((text.markup.asMap().tailMap(pos).keySet()));
        int d = this.text.length();
        for (int i = list.size() - 1; i >= 0; i--) {
            int newPos = list.get(i) + d;
            SortedSet<Markup> m = text.markup.removeAll(list.get(i));
            m.forEach(e -> e.fireShift(d, newPos));
            text.markup.putAll(newPos, m);
        }
        return text;
    }

    @Override
    int offset() {
        return text.length();
    }

    @Override
    public String toString() {
        return "INS: " + text;
    }
}
