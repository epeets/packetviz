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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.newisys.util.symbol.SymbolException;
import com.newisys.util.symbol.SymbolTable;

/**
 * Abstract graph, containing nodes optionally connected by edges.
 * Nodes may also be arranged in hierarchical subgraphs.
 */
public class Graph
extends Subgraph
{
    /**
     * Symbol table listing all elements in the graph.
     */
    private SymbolTable symbolTable = new SymbolTable();

    /**
     * Count of each type of element within the graph,
     * used for generating unique element names.
     */
    private HashMap<String, Integer> classCounter = new HashMap<String, Integer>();

    public Graph(String _name)
    {
        super(_name);
        setGraph(this);
    }

    public String getKind()
    {
        return "graph";
    }

    public GraphElement getElement(String name)
    {
        return (GraphElement)symbolTable.getSymbol(name);
    }

    public Map getElementMap()
    {
        return symbolTable.getSymbolMap();
    }

    public void addEdge(Edge edge) throws GraphException
    {
        addElement(edge);

        edge.getFromNode().addOutEdge(edge);
        edge.getToNode().addInEdge(edge);
    }

    public void removeEdge(Edge edge) throws GraphException
    {
        edge.getFromNode().removeOutEdge(edge);
        edge.getToNode().removeInEdge(edge);

        removeElement(edge);
    }

    public void removeEdges(List edgeList) throws GraphException
    {
        Iterator i = edgeList.iterator();
        while (i.hasNext()) {
            Edge e = (Edge)i.next();
            removeEdge(e);
        }
    }

    protected void addElement(GraphElement elem) throws GraphException
    {
        if (elem.getGraph() != null) {
            throw new GraphException("Element is already part of a graph");
        }

        try {
            String name = elem.getName();
            if (name == null) {
                String className = elem.getKind();

                Integer counter = classCounter.get(className);
                int i = (counter != null) ? counter.intValue() : 0;

                name = className + i;
                elem.setName(name);

                counter = new Integer(++i);
                classCounter.put(className, counter);
            }

            symbolTable.addSymbol(elem);
        }
        catch (SymbolException e) {
            throw new GraphException(e.getMessage());
        }

        elem.setGraph(this);
    }

    protected void removeElement(GraphElement elem) throws GraphException
    {
        if (elem.getGraph() != this) {
            throw new GraphException("Element is not in this graph");
        }

        try {
            symbolTable.removeSymbol(elem);
        }
        catch (SymbolException e) {
            throw new GraphException(e.getMessage());
        }

        elem.setGraph(null);
    }

    public void dumpElements()
    {
        Map symbolMap = symbolTable.getSymbolMap();
        Iterator iter = symbolMap.values().iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            System.out.println(obj);
        }
    }

}

