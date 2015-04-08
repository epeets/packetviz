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

package com.newisys.prtree;

public class IntervalUtil {

	public static final int CONTAINS = 1;
	public static final int PRECEDES = 2;
	public static final int SUCCEEDS = 4;

    public static boolean precedesPoint(Interval i, long value) {
        return (value > i.getLowerBound());
    }

    public static boolean succeedsPoint(Interval i, long value) {
        return (value < i.getUpperBound());
    }

	public static boolean containsPoint(Interval i, long value) {
		long low = i.getLowerBound();
		long high = i.getUpperBound();
		return (i.isLowerBoundClosed() ? value >= low : value > low)
			&& (i.isUpperBoundClosed() ? value <= high : value < high);
	}

	public static int testPoint(Interval i, long value) {
		long low = i.getLowerBound();
		long high = i.getUpperBound();
		int result = 0;
		if (value > low) {
			result |= PRECEDES;
		}
		if (value < high) {
			result |= SUCCEEDS;
		}
		if (result == (PRECEDES | SUCCEEDS)
			|| (i.isLowerBoundClosed() && value == low)
			|| (i.isUpperBoundClosed() && value == high)) {
			result |= CONTAINS;
		}
		return result;
	}

	public static boolean containsInterval(Interval i, Interval sub) {
		long low = i.getLowerBound();
		long high = i.getUpperBound();
		long subLow = sub.getLowerBound();
		long subHigh = sub.getUpperBound();
		return (
			(i.isLowerBoundClosed() || !sub.isLowerBoundClosed())
				? subLow >= low
				: subLow > low)
			&& ((i.isUpperBoundClosed() || !sub.isUpperBoundClosed())
				? subHigh <= high
				: subHigh < high);
	}
}
