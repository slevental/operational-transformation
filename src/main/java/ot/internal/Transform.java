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

    public static Result transform(Changes chs1, Changes chs2) {
        ListIterator<Change> list1 = new ArrayList<>(chs1.changes).listIterator();
        ListIterator<Change> list2 = new ArrayList<>(chs2.changes).listIterator();
        List<Change> result1 = new ArrayList<>(chs1.changes.size());
        List<Change> result2 = new ArrayList<>(chs2.changes.size());

        while (list1.hasNext() && list2.hasNext()) {
            Change ch1 = list1.next();
            Change ch2 = list2.next();

            if (ch1 instanceof Retain) {
                if (ch2 instanceof Retain) {
                    if (ch1.offset() == ch2.offset()) {
                        result1.add(ch1);
                        result2.add(ch2);
                    } else if (ch1.offset() > ch2.offset()) {
                        list1.set(new Retain(ch1.offset() - ch2.offset()));
                        list1.previous();
                        result1.add(new Retain(ch2.offset()));
                        result2.add(ch2);
                    } else if (ch1.offset() < ch2.offset()) {
                        list2.set(new Retain(ch2.offset() - ch1.offset()));
                        list2.previous();
                        result1.add(ch1);
                        result2.add(new Retain(ch1.offset()));
                    }
                }
                if (ch2 instanceof Insert) {
                    result1.add(new Retain(ch1.offset() + ch2.offset()));
                    result2.add(ch2);
                }
                if (ch2 instanceof Delete) {
                    if (ch1.offset() == ch2.operationSize()) {
                        result2.add(ch2);
                    } else if (ch1.offset() > ch2.operationSize()) {
                        list1.set(new Retain(ch1.offset() - ch2.operationSize()));
                        list1.previous();
                        result2.add(ch2);
                    } else if (ch1.offset() < ch2.operationSize()) {
                        String txt = ((Delete) ch2).text;
                        result2.add(new Delete(txt.substring(0, ch1.offset())));
                        list2.set(new Delete(txt.substring(ch1.offset())));
                        list2.previous();
                    }
                }
            } else if (ch1 instanceof Insert) {
                if (ch2 instanceof Retain) {
                    result1.add(ch1);
                    result2.add(new Retain(ch2.offset() + ch1.offset()));
                } else if (ch2 instanceof Insert) {
                    if (ch1.revision() == ch2.revision()) {
                        //todo: WAT?
                        result1.add(ch1);
                        result1.add(new Retain(ch2.offset()));
                        result2.add(new Retain(ch1.offset()));
                        result2.add(ch2);
                    } else if (ch1.revision() < ch2.revision()) {
                        result1.add(ch1);
                        result1.add(new Retain(ch2.offset()));
                        result2.add(new Retain(ch1.offset()));
                        result2.add(ch2);
                    } else if (ch1.revision() > ch2.revision()) {
                        result1.add(new Retain(ch2.offset()));
                        result1.add(ch1);
                        result2.add(ch2);
                        result2.add(new Retain(ch1.offset()));
                    }
                } else if (ch2 instanceof Delete) {
                    result1.add(ch1);
                    result2.add(new Retain(ch1.offset()));
                    result2.add(ch2);

                }
            } else if (ch1 instanceof Delete) {
                if (ch2 instanceof Retain) {
                    if (ch1.operationSize() == ch2.offset()) {
                        result1.add(ch1);
                    } else if (ch1.operationSize() < ch2.offset()) {
                        result1.add(ch1);
                        list2.set(new Retain(ch2.offset() - ch1.operationSize()));
                        list2.previous();
                    } else if (ch1.operationSize() > ch2.offset()) {
                        String txt = ((Delete) ch1).text;
                        result1.add(new Delete(txt.substring(0, ch2.offset())));
                        list1.set(new Delete(txt.substring(ch2.offset())));
                        list1.previous();
                    }
                } else if (ch2 instanceof Insert) {
                    result1.add(new Retain(ch2.offset()));
                    result1.add(ch1);
                    list2.previous();
                } else if (ch2 instanceof Delete) {
                    Delete del1 = (Delete) ch1;
                    Delete del2 = (Delete) ch2;
                    if (del1.operationSize() > del2.operationSize()) {
                        list1.set(new Delete(del1.text.substring(del2.operationSize())));
                        list1.previous();
                    } else if (del1.operationSize() < del2.operationSize()) {
                        list2.set(new Delete(del2.text.substring(del1.operationSize())));
                        list2.previous();
                    }
                }
            }
        }

        while (list1.hasNext()) result1.add(list1.next());
        while (list2.hasNext()) result2.add(list2.next());

        return new Result(
                new Changes(result1),
                new Changes(result2)
        );
    }

    public static class Result {
        private final Changes changes1;
        private final Changes changes2;

        public Result(Changes changes1, Changes changes2) {
            this.changes1 = changes1;
            this.changes2 = changes2;
        }

        public Change getLeft() {
            return changes1;
        }

        public Change getRight() {
            return changes2;
        }
    }

}
