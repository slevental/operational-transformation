package ot.internal;

import java.util.List;

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
        for (int i = list.size() - 1; i >= 0; i--) {
            text.markup.putAll(list.get(i) + this.text.length(),
                    text.markup.removeAll(list.get(i)));
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
