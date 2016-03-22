package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
class Retain extends TextChange {
    private final int length;

    public Retain(int length) {
        this.length = length;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        return text;
    }

    @Override
    int cursorOffset() {
        return length;
    }

    @Override
    public String toString() {
        return "SKIP: " + length;
    }
}
