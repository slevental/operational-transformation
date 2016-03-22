package ot.internal;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isLetterOrDigit;
import static org.apache.commons.lang.StringUtils.repeat;

/**
 * Created by Stas on 3/22/16.
 */
public class IO {
    private IO() {
    }

    public static Change fromString(String str) {
        String[] split = StringUtils.splitPreserveAllTokens(str, '|');
        if (split.length != 2)
            throw new IllegalArgumentException("Wrong format, expected 3 blocks separated by '|', but was: " + str);

        List<Change> res = new ArrayList<>();
        String stream = split[0];
        String inserts = unescape(split[1]);
        StringBuilder num = new StringBuilder();

        int ins = 0;
        for (int i = 0; i < stream.length(); i++) {
            num.delete(0, num.length());
            char operation = stream.charAt(i);

            while (i < stream.length() - 1 && isLetterOrDigit(stream.charAt(i + 1)))
                num.append(stream.charAt(++i));

            int len = Integer.parseInt(num.toString()); //todo: use radix 36
            switch (operation) {
                case '=':
                    res.add(new Retain(len));
                    break;
                case '+':
                    res.add(new Insert(inserts.substring(ins, ins += len)));
                    break;
                case '-':
                    res.add(new Delete(len));
                    break;
            }
        }

        return new Changes(res);
    }

    public static String toString(Change ch) {
        return ch instanceof Changes
                ? toString((Changes) ch)
                : toString(new Changes(ch));
    }

    public static String toDebugString(Changes ch) {
        StringBuilder stream = new StringBuilder();
        for (Change each : ch.changes) {
            if (each instanceof Retain) stream.append(repeat("=", each.offset()));
            else if (each instanceof Delete) stream.append(repeat("-", ((Delete) each).len));
            else if (each instanceof Insert) stream.append(((Insert) each).text);
        }
        return stream.toString();
    }

    static String toString(Changes ch) {
        StringBuilder insert = new StringBuilder();
        StringBuilder stream = new StringBuilder();
        for (Change each : ch.changes) {
            if (each instanceof Retain)
                stream.append("=").append(each.offset());
            else if (each instanceof Delete) {
                stream.append("-").append(((Delete) each).len);
            } else if (each instanceof Insert) {
                stream.append("+").append(each.offset());
                //todo: implement method in change (getText or similar)
                insert.append(escape(((Insert) each).text));
            }
        }
        return stream.append("|")
                .append(insert)
                .toString();
    }

    private static String escape(String text) {
        return text.replaceAll("%", "%25").replaceAll("\\|", "%7C");
    }

    private static String unescape(String text) {
        return text.replaceAll("%7C", "|").replaceAll("%25", "%");
    }
}
