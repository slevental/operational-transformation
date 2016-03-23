package ot.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Math.max;

/**
 * Created by Stas on 3/21/16.
 */
public class Compose {
    private Compose() {
    }

    public static Changes compose(Changes l, Changes r) {
        ListIterator<Change> i = new ArrayList<>(l.changes).listIterator();
        ListIterator<Change> j = new ArrayList<>(r.changes).listIterator();
        List<Change> result = new ArrayList<>(max(l.changes.size(), r.changes.size()));
        while (i.hasNext() && j.hasNext()) {
            Change ch1 = i.next();
            Change ch2 = j.next();
            if (ch1 instanceof Retain) {
                if (ch2 instanceof Retain) {
                    if (ch1.offset() == ch2.offset())
                        result.add(ch1);
                    else if (ch1.offset() < ch2.offset()) {
                        result.add(ch1);
                        j.set(new Retain(ch2.offset() - ch1.offset()));
                        j.previous();
                    } else if (ch1.offset() > ch2.offset()) {
                        result.add(ch2);
                        i.set(new Retain(ch1.offset() - ch2.offset()));
                        i.previous();
                    }
                } else if (ch2 instanceof Insert) {
                    result.add(ch2);
                    i.set(new Retain(ch1.offset() - ch2.offset()));
                    i.previous();
                } else if (ch2 instanceof Delete) {
                    result.add(ch2);
                    int len = ((Delete) ch2).len;
                    if (len < ch1.offset()) {
                        i.set(new Retain(ch1.offset() - len));
                        i.previous();
                    }
                }
            } else if (ch1 instanceof Delete) {
                if (ch2 instanceof Retain) {
                    result.add(ch1);
                    int len = ((Delete) ch1).len;
                    if (ch2.offset() > len) {
                        j.set(new Retain(ch2.offset() - len));
                        j.previous();
                    }
                } else if (ch2 instanceof Insert) {
                    result.add(ch1);
                    result.add(ch2);
                } else if (ch2 instanceof Delete) {
                    result.add(new Delete(((Delete) ch1).len + ((Delete) ch2).len));
                }
            } else if (ch1 instanceof Insert) {
                if (ch2 instanceof Retain) {
                    result.add(ch1);
                    j.set(new Retain(ch2.offset() - ch1.offset()));
                    j.previous();
                } else if (ch2 instanceof Insert) {
                    if (ch1.revision() <= ch2.revision())
                        result.add(new Insert(((Insert) ch1).text + ((Insert) ch2).text));
                    else
                        result.add(new Insert(((Insert) ch2).text + ((Insert) ch1).text));
                } else if (ch2 instanceof Delete) {
                    result.add(ch1);
                    result.add(ch2);
                }
            }
        }

        while (i.hasNext()) result.add(i.next());
        while (j.hasNext()) result.add(j.next());

        return new Changes(result);
    }
}
