package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
public abstract class Change {

    public Change transform(Change diff1) {
        return this;
    }

    abstract Text apply(int pos, Text text) throws ValidationException;
    abstract int offset();
}
