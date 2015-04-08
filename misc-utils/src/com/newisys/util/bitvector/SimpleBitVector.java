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

public class SimpleBitVector
{
    private int size;
    private LongArray longArray;

    public SimpleBitVector(int _size, LongArray _longArray)
    {
        size = _size;
        longArray = _longArray;
    }

    public int[] getIntArray()
    {
        int intCount = (size + 31) / 32;
        int[] res = new int[intCount];

        int curInt = 0;
        int curLong = 0;
        while (curInt < intCount) {
            long v = longArray.get(curLong++);
            res[curInt++] = (int)v;
            if (curInt < intCount) {
                res[curInt++] = (int)(v >>> 32);
            }
        }

        return res;
    }

    public long[] getLongArray()
    {
        int longCount = (size + 63) / 64;
        long[] res = new long[longCount];

        for (int i = 0; i < longCount; ++i) {        
            res[i] = longArray.get(i);
        }

        return res;
    }

    public boolean getFieldBoolean(int startBit)
    {
        checkFieldIndices(startBit, startBit, 1);

        int startIndex = startBit >>> 6;
        startBit &= 63;
        long startMask = 1 << startBit;

        long l = longArray.get(startIndex);
        return (l & startMask) != 0;
    }

    public int getFieldInt(int startBit, int endBit)
    {
        checkFieldIndices(startBit, endBit, 32);

        int startIndex = startBit >>> 6;
        startBit &= 63;
        long startMask = -1L << startBit;

        int endIndex = endBit >>> 6;
        endBit &= 63;
        long endMask = -1L >>> (63 - endBit);

        if (startIndex == endIndex) {
            long l = longArray.get(startIndex);
            return (int)((l & startMask & endMask) >>> startBit);
        } else {
            long ls = longArray.get(startIndex);
            long le = longArray.get(endIndex);
            return (int)(((ls & startMask) >>> startBit) | ((le & endMask) << (64 - startBit)));
        }
    }

    public long getFieldLong(int startBit, int endBit)
    {
        checkFieldIndices(startBit, endBit, 64);

        int startIndex = startBit >>> 6;
        startBit &= 63;
        long startMask = -1L << startBit;

        int endIndex = endBit >>> 6;
        endBit &= 63;
        long endMask = -1L >>> (63 - endBit);

        if (startIndex == endIndex) {
            long l = longArray.get(startIndex);
            return (l & startMask & endMask) >>> startBit;
        } else {
            long ls = longArray.get(startIndex);
            long le = longArray.get(endIndex);
            return ((ls & startMask) >>> startBit) | ((le & endMask) << (64 - startBit));
        }
    }

    public int size()
    {
        return size;
    }

    private void checkFieldIndices(int startBit, int endBit, int maxBits)
    {
        if (startBit > endBit) {
            throw new IllegalArgumentException("Start bit is greater than end bit");
        }
        if (endBit >= size) {
            throw new IllegalArgumentException("Field extends beyond bit vector");
        }
        if (endBit - startBit >= maxBits) {
            throw new IllegalArgumentException("Field is larger than " + maxBits + " bits");
        }
    }
}

