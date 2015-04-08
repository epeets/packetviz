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

import java.awt.Container;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.newisys.apps.pktviz.model.PacketGraph;
import com.newisys.apps.pktviz.model.PacketGraphListener;
import com.newisys.apps.pktviz.model.PacketGraphSource;
import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.PacketNode;

abstract class JAbstractPacketGraph
    extends JLayeredPane
    implements PacketGraphListener
{
    protected PacketGraphSource source;
    protected PacketGraph graph;

    protected JPanel nodePane;
    protected Map<PacketNode, JAbstractPacketNode> nodeViewMap;

    public JAbstractPacketGraph()
    {
        setLayout(new SimpleOverlayLayout());

        nodePane = new JPanel();
        nodePane.setLayout(new SimpleBoxLayout(nodePane, SimpleBoxLayout.Y_AXIS));
        add(nodePane);

        nodeViewMap = new HashMap<PacketNode, JAbstractPacketNode>();
    }

    public void setName(String name)
    {
        super.setName(name);
        nodePane.setName(name + ".nodePane");
    }

    public PacketGraphSource getSource()
    {
        return source;
    }

    public void setSource(PacketGraphSource _source)
    {
        if (source != _source)
        {
            if (source != null)
            {
                graph.removeListener(this);
                removeNodes();
            }

            source = _source;

            if (source != null)
            {
                graph = source.getPacketGraph();
                assert (graph != null);

                addAllNodes(graph.getTopLevelNodes(), nodePane);
                graph.addListener(this);
            }
        }
    }

    private void removeNodes()
    {
        nodePane.removeAll();
        nodeViewMap.clear();
    }

    protected abstract JAbstractPacketNode newNode(PacketNode node);

    protected abstract JAbstractPacketNode newGroup(PacketNode node);

    private void addAllNodes(Collection nodes, Container c)
    {
        Iterator i = nodes.iterator();
        while (i.hasNext())
        {
            PacketNode node = (PacketNode) i.next();
            List childList = node.getChildList();

            JAbstractPacketNode nodeView;
            if (childList.isEmpty())
            {
                nodeView = newNode(node);
            }
            else
            {
                nodeView = newGroup(node);
            }

            nodeViewMap.put(node, nodeView);
            c.add(nodeView);

            if (!childList.isEmpty())
            {
                addAllNodes(childList, nodeView);
            }
        }
    }

    protected JAbstractPacketNode getViewForNode(PacketNode node)
    {
        return nodeViewMap.get(node);
    }

    public void nodeAdded(PacketNode node, boolean topLevel)
    {
        nodeUpdated(node);
    }

    public void nodeUpdated(PacketNode node)
    {
        // look up current view for node
        JAbstractPacketNode nodeView = getViewForNode(node);

        // check for non-existent view or wrong view type
        boolean hasChildren = node.hasChildren();
        if (nodeView == null || hasChildren != nodeView.isGroup())
        {
            // if node view is wrong type, remove from parent container
            if (nodeView != null)
            {
                Container cont = nodeView.getParent();
                if (cont != null)
                {
                    cont.remove(nodeView);
                }
            }
            // create correct view type
            if (hasChildren)
            {
                nodeView = newGroup(node);
            }
            else
            {
                nodeView = newNode(node);
            }
            // update node-view mapping
            nodeViewMap.put(node, nodeView);
        }

        // update view properties
        nodeView.updateFromModel();

        // get correct parent container for node view
        PacketNode parent = node.getParent();
        Container parentContainer;
        List siblingList;
        if (parent != null)
        {
            parentContainer = nodeViewMap.get(parent);
            siblingList = parent.getChildList();
        }
        else
        {
            parentContainer = nodePane;
            siblingList = graph.getTopLevelNodes();
        }

        // move node view to correct container if necessary
        Container curContainer = nodeView.getParent();
        if (parentContainer != curContainer)
        {
            if (curContainer != null)
            {
                curContainer.remove(nodeView);
            }
            if (parentContainer != null)
            {
                int index = siblingList.indexOf(node);
                parentContainer.add(nodeView, index);
            }
        }
    }

    public void packetAdded(PacketInfo packet)
    {
        // do nothing
    }

    public void packetUpdated(PacketInfo packet)
    {
        // do nothing
    }

    public void filterChanged()
    {
        // TODO: show only used nodes
    }

}
