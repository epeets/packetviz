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
 * Graph element that contains nodes or other subgraphs.
 */
public class Subgraph
extends GraphElement
{
    /**
     * List of subgraphs contained directly within this subgraph.
     */
    private LinkedList<Subgraph> subgraphList = new LinkedList<Subgraph>();

    /**
     * List of nodes contained directly within this subgraph.
     */
    private LinkedList<Node> nodeList = new LinkedList<Node>();

    /**
     * Cached unmodifiable subgraph list.
     */
    private List<Subgraph> unmodSubgraphList;

    /**
     * Cached unmodifiable node list.
     */
    private List unmodNodeList;

    public Subgraph()
    {
    }

    public Subgraph(String _name)
    {
        super(_name);
    }

    public String getKind()
    {
        return "subgraph";
    }

    private void checkGraph() throws GraphException
    {
        if (getGraph() == null) {
            throw new GraphException("Subgraph is not part of graph");
        }
    }

    public List getSubgraphList()
    {
        if (unmodSubgraphList == null) {
            unmodSubgraphList = Collections.unmodifiableList(subgraphList);
        }
        return unmodSubgraphList;
    }

    public void addSubgraph(Subgraph subgraph) throws GraphException
    {
        checkGraph();

        if (subgraph.getSubgraph() != null) {
            throw new GraphException("Element is already part of a subgraph");
        }

        getGraph().addElement(subgraph);

        subgraphList.add(subgraph);

        subgraph.setSubgraph(this);
    }

    public void removeSubgraph(Subgraph subgraph) throws GraphException
    {
        checkGraph();

        if (subgraph.getSubgraph() != this) {
            throw new GraphException("Element is not in this subgraph");
        }

        subgraphList.remove(subgraph);

        subgraph.setSubgraph(null);

        getGraph().removeElement(subgraph);
    }

    public List getNodeList()
    {
        if (unmodNodeList == null) {
            unmodNodeList = Collections.unmodifiableList(nodeList);
        }
        return unmodNodeList;
    }

    public void addNode(Node node) throws GraphException
    {
        checkGraph();

        if (node.getSubgraph() != null) {
            throw new GraphException("Element is already part of a subgraph");
        }

        getGraph().addElement(node);

        nodeList.add(node);

        node.setSubgraph(this);
    }

    public void removeNode(Node node) throws GraphException
    {
        checkGraph();

        if (node.getSubgraph() != this) {
            throw new GraphException("Element is not in this subgraph");
        }

        Graph graph = getGraph();

        graph.removeEdges(node.getInEdgeList());
        graph.removeEdges(node.getOutEdgeList());

        nodeList.remove(node);

        node.setSubgraph(null);

        graph.removeElement(node);
    }

}

