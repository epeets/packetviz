/*
 * Misc-Utils - Miscellaneous Utility Classes
 * Copyright (C) 2007 Newisys, Inc. or its licensors, as applicable.
 * Java is a registered trademark of Sun Microsystems, Inc. in the U.S. or
 * other countries.
 *
 * Licensed under the Open Software License version 3.0 (the "License"); you
 * may not use this file except in compliance with the License. You should
 * have received a copy of the License along with this software; if not, you
 * may obtain a copy of the License at
 *
 * http://opensource.org/licenses/osl-3.0.php
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.newisys.util.format;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.newisys.util.MathUtils;


public class SizedIntegerFormat
{
    private int size;
    private int radix;
    private String radixPrefix;
    private boolean prefixed;

    private static Pattern pattern;

    public SizedIntegerFormat()
    {
        size = 64;
        setRadix(10);
        prefixed = true;
    }

    public SizedIntegerFormat(int _size, int _radix)
    {
        size = _size;
        setRadix(_radix);
        prefixed = true;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int newSize)
    {
        if (newSize < 1 || newSize > 64) {
            throw new IllegalArgumentException("Invalid size");
        }
        size = newSize;
    }

    public int getRadix()
    {
        return radix;
    }

    public void setRadix(int newRadix)
    {
        radixPrefix = getRadixPrefix(newRadix);
        radix = newRadix;
    }

    public String getRadixPrefix()
    {
        return radixPrefix;
    }

    public static String getRadixPrefix(int radix)
    {
        switch (radix) {
            case 2:
                return "'b";
            case 10:
                return "";
            case 16:
                return "'h";
            default:
                throw new IllegalArgumentException("Invalid radix");
        }
    }

    public boolean isPrefixed()
    {
        return prefixed;
    }

    public void setPrefixed(boolean _prefixed)
    {
        prefixed = _prefixed;
    }

    public long parse(String s)
    {
        if (pattern == null) {
            pattern = Pattern.compile("-?(?:([0-9]+)?'([bdh]))?([0-9A-Fa-f]+)");
        }

        Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid number: " + s);
        }

        String sizeStr = matcher.group(1);
        if (sizeStr != null) {
            setSize(Integer.parseInt(sizeStr));
        }

        String radixStr = matcher.group(2);
        if (radixStr != null) {
            setPrefixed(true);
            switch (radixStr.charAt(0)) {
                case 'b':
                    setRadix(2);
                    break;
                case 'd':
                    setRadix(10);
                    break;
                case 'h':
                    setRadix(16);
                    break;
            }
        } else {
            setPrefixed(false);
        }

        boolean negative = s.startsWith("-");
        if (negative && radix != 10) {
            throw new IllegalArgumentException(
                "Base " + radix + " numbers cannot be negative");
        }

        long res;
        String numStr = matcher.group(3);
        if (negative) {
            res = -Long.parseLong(numStr, radix);
        } else {
            res = parseUnsignedLong(numStr, radix);
        }

        return res;
    }

    public static long parseUnsignedLong(String s, int radix)
        throws NumberFormatException
    {
        int bitsPerDigit;
        if (MathUtils.isPowerOf2(radix)) {
            bitsPerDigit = MathUtils.log2(radix);
        } else {
            bitsPerDigit = -1;
        }
        int len = s.length();
        if (bitsPerDigit > 0 && (len * bitsPerDigit) == 64) {
            int intPos = len - (32 / bitsPerDigit);
            long high = Long.parseLong(s.substring(0, intPos), radix);
            long low = Long.parseLong(s.substring(intPos, len), radix);
            return (high << 32) | low;
        } else {
            return Long.parseLong(s, radix);
        }
    }

    public String format(long v)
    {
        return format(v, size, radix, prefixed, radixPrefix);
    }

    public static String format(long v, int size, int radix)
    {
        return format(v, size, radix, true, getRadixPrefix(radix));
    }

    public static String format(long v, int size, int radix,
        boolean prefixed)
    {
        String radixPrefix = prefixed ? getRadixPrefix(radix) : null;
        return format(v, size, radix, prefixed, radixPrefix);
    }

    private static String format(long v, int size, int radix,
        boolean prefixed, String radixPrefix)
    {
        int minLength;
        boolean negative;
        String num;
        switch (radix) {
            case 2:
                minLength = size;
                negative = false;
                num = Long.toBinaryString(v);
                break;
            case 16:
                minLength = (size + 3) / 4;
                negative = false;
                num = Long.toHexString(v);
                break;
            default:
                minLength = 1;
                negative = (v < 0);
                if (negative) v = -v;
                num = Long.toString(v, radix);
                break;
        }

        int curLength = num.length();
        int actualLength = Math.max(curLength, minLength);
        if (negative) ++actualLength;

        StringBuffer buf;
        if (prefixed) {
            String sizePrefix = Integer.toString(size);
            buf = new StringBuffer(
                sizePrefix.length() +
                radixPrefix.length() +
                actualLength);
            if (negative) buf.append('-');
            buf.append(sizePrefix);
            buf.append(radixPrefix);
        } else {
            buf = new StringBuffer(actualLength);
            if (negative) buf.append('-');
        }

        while (curLength < minLength) {
            buf.append('0');
            ++curLength;
        }
        buf.append(num);

        return buf.toString();
    }

}

