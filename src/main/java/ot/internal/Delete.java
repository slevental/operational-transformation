package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
class Delete extends Change {
    private final String text;

    public Delete(String text) {
        this.text = text;
    }
}
