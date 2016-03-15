package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
class InsertChars extends Change {
    private final String text;

    public InsertChars(String text) {
        this.text = text;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        text.buffer.insert(pos, this.text);
        return text;
    }

    @Override
    int offset() {
        return text.length();
    }
}
