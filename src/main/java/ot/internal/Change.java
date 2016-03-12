package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
public class Change {
    public Change transform(Change diff1) {
        return this;
    }

    public Text apply(Text text) {
        return text;
    }
}
