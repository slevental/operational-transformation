package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
class Equal extends Change {
    private final int length;

    public Equal(int length) {
        this.length = length;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        return text;
    }

    @Override
    int offset() {
        return length;
    }
}
