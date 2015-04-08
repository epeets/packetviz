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

package com.newisys.util.bitvector;

public class BitAccess {

    public static int getBit(int value, int pos)
    {
        if (pos < 0 || pos > 31) {
            throw new IllegalArgumentException();
        }

        return (value >> pos) & 1;
    }

    public static int setBit(int value, int pos, int toValue)
    {
        if (pos < 0 || pos > 31) {
            throw new IllegalArgumentException();
        }

        int mask = 1 << pos;

        return (value & ~mask) | ((toValue << pos) & mask);
    }

    public static int getBit(long value, int pos)
    {
        if (pos < 0 || pos > 63) {
            throw new IllegalArgumentException();
        }

        return (int) (value >> pos) & 1;
    }

    public static long setBit(long value, int pos, long toValue)
    {
        if (pos < 0 || pos > 63) {
            throw new IllegalArgumentException();
        }

        long mask = 1L << pos;

        return (value & ~mask) | ((toValue << pos) & mask);
    }

    public static int getBits(int value, int start, int end)
    {
        if (start > end || start < 0 || end > 31) {
            throw new IllegalArgumentException();
        }

        int startMask = -1 << start;
        int endMask = -1 >>> (31 - end);
        int mask = startMask & endMask;

        return (value & mask) >>> start;
    }

    public static int setBits(int value, int start, int end, int toValue)
    {
        if (start > end || start < 0 || end > 31) {
            throw new IllegalArgumentException();
        }

        int startMask = -1 << start;
        int endMask = -1 >>> (31 - end);
        int mask = startMask & endMask;

        return (value & ~mask) | ((toValue << start) & mask);
    }

    public static long getBits(long value, int start, int end)
    {
        if (start > end || start < 0 || end > 63) {
            throw new IllegalArgumentException();
        }

        long startMask = -1L << start;
        long endMask = -1L >>> (63 - end);
        long mask = startMask & endMask;

        return (value & mask) >>> start;
    }

    public static long setBits(long value, int start, int end, long toValue)
    {
        if (start > end || start < 0 || end > 63) {
            throw new IllegalArgumentException();
        }

        long startMask = -1L << start;
        long endMask = -1L >>> (63 - end);
        long mask = startMask & endMask;

        return (value & ~mask) | ((toValue << start) & mask);
    }

}
