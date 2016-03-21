package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
class Delete extends TextChange {
    final String text;

    public Delete(String text) {
        this.text = text;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
//        assertDel(pos, text.buffer, this.text);
        text.buffer.delete(pos, pos + this.text.length());
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
