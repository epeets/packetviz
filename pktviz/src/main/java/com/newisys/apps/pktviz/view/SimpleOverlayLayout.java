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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

/**
 * A layout manager to arrange components over the top of each other.
 * Components are not moved, they are simply resized to fill the container.
 * The requested size of the container will be the largest requested bounds
 * of the children. This layout manager ignores component alignment.
 * 
 * @see javax.swing.OverlayLayout
 */
public class SimpleOverlayLayout
    implements LayoutManager2
{

    private Dimension minSize;
    private Dimension prefSize;
    private Dimension maxSize;

    public void addLayoutComponent(Component comp, Object constraints)
    {
        invalidateLayout(null);
    }

    public void addLayoutComponent(String name, Component comp)
    {
        invalidateLayout(null);
    }

    public void removeLayoutComponent(Component comp)
    {
        invalidateLayout(null);
    }

    public void invalidateLayout(Container target)
    {
        minSize = null;
        prefSize = null;
        maxSize = null;
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
        checkDimensions(target);
        return minSize;
    }

    public Dimension preferredLayoutSize(Container target)
    {
        checkDimensions(target);
        return prefSize;
    }

    public Dimension maximumLayoutSize(Container target)
    {
        checkDimensions(target);
        return maxSize;
    }

    /**
     * Resizes the child components to fill the target container.
     * 
     * @param target the target container
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    public void layoutContainer(Container target)
    {
        checkDimensions(target);

        Dimension alloc = target.getSize();
        Insets in = target.getInsets();
        alloc.width -= in.left + in.right;
        alloc.height -= in.top + in.bottom;

        int count = target.getComponentCount();
        for (int i = 0; i < count; ++i)
        {
            Component child = target.getComponent(i);
            int x = child.getX();
            int y = child.getY();
            child.setSize(alloc.width - x, alloc.height - y);
        }
    }

    /**
     * Aggregates the minimum, preferred, and maximum bounds of the child
     * components if the current bounds are not known.
     * 
     * @param target the target container, needed to requests its insets
     */
    private void checkDimensions(Container target)
    {
        if (minSize == null)
        {
            minSize = new Dimension();
            prefSize = new Dimension();
            maxSize = new Dimension();

            Insets insets = target.getInsets();
            addInsets(minSize, insets);
            addInsets(prefSize, insets);
            addInsets(maxSize, insets);

            int count = target.getComponentCount();
            for (int i = 0; i < count; ++i)
            {
                Component child = target.getComponent(i);
                int x = child.getX();
                int y = child.getY();

                Dimension min = child.getMinimumSize();
                minSize.width = Math.max(minSize.width, min.width + x);
                minSize.height = Math.max(minSize.height, min.height + y);

                Dimension pref = child.getPreferredSize();
                prefSize.width = Math.max(prefSize.width, pref.width + x);
                prefSize.height = Math.max(prefSize.height, pref.height + y);

                Dimension max = child.getMaximumSize();
                maxSize.width = Math.max(maxSize.width, max.width + x);
                maxSize.height = Math.max(maxSize.height, max.height + y);
            }
        }
    }

    private static void addInsets(Dimension d, Insets i)
    {
        d.width += i.left + i.right;
        d.height += i.top + i.bottom;
    }

}
