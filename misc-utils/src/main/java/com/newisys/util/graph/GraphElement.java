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

import java.awt.Color;

import com.newisys.util.symbol.Symbol;

/**
 * Abstract graph element, such as a node or edge.
 */
public class GraphElement
extends Symbol
{
    /**
     * Graph currently containing this element.
     */
    private Graph graph;

    /**
     * Parent subgraph.
     */
    private Subgraph subgraph;

    private String label;
    private Color color;
    private String description;

    public GraphElement()
    {
    }

    public GraphElement(String _name)
    {
        super(_name);
    }

    public String getKind()
    {
        return "element";
    }

    public final Graph getGraph()
    {
        return graph;
    }

    protected final void setGraph(Graph _graph)
    {
        graph = _graph;
    }

    public final Subgraph getSubgraph()
    {
        return subgraph;
    }

    protected final void setSubgraph(Subgraph _subgraph)
    {
        subgraph = _subgraph;
    }

    public final String getLabel()
    {
        return (label != null) ? label : getName();
    }

    public final void setLabel(String _label)
    {
        label = _label;
    }

    public final Color getColor()
    {
        return color;
    }

    public final void setColor(Color _color)
    {
        color = _color;
    }

    public final String getDescription()
    {
        return description;
    }

    public final void setDescription(String _description)
    {
        description = _description;
    }

}

