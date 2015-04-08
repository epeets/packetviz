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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class Connector
implements Glyph
{
	private GeneralPath bodyPath;
	private GeneralPath tailPath;
	private GeneralPath headPath;

	public Connector(Shape bodyShape)
	{
		this(bodyShape, null, null);
	}

	public Connector(Shape bodyShape, Shape tailShape, Shape headShape)
	{
		bodyPath = new GeneralPath(bodyShape);

		List segs = PathSegment.getSegments(bodyPath.getPathIterator(null));

		if (tailShape != null) {
			tailPath = new GeneralPath(tailShape);

			Line2D firstSeg = getFirstSegment(segs);
			transformEnd(tailPath, firstSeg.getX1(), firstSeg.getY1(), getLineAngle(firstSeg));
		}

		if (headShape != null) {
			headPath = new GeneralPath(headShape);

			Line2D lastSeg = getLastSegment(segs);
			transformEnd(headPath, lastSeg.getX2(), lastSeg.getY2(), getLineAngle(lastSeg));
		}
	}

	public final Shape getBody()
	{
		return bodyPath;
	}

	public final Shape getTail()
	{
		return tailPath;
	}

	public final Shape getHead()
	{
		return headPath;
	}

	public Rectangle2D getBounds()
	{
		Rectangle2D r = bodyPath.getBounds2D();
		if (tailPath != null) {
			r = r.createUnion(tailPath.getBounds2D());
		}
		if (headPath != null) {
			r = r.createUnion(headPath.getBounds2D());
		}
		return r;
	}

	public void paint(Graphics2D g2d)
	{
		g2d.draw(bodyPath);
		if (tailPath != null) {
			g2d.draw(tailPath);
			g2d.fill(tailPath);
		}
		if (headPath != null) {
			g2d.draw(headPath);
			g2d.fill(headPath);
		}
	}

	private static void transformEnd(GeneralPath path, double x, double y, double angle)
	{
		AffineTransform xform = AffineTransform.getTranslateInstance(x, y);
		xform.rotate(angle);
		path.transform(xform);
	}

	private static Line2D getFirstSegment(List segs)
	{
		PathSegment seg = (PathSegment)segs.get(0);
		return new Line2D.Double(seg.coords[0], seg.coords[1], seg.coords[2], seg.coords[3]);
	}

	private static Line2D getLastSegment(List segs)
	{
		PathSegment seg = (PathSegment)segs.get(segs.size() - 1);
		int n = seg.getCoordCount();
		return new Line2D.Double(seg.coords[n - 4], seg.coords[n - 3], seg.coords[n - 2], seg.coords[n - 1]);
	}

	private static double getLineAngle(Line2D line)
	{
		return Math.atan2(line.getX2() - line.getX1(), line.getY1() - line.getY2());
	}

	public static GeneralPath makeArrowEnd()
	{
		return makeArrowEnd(5, 7);
	}

	public static GeneralPath makeArrowEnd(float w, float h)
	{
		GeneralPath p = new GeneralPath();
		p.moveTo(0, 0);
		p.lineTo(-w / 2.0f, h);
		p.lineTo(0, h * 0.9f);
		p.lineTo(w / 2.0f, h);
		p.closePath();
		return p;
	}

	public static GeneralPath makeEllipseEnd()
	{
		return makeEllipseEnd(5, 5);
	}

	public static GeneralPath makeEllipseEnd(float w, float h)
	{
		return new GeneralPath(new Ellipse2D.Float(-w / 2, -w / 2, w, h));
	}

	public static GeneralPath makeRectangleEnd()
	{
		return makeRectangleEnd(5, 5);
	}

	public static GeneralPath makeRectangleEnd(float w, float h)
	{
		return new GeneralPath(new Rectangle2D.Float(-w / 2, -w / 2, w, h));
	}

}

