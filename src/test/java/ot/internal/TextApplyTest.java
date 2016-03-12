package ot.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TextApplyTest {
    private Text t1;
    private Text t2;

    public TextApplyTest(String t1, String t2) {
        this.t1 = Text.wrap(t1);
        this.t2 = Text.wrap(t2);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] d = {
                {"I'm wwork", "I'm working"}
                , {"qwerty56uiop", "1234567890"}
                , {"cat", "cow"}
                , {"this is a test", "that is a post"}
                , {"I have a nice test", "He has a nice test"}
                , {"111", "222"}
                , {"test.", "test,"}
                , {"test.", "best."}
                , {"111", "111"}
                , {"", ""}
                , {"It's cool", "That's cool"}
        };
        return Arrays.asList(d);
    }

    @Test
    public void test() throws Exception {
        Change diff = t1.diff(t2);
        assertEquals(String.format("'%s' over '%s' != '%s'", diff, t1, t2), t2, t1.apply(diff));
    }
}
