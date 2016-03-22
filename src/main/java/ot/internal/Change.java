package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
public abstract class Change {

    abstract Text apply(int pos, Text text) throws ValidationException;

    abstract int cursorOffset();

    int revision() {
        return 0;
    }

    int changeSize() {
        return cursorOffset();
    }
}
