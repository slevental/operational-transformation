package ot.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static ot.internal.Compose.compose;

/**
 * Created by Stas on 3/22/16.
 */
@RunWith(Parameterized.class)
public class ComposeTest {
    private Text original;
    private Text middle;
    private Text expected;

    public ComposeTest(String original, String v1, String expected) {
        this.original = Text.wrap(original);
        this.middle = Text.wrap(v1);
        this.expected = Text.wrap(expected);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] d = {
                {"12345", "1234", "123"},
                {"12345", "12346", "123467"},
                {"123", "12a3", "12ab3"},
                {"123", "13", "133"},
                {"123", "13", "123"},
                {"123", "", "123"},
                {"123", "123abc", "abc"},
                {"1ab", "1a", "1b"},
                {"1a", "1", "a"},
        };
        return asList(d);
    }

    @Test
    public void test() throws Exception {
        Changes diff1 = original.diff(middle);
        Changes diff2 = middle.diff(expected);
        Changes allDiffs = compose(diff1, diff2);
        Text result = original.apply(allDiffs);
        assertEquals(String.format("'%s' != '%s'", expected, result), expected, result);
    }
}