package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
public class InsertElementStart extends Change {
    @Override
    Text apply(int pos, Text text) throws ValidationException {
        return null;
    }

    @Override
    int offset() {
        return 0;
    }
}
