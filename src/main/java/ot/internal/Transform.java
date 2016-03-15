package ot.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stas on 3/15/16.
 */
public class Transform {
    private Transform() {
    }

    public static Result transform(Change ch1, Change ch2) {
        return transform(new Changes(ch1), new Changes(ch2));
    }

    public static Result transform(Changes chs1, Changes chs2) {
        List<Change> list1 = chs1.changes;
        List<Change> list2 = chs2.changes;

        int i = 0, j = 0;
        List<Change> result1 = new ArrayList<>(list1.size());
        List<Change> result2 = new ArrayList<>(list2.size());

        while (i < list1.size() && j < list2.size()) {
            Change ch1 = list1.get(i);
            Change ch2 = list2.get(j);
        }

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
