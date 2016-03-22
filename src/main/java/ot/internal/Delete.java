package ot.internal;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Stas on 3/12/16.
 */
class Delete extends TextChange {
    final String text;

    public Delete(String text) {
        this.text = text;
    }

    @Override
    Text apply(int lo, Text text) throws ValidationException {
        assertDel(lo, text.buffer, this.text);
        int hi = lo + this.text.length();
        text.buffer.delete(lo, hi);
        for (Integer p : newArrayList((text.markup.asMap().tailMap(lo, false).keySet()))) {
            if (p < hi) text.markup.removeAll(p);
            else text.markup.putAll(p + lo - hi, text.markup.removeAll(p));
        }
        return text;
    }

    @Override
    int offset() {
        return 0;
    }

    @Override
    int operationSize() {
        return text.length();
    }

    private static void assertDel(int pos, GapBuffer text, String toDel) throws ValidationException {
        if (!text.equals(toDel, pos, toDel.length()))
            throw new ValidationException("Trying to delete part of a text which is not equal to originally deleted");
    }

    @Override
    public String toString() {
        return "DEL: " + text;
    }
}
