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

package com.newisys.util.glyph;

import java.awt.geom.PathIterator;
import java.util.LinkedList;
import java.util.List;

public final class PathSegment
{
	public static final int SEG_LINE = 1;
	public static final int SEG_QUAD = 2;
	public static final int SEG_CUBIC = 3;

	public int type;
	public double[] coords;

	public PathSegment()
	{
		type = SEG_LINE;
		coords = new double[8];
	}

	public PathSegment(int _type, double[] _coords)
	{
		type = _type;
		coords = new double[8];
		System.arraycopy(_coords, 0, coords, 0, Math.min(_coords.length, coords.length));
	}

	public int getType()
	{
		return type;
	}

	public double[] getCoords()
	{
		return coords;
	}

	public int getCoordCount()
	{
		switch (type) {
		case SEG_LINE:
			return 4;
		case SEG_QUAD:
			return 6;
		case SEG_CUBIC:
			return 8;
		default:
			return 0;
		}
	}

	public static List getSegments(PathIterator pi)
	{
		LinkedList<PathSegment> l = new LinkedList<PathSegment>();

		double xc = 0, yc = 0;
		double xm = 0, ym = 0;
		double[] coords = new double[6];
		while (!pi.isDone()) {
			int absSegType = 0;
			int coordCount = 0;

			int segType = pi.currentSegment(coords);
			switch (segType) {
			case PathIterator.SEG_MOVETO:
				xc = xm = coords[0];
				yc = ym = coords[1];
				break;
			case PathIterator.SEG_LINETO:
				absSegType = SEG_LINE;
				coordCount = 2;
				break;
			case PathIterator.SEG_QUADTO:
				absSegType = SEG_QUAD;
				coordCount = 4;
				break;
			case PathIterator.SEG_CUBICTO:
				absSegType = SEG_CUBIC;
				coordCount = 6;
				break;
			case PathIterator.SEG_CLOSE:
				absSegType = SEG_LINE;
				coordCount = 2;
				coords[0] = xm;
				coords[1] = ym;
				break;
			}

			if (coordCount > 0) {
				PathSegment p = new PathSegment();
				p.type = absSegType;
				p.coords[0] = xc;
				p.coords[1] = yc;
				System.arraycopy(coords, 0, p.coords, 2, coordCount);
				l.add(p);

				xc = coords[coordCount - 2];
				yc = coords[coordCount - 1];
			}

			pi.next();
		}

		return l;
	}

}

