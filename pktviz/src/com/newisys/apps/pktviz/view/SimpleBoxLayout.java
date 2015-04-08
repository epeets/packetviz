/*
 * PacketViz packet visualization for the Java (TM) Platform
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

package com.newisys.apps.pktviz.view;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

/**
 * A layout manager to arrange components in a line, either horizontally or
 * vertically. The requested size of the container will be the largest
 * requested bounds of the children. This layout manager ignores component
 * alignment.
 * 
 * @see javax.swing.BoxLayout
 */
public class SimpleBoxLayout
    implements LayoutManager2
{

    public static final int X_AXIS = 0;
    public static final int Y_AXIS = 1;

    private Container target;
    private int axis;
    private SizeRequirements[] xChildren;
    private SizeRequirements[] yChildren;
    private SizeRequirements xTotal;
    private SizeRequirements yTotal;

    public SimpleBoxLayout(Container target, int axis)
    {
        if (axis != X_AXIS && axis != Y_AXIS)
        {
            throw new AWTError("Invalid axis");
        }

        this.target = target;
        this.axis = axis;
    }

    public void addLayoutComponent(Component comp, Object constraints)
    {
        invalidateLayout(comp.getParent());
    }

    public void addLayoutComponent(String name, Component comp)
    {
        invalidateLayout(comp.getParent());
    }

    public void removeLayoutComponent(Component comp)
    {
        invalidateLayout(comp.getParent());
    }

    public synchronized void invalidateLayout(Container target)
    {
        checkContainer(target);
        xChildren = null;
        yChildren = null;
        xTotal = null;
        yTotal = null;
    }

    public float getLayoutAlignmentX(Container target)
    {
        return 0;
    }

    public float getLayoutAlignmentY(Container target)
    {
        return 0;
    }

    public Dimension minimumLayoutSize(Container target)
    {
        checkContainer(target);
        checkRequests(target);
        return dimensionWithInsets(xTotal.min, yTotal.min, target);
    }

    public Dimension preferredLayoutSize(Container target)
    {
        checkContainer(target);
        checkRequests(target);
        return dimensionWithInsets(xTotal.pref, yTotal.pref, target);
    }

    public Dimension maximumLayoutSize(Container target)
    {
        checkContainer(target);
        checkRequests(target);
        return dimensionWithInsets(xTotal.max, yTotal.max, target);
    }

    private static Dimension dimensionWithInsets(int w, int h, Container target)
    {
        Insets insets = target.getInsets();
        w = (int) Math.min((long) w + (long) insets.left + (long) insets.right,
            Integer.MAX_VALUE);
        h = (int) Math.min((long) h + (long) insets.top + (long) insets.bottom,
            Integer.MAX_VALUE);
        return new Dimension(w, h);
    }

    public void layoutContainer(Container target)
    {
        checkContainer(target);
        checkRequests(target);

        Dimension alloc = target.getSize();
        Insets in = target.getInsets();
        alloc.width -= in.left + in.right;
        alloc.height -= in.top + in.bottom;

        int count = target.getComponentCount();

        int[] xOffsets = new int[count];
        int[] xSpans = new int[count];
        int[] yOffsets = new int[count];
        int[] ySpans = new int[count];

        if (axis == X_AXIS)
        {
            SizeRequirements.calculateTiledPositions(alloc.width, xChildren,
                xOffsets, xSpans);
            SizeRequirements.calculateAlignedPositions(alloc.height, yChildren,
                yOffsets, ySpans);
        }
        else
        {
            SizeRequirements.calculateAlignedPositions(alloc.width, xChildren,
                xOffsets, xSpans);
            SizeRequirements.calculateTiledPositions(alloc.height, yChildren,
                yOffsets, ySpans);
        }

        long left = in.left;
        long top = in.top;

        for (int i = 0; i < count; ++i)
        {
            Component child = target.getComponent(i);
            child.setBounds((int) Math.min(left + (long) xOffsets[i],
                Integer.MAX_VALUE), (int) Math.min(top + (long) yOffsets[i],
                Integer.MAX_VALUE), xSpans[i], ySpans[i]);
        }
    }

    private void checkContainer(Container target)
    {
        if (this.target != target)
        {
            throw new AWTError("SimpleBoxLayout cannot be shared");
        }
    }

    private synchronized void checkRequests(Container target)
    {
        if (xChildren == null)
        {

            int count = target.getComponentCount();

            xChildren = new SizeRequirements[count];
            yChildren = new SizeRequirements[count];

            for (int i = 0; i < count; ++i)
            {
                Component child = target.getComponent(i);

                SizeRequirements childX = new SizeRequirements();
                SizeRequirements childY = new SizeRequirements();

                Dimension min = child.getMinimumSize();
                childX.min = min.width;
                childY.min = min.height;

                Dimension pref = child.getPreferredSize();
                childX.pref = pref.width;
                childY.pref = pref.height;

                Dimension max = child.getMaximumSize();
                childX.max = max.width;
                childY.max = max.height;

                xChildren[i] = childX;
                yChildren[i] = childY;
            }

            if (axis == X_AXIS)
            {
                xTotal = SizeRequirements.getTiledSizeRequirements(xChildren);
                yTotal = SizeRequirements.getAlignedSizeRequirements(yChildren);
            }
            else
            {
                xTotal = SizeRequirements.getAlignedSizeRequirements(xChildren);
                yTotal = SizeRequirements.getTiledSizeRequirements(yChildren);
            }
        }
    }

    private static class SizeRequirements
    {

        public int min;
        public int pref;
        public int max;

        public String toString()
        {
            return "[" + min + "," + pref + "," + max + "]";
        }

        public static SizeRequirements getAlignedSizeRequirements(
            SizeRequirements[] children)
        {

            SizeRequirements total = new SizeRequirements();
            for (int i = 0; i < children.length; i++)
            {
                SizeRequirements child = children[i];
                total.min = Math.max(total.min, child.min);
                total.pref = Math.max(total.pref, child.pref);
                total.max = Math.max(total.max, child.max);
            }
            return total;
        }

        public static SizeRequirements getTiledSizeRequirements(
            SizeRequirements[] children)
        {

            SizeRequirements total = new SizeRequirements();
            for (int i = 0; i < children.length; i++)
            {
                SizeRequirements child = children[i];
                total.min = (int) Math.min((long) total.min + (long) child.min,
                    Integer.MAX_VALUE);
                total.pref = (int) Math.min((long) total.pref
                    + (long) child.pref, Integer.MAX_VALUE);
                total.max = (int) Math.min((long) total.max + (long) child.max,
                    Integer.MAX_VALUE);
            }
            return total;
        }

        public static void calculateAlignedPositions(
            int allocated,
            SizeRequirements[] children,
            int[] offsets,
            int[] spans)
        {

            for (int i = 0; i < children.length; i++)
            {
                offsets[i] = 0;
                spans[i] = Math.min(children[i].max, allocated);
            }
        }

        public static void calculateTiledPositions(
            int allocated,
            SizeRequirements[] children,
            int[] offsets,
            int[] spans)
        {

            long min = 0;
            long pref = 0;
            long max = 0;
            for (int i = 0; i < children.length; i++)
            {
                min += children[i].min;
                pref += children[i].pref;
                max += children[i].max;
            }

            float scale;
            if (allocated >= pref)
            {
                float totalPlay = Math.min(allocated - pref, max - pref);
                scale = (max - pref == 0) ? 0.0f : totalPlay / (max - pref);
            }
            else
            {
                float totalPlay = Math.min(pref - allocated, pref - min);
                scale = (pref - min == 0) ? 0.0f : totalPlay / (pref - min);
            }

            int totalOffset = 0;
            for (int i = 0; i < children.length; i++)
            {
                SizeRequirements child = children[i];

                float play;
                if (allocated >= pref)
                {
                    play = scale * (child.max - child.pref);
                }
                else
                {
                    play = scale * (child.pref - child.min);
                }
                spans[i] = (int) Math.min((long) child.pref + (long) play,
                    Integer.MAX_VALUE);

                offsets[i] = totalOffset;
                totalOffset = (int) Math.min((long) totalOffset
                    + (long) spans[i], Integer.MAX_VALUE);
            }
        }

    }

}
