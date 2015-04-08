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

package com.newisys.util.graph;

/**
 * Edge between nodes in a graph.
 */
public class Edge
extends GraphElement
{
    private Node fromNode;
    private Node toNode;

    private double fromTime;
    private double toTime;

    public Edge(Node _fromNode, Node _toNode)
    {
        fromNode = _fromNode;
        toNode = _toNode;
    }

    public Edge(Node _fromNode, Node _toNode, String _name)
    {
        super(_name);
        fromNode = _fromNode;
        toNode = _toNode;
    }

    public String getKind()
    {
        return "edge";
    }

    public final Node getFromNode()
    {
        return fromNode;
    }

    public final Node getToNode()
    {
        return toNode;
    }

    public final double getFromTime()
    {
        return fromTime;
    }

    public final void setFromTime(double _fromTime)
    {
        fromTime = _fromTime;
    }

    public final double getToTime()
    {
        return toTime;
    }

    public final void setToTime(double _toTime)
    {
        toTime = _toTime;
    }

}

