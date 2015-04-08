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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Node in a graph.
 */
public class Node
extends GraphElement
{
    /**
     * List of incoming edges (i.e. edges that terminate at this node).
     */
    private LinkedList<Edge> inEdgeList = new LinkedList<Edge>();

    /**
     * List of outgoing edges (i.e. edges that originate from this node).
     */
    private LinkedList<Edge> outEdgeList = new LinkedList<Edge>();

    /**
     * Cached unmodifiable list of incoming edges.
     */
    private List<Edge> unmodInEdgeList;

    /**
     * Cached unmodifiable list of outgoing edges.
     */
    private List unmodOutEdgeList;

    public Node()
    {
    }

    public Node(String _name)
    {
        super(_name);
    }

    public String getKind()
    {
        return "node";
    }

    public List getInEdgeList()
    {
        if (unmodInEdgeList == null) {
            unmodInEdgeList = Collections.unmodifiableList(inEdgeList);
        }
        return unmodInEdgeList;
    }

    protected void addInEdge(Edge edge)
    {
        inEdgeList.add(edge);
    }

    protected void removeInEdge(Edge edge)
    {
        inEdgeList.remove(edge);
    }

    public List getOutEdgeList()
    {
        if (unmodOutEdgeList == null) {
            unmodOutEdgeList = Collections.unmodifiableList(outEdgeList);
        }
        return unmodOutEdgeList;
    }

    protected void addOutEdge(Edge edge)
    {
        outEdgeList.add(edge);
    }

    protected void removeOutEdge(Edge edge)
    {
        outEdgeList.remove(edge);
    }

    public boolean hasEdges()
    {
        return (inEdgeList.size() > 0 || outEdgeList.size() > 0);
    }

}

