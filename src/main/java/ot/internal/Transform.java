package ot.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Stas on 3/15/16.
 */
public class Transform {
    private Transform() {
    }

    private static void transform(Retain r1, Retain r2, ResultBuilder builder) {
        if (r1.cursorOffset() == r2.cursorOffset()) {
            builder.addLeft(r1);
            builder.addRight(r2);
        } else if (r1.cursorOffset() > r2.cursorOffset()) {
            builder.replaceLeft(new Retain(r1.cursorOffset() - r2.cursorOffset()));
            builder.addLeft(new Retain(r2.cursorOffset()));
            builder.addRight(r2);
        } else if (r1.cursorOffset() < r2.cursorOffset()) {
            builder.replaceRight(new Retain(r2.cursorOffset() - r1.cursorOffset()));
            builder.addLeft(r1);
            builder.addRight(new Retain(r1.cursorOffset()));
        }
    }

    private static void transform(Retain r1, Insert i2, ResultBuilder builder) {
        builder.addLeft(new Retain(r1.cursorOffset() + i2.cursorOffset()));
        builder.addRight(i2);
    }

    private static void transform(Retain r1, Delete d2, ResultBuilder builder) {
        if (r1.cursorOffset() == d2.changeSize()) {
            builder.addRight(d2);
        } else if (r1.cursorOffset() > d2.changeSize()) {
            builder.replaceLeft(new Retain(r1.cursorOffset() - d2.changeSize()));
            builder.addRight(d2);
        } else if (r1.cursorOffset() < d2.changeSize()) {
            String txt = d2.text;
            builder.addRight(new Delete(txt.substring(0, r1.cursorOffset())));
            builder.replaceRight(new Delete(txt.substring(r1.cursorOffset())));
        }
    }

    private static void transform(Insert i1, Delete d2, ResultBuilder builder) {
        builder.addLeft(i1);
        builder.addRight(new Retain(i1.cursorOffset()));
        builder.addRight(d2);
    }

    private static void transform(Insert i1, Insert i2, ResultBuilder builder) {
        if (i1.revision() <= i2.revision()) { //fixme: rev1 == rev2 -should not be possible
            builder.addLeft(i1);
            builder.addLeft(new Retain(i2.cursorOffset()));
            builder.addRight(new Retain(i1.cursorOffset()));
            builder.addRight(i2);
        } else {
            builder.addLeft(new Retain(i2.cursorOffset()));
            builder.addLeft(i1);
            builder.addRight(i2);
            builder.addRight(new Retain(i1.cursorOffset()));
        }
    }

    private static void transform(Delete d1, Delete d2, ResultBuilder builder) {
        if (d1.changeSize() > d2.changeSize())
            builder.replaceLeft(new Delete(d1.text.substring(d2.changeSize())));
        else if (d1.changeSize() < d2.changeSize())
            builder.replaceRight(new Delete(d2.text.substring(d1.changeSize())));
    }

    public static Result transform(Changes chs1, Changes chs2) {
        ResultBuilder builder = new ResultBuilder(chs1.changes, chs2.changes);
        while (builder.hasNext()) {
            Change ch1 = builder.left();
            Change ch2 = builder.right();
            if (ch1 instanceof Retain) {
                if (ch2 instanceof Retain) transform((Retain) ch1, (Retain) ch2, builder);
                if (ch2 instanceof Insert) transform((Retain) ch1, (Insert) ch2, builder);
                if (ch2 instanceof Delete) transform((Retain) ch1, (Delete) ch2, builder);
            } else if (ch1 instanceof Insert) {
                if (ch2 instanceof Retain) transform((Retain) ch2, (Insert) ch1, builder.flip());
                else if (ch2 instanceof Insert) transform((Insert) ch1, (Insert) ch2, builder);
                else if (ch2 instanceof Delete) transform((Insert) ch1, (Delete) ch2, builder);
            } else if (ch1 instanceof Delete) {
                if (ch2 instanceof Retain) transform((Retain) ch2, (Delete) ch1, builder.flip());
                else if (ch2 instanceof Insert) transform((Insert) ch2, (Delete) ch1, builder.flip());
                else if (ch2 instanceof Delete) transform((Delete) ch1, (Delete) ch2, builder);
            }
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static class ResultBuilder {
        private ListIterator<Change> leftIterator;
        private ListIterator<Change> rightIterator;
        private List<Change> leftRes;
        private List<Change> rightRes;
        boolean flipped = false;

        public ResultBuilder(List<Change> l, List<Change> r) {
            this.leftIterator = new ArrayList<>(l).listIterator();
            this.rightIterator = new ArrayList<>(r).listIterator();
            this.leftRes = new ArrayList<>(l.size());
            this.rightRes = new ArrayList<>(r.size());
        }

        boolean hasNext() {
            return leftIterator.hasNext() && rightIterator.hasNext();
        }

        Change left() {
            return leftIterator.next();
        }

        Change right() {
            return rightIterator.next();
        }

        void addLeft(Change c) {
            leftRes.add(c);
        }

        void addRight(Change c) {
            rightRes.add(c);
        }

        void replaceLeft(Change c) {
            leftIterator.set(c);
            leftIterator.previous();
        }

        void replaceRight(Change c) {
            rightIterator.set(c);
            rightIterator.previous();
        }

        ResultBuilder flip() {
            flipped = !flipped;

            List<Change> b = this.leftRes;
            this.leftRes = this.rightRes;
            this.rightRes = b;

            ListIterator<Change> i = this.leftIterator;
            this.leftIterator = this.rightIterator;
            this.rightIterator = i;

            return this;
        }

        Result build() {
            while (leftIterator.hasNext()) leftRes.add(leftIterator.next());
            while (rightIterator.hasNext()) rightRes.add(rightIterator.next());
            return flipped ? new Result(rightRes, leftRes) : new Result(leftRes, rightRes);
        }
    }

    public static class Result {
        private final Changes changes1;
        private final Changes changes2;

        public Result(List<Change> changes1, List<Change> changes2) {
            this.changes1 = new Changes(changes1);
            this.changes2 = new Changes(changes2);
        }

        public Change getLeft() {
            return changes1;
        }

        public Change getRight() {
            return changes2;
        }
    }

}
