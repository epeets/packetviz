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

package com.newisys.apps.pktviz.model;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

public final class PacketNode
{
    private String name;
    private String label;
    private Color bgColor;
    private Color fgColor;
    private PacketNode parent;
    private List<PacketNode> childList;
    private PacketGraph graph;
    private int disableUpdateCount;
    private boolean updated;

    public PacketNode(String _name)
    {
        name = _name;
        label = _name;
        childList = new LinkedList<PacketNode>();
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String _label)
    {
        if (!label.equals(_label))
        {
            label = _label;
            nodeUpdated();
        }
    }

    public Color getBgColor()
    {
        return bgColor;
    }

    public void setBgColor(Color color)
    {
        if (bgColor != color)
        {
            bgColor = color;
            nodeUpdated();
        }
    }

    public Color getFgColor()
    {
        return fgColor;
    }

    public void setFgColor(Color color)
    {
        if (fgColor != color)
        {
            fgColor = color;
            nodeUpdated();
        }
    }

    public PacketNode getParent()
    {
        return parent;
    }

    private void setParent(PacketNode _parent)
    {
        if (parent != _parent)
        {
            if (parent != null)
            {
                throw new IllegalStateException(
                    "Node is already child of another parent");
            }
            parent = _parent;
            nodeUpdated();
        }
    }

    public void addChildNode(PacketNode child)
    {
        addChildNode(child, childList.size());
    }

    public void addChildNode(PacketNode child, int index)
    {
        child.setParent(this);
        childList.add(index, child);
        nodeUpdated();
    }

    public List<PacketNode> getChildList()
    {
        return childList;
    }

    public boolean hasChildren()
    {
        return !childList.isEmpty();
    }

    public String toString()
    {
        return name;
    }

    void setGraph(PacketGraph g)
    {
        if (graph != null)
        {
            throw new IllegalStateException("Node is already in a graph");
        }
        graph = g;
    }

    public void disableUpdateEvents()
    {
        ++disableUpdateCount;
    }

    public void enableUpdateEvents()
    {
        if (--disableUpdateCount == 0 && updated)
        {
            nodeUpdated();
        }
    }

    private void nodeUpdated()
    {
        if (graph != null)
        {
            if (disableUpdateCount == 0)
            {
                graph.nodeUpdated(this);
                updated = false;
            }
            else
            {
                updated = true;
            }
        }
    }
}
