package ot.internal;

import static ot.internal.Transform.transformMarkupAgainstInsert;

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
        transformMarkupAgainstInsert(text.markup, pos, this.text.length());
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
