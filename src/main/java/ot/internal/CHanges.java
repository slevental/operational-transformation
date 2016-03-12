package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
class Changes extends Change {
    private final Iterable<Change> changes;

    Changes(Iterable<Change> changes) {
        this.changes = changes;
    }
}
