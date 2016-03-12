package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
class Changes extends Change {
    private final Iterable<Change> changes;

    Changes(Iterable<Change> changes) {
        this.changes = changes;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        for (Change change : changes) {
            change.apply(pos, text);
            pos += change.offset();
        }
        return text;
    }

    @Override
    int offset() {
        int pos = 0;
        for (Change change : changes) pos += change.offset();
        return pos;
    }
}
