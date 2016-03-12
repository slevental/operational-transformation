package ot.internal;

import java.util.Arrays;

import static java.lang.System.arraycopy;

class GapBuffer implements CharSequence {
    protected static final int DEFAULT_GAP_SIZE = 16;

    protected int gapSize = DEFAULT_GAP_SIZE;
    protected char[] text;
    protected int lo;
    protected int hi;

    public GapBuffer(String str) {
        this(toCharArray(str));
    }

    public GapBuffer(char[] arr) {
        this(arr, arr.length, arr.length);
    }


    GapBuffer(char[] arr, int lo, int hi) {
        if (lo > hi)
            throw new IllegalArgumentException("lo > hi");

        if (lo > arr.length)
            throw new IllegalArgumentException("lo > len");

        if (hi > arr.length)
            throw new IllegalArgumentException("hi > len");

        this.text = arr;
        this.lo = lo;
        this.hi = hi;
    }

    protected static char[] toCharArray(String str) {
        return str.toCharArray();
    }

    @Override
    public synchronized int length() {
        return text.length - gapSize();
    }

    @Override
    public synchronized char charAt(int index) {
        return text[index < lo ? index : index + gapSize()];
    }

    @Override
    public synchronized String subSequence(int start, int end) {
        if (text.length == 0)
            return "";

        if (end > length())
            end = length();

        if (start > end)
            return "";

        if (end < 0)
            end = 0;

        if (start < 0)
            start = 0;

        char[] res;
        res = new char[end - start];
        if (start < lo && end < lo) { // all data lay left side of the gap
            arraycopy(text, start, res, 0, end - start);
        } else if (start >= lo) { // all data lay right size of the gap
            arraycopy(text, toInternal(start, lo, hi), res, 0, end - start);
        } else { // it contains gap
            arraycopy(text, start, res, 0, lo - start);
            arraycopy(text, hi, res, lo - start, end - lo);
        }
        return new String(res);
    }

    public boolean equals(String text, int start, int end) {
        for (int i = start; i < end; i++) {
            int pos = i - start;
            if (pos >= text.length() || pos < 0)
                return false;
            int internal = toInternal(i, lo, hi);
            if (internal >= this.text.length || internal < 0)
                return false;
            if (text.charAt(pos) != this.text[internal])
                return false;
        }
        return true;
    }

    public void append(String txt) {
        if (txt == null)
            throw new IllegalArgumentException("appended text must not be null");
        ensureSize(txt.length());
        moveGap(length());
        for (int i = 0; i < txt.length(); i++) {
            text[lo += i] = txt.charAt(i);
        }
    }

    public void delete(int l, int h) {
        int txtLen = length();
        h = Math.min(txtLen, h);
        l = Math.max(0, l);

        int deletionLen = h - l;
        if (deletionLen <= 0 || txtLen == 0)
            return;
        moveGap(l);
        hi += deletionLen;
    }

    public int getCursor() {
        return Math.min(lo, length());
    }

    @Override
    public String toString() {
        int h = hi;
        int l = lo;
        int len = text.length;

        if (h == len) {
            return new String(Arrays.copyOfRange(text, 0, l));
        }
        char[] res = new char[len + l - h];
        arraycopy(text, 0, res, 0, l);
        arraycopy(text, h, res, l, len - h);
        return new String(res);
    }

    public void insert(int pos, String subStr) {
        if (pos < 0)
            throw new IllegalArgumentException("negative position is prohibited");
        if (pos > length())
            throw new IllegalArgumentException("bigger that length position is prohibited");

        if (subStr == null)
            throw new IllegalArgumentException("inserted string must not be null");

        int len = subStr.length();
        ensureSize(len);
        moveGap(pos);
        if (len == 1) {
            text[pos] = subStr.charAt(0);
        } else {
            for (int i = 0; i < len; i++) text[pos + i] = subStr.charAt(i);
        }
        lo += len;
    }

    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    public int indexOf(String str, int fromIndex) {
        char[] val = str.toCharArray();
        return indexOf(text, 0, length(), val, 0, val.length, fromIndex, lo, hi);
    }

    public int gapSize() {
        return hi - lo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GapBuffer that = (GapBuffer) o;

        if (length() != that.length()) return false;
        for (int i = 0; i < length(); i++) {
            if (text[toInternal(i, lo, hi)] != that.text[toInternal(i, that.lo, that.hi)])
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = length();
        for (int i = 0; i < text.length; i++) {
            char element = text[toInternal(i, lo, hi)];
            result = 31 * result + element;
        }
        return result;
    }

    static int indexOf(char[] source, int sourceOffset, int sourceCount,
                       char[] target, int targetOffset, int targetCount,
                       int fromIndex, int gapStart, int gapEnd) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[toInternal(i, gapStart, gapEnd)] != first) {
                while (++i <= max && source[toInternal(i, gapStart, gapEnd)] != first) ;
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[toInternal(j, gapStart, gapEnd)] == target[k]; j++, k++)
                    ;
                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    private static int toInternal(int i, int lo, int hi) {
        return i < lo ? i : i + (hi - lo);
    }

    private void moveGap(int pos) {
        int len = gapSize();
        int offset = Math.abs(lo - pos);
        if (pos < lo) {
            arraycopy(text, pos, text, pos + len, offset);
        } else if (pos > lo && hi < text.length) {
            arraycopy(text, hi, text, lo, offset);
        }
        lo = pos;
        hi = lo + len;
    }

    private void ensureSize(int required) {
        if (gapSize() < required) {
            while (gapSize < required)
                gapSize *= 2;
            growGap(gapSize);
        }
    }

    private void growGap(int newGapSize) {
        int currGapSize = gapSize();
        char[] old = text;
        char[] nw = new char[old.length + newGapSize];
        if (lo > 0)
            arraycopy(old, 0, nw, 0, lo);
        arraycopy(old, hi, nw, lo + newGapSize + currGapSize, old.length - hi);
        hi += newGapSize;
        text = nw;
    }
}
