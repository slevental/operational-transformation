package ot.internal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stas on 3/22/16.
 */
public class IOTest {
    @Test
    public void test_io() throws Exception {
        String str = IO.toString(Text.wrap("test").diff("txxx"));
        assertEquals("txxx", Text.wrap("test").apply(IO.fromString(str)).toString());
    }

    @Test
    public void test_io_scaping() throws Exception {
        String str = IO.toString(Text.empty().diff("%%|||%%"));
        assertEquals("%%|||%%", Text.empty().apply(IO.fromString(str)).toString());
    }

}