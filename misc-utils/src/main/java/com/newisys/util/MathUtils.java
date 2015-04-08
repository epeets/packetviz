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
package com.newisys.util;


/**
 * A collection of useful math functions.
 * 
 * @author Trevor Robinson
 */
public class MathUtils
{
    public static boolean isPowerOf2(int x)
    {
        return ((x & -x) == x);
    }
    
    public static boolean isPowerOf2(long x)
    {
        return ((x & -x) == x);
    }

    public static int log2(int x)
    {
        int res = -1;
        while (x != 0)
        {
            x >>>= 1;
            ++res;
        }
        return res;
    }
    
    public static int log2(long x)
    {
        int res = -1;
        while (x != 0)
        {
            x >>>= 1;
            ++res;
        }
        return res;
    }

    public static long pow2(int x)
    {
        return 1L << x;
    }
}
