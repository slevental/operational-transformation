package ot.internal;

/**
 * Created by Stas on 3/12/16.
 */
class Delete extends Change {
    private final String text;

    public Delete(String text) {
        this.text = text;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        if (!text.buffer.equals(this.text, pos, this.text.length()))
            throw new ValidationException("Trying to delete part of a text which is not equal to originally deleted");

        text.buffer.delete(pos, pos + this.text.length());
        return text;
    }

    @Override
    int offset() {
        return 0;
    }
}
