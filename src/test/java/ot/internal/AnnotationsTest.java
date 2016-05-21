package ot.internal;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * User: stas
 * Date: 1/15/14
 */
@RunWith(Parameterized.class)
public class AnnotationsTest {
    private Text text1;
    private Text text2;

    public AnnotationsTest(String text1, String text2) {
        this.text1 = createTextWithAttribute(text1);
        this.text2 = createTextWithAttribute(text2);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] d = {
                {"I {have} {a} {cat}", "I {have} big {a} {cat}"}
                , {"I have a cat{}'", "I have a cat{}"}
                , {"I big {have} {a} {cat}", "I {have} {a} {cat}"}
                , {"I {have} big {a} {cat}", "I {have} {a} {cat}"}
                , {"I {have} {a} {cat}", "Mike Tyson and I {have} {a} {cat}"}
                , {"I {have} {a} {cat}", "I {have} {a} big {cat}"}
                , {"I {have} {a} {cat}", "I {have} {a} {cat big}"}
                , {"I {have} {a} big {cat}", "I {have} {a} {cat}"}
                , {"{I} has a cat", "You and {I} has a cat"}
                , {"I has {a}", "I has {a cat}"}
                , {"I {has} {a}", "I {has} {a cat}"}
                , {"I {has} a cat", "I {hassss} a cat"}
                , {"I {has a} cat", "I {has}"}
                , {"I {has a} cat", "I {}cat"} // fixme: is it corrent annotation behaviour
                , {"I {has} a cat", "I {h} a cat"}
                , {"I a cat", "I a cat"}
                , {"I {has} a cat", "I hhh{has} a cat"}
                , {"I {has} a cat", "I hhh{has} a cat"}
                , {"I {has} a cat", "I {hasssss} a cat"}
                , {"I {has} a cat", "I {has} a bat"}
                , {"this is a test", "that is a post"}
        };
        return Arrays.asList(d);
    }

    @Test
    public void testWithAttributes() throws Exception {
        Change diff = text1.diff(text2);
        Text result = text1.apply(diff);
        assertEquals(text2, result);
    }

    public Text createTextWithAttribute(String str) {
        StringBuilder res = new StringBuilder();
        Multimap<Integer, Markup> attributes = HashMultimap.create();
        int offset = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '{':
                    attributes.put(i - offset++, new InsertAnnotationStart());
                    break;
                case '}':
                    attributes.put(i - offset++, new InsertAnnotationEnd());
                    break;
                default:
                    res.append(c);
            }
        }
        Text txt = Text.wrap(res.toString());
        txt.markup.putAll(attributes);
        return txt;
    }
}
