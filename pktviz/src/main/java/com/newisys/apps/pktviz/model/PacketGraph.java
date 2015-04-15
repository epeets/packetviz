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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.newisys.apps.pktviz.model.filter.PacketFilter;
import com.newisys.apps.pktviz.model.xform.PacketTimeTransform;
import com.newisys.prtree.Interval;
import com.newisys.prtree.PRTree;
import com.newisys.prtree.PointIterator;
import com.newisys.prtree.SimpleInterval;

public final class PacketGraph
{
    private List<PacketGraphListener> listeners;
    private Map<String, PacketNode> nodeMap;
    private List<PacketNode> topLevelNodes;
    private PacketFilter packetFilter;
    private PacketTimeTransform timeTransform;
    private List<PacketInfo> packetList;
    private PRTree packetIndex;
    private boolean indexInvalid;

    public PacketGraph()
    {
        listeners = new LinkedList<PacketGraphListener>();
        nodeMap = new HashMap<String, PacketNode>();
        topLevelNodes = new LinkedList<PacketNode>();
        packetList = new LinkedList<PacketInfo>();
        packetIndex = new PRTree();
        indexInvalid = false;
    }

    public void addListener(PacketGraphListener lsnr)
    {
        listeners.add(lsnr);
    }

    public void removeListener(PacketGraphListener lsnr)
    {
        listeners.remove(lsnr);
    }

    public void addNode(PacketNode node, boolean topLevel)
    {
        addNode(node, topLevel, topLevelNodes.size());
    }

    public void addNode(PacketNode node, boolean topLevel, int index)
    {
        String nodeName = node.getName();
        if (nodeMap.containsKey(nodeName))
        {
            throw new RuntimeException("Duplicate node name");
        }

        node.setGraph(this);
        nodeMap.put(nodeName, node);
        if (topLevel) topLevelNodes.add(index, node);

        if (!listeners.isEmpty())
        {
            Iterator<PacketGraphListener> i = listeners.iterator();
            while (i.hasNext())
            {
                PacketGraphListener lsnr = i.next();
                lsnr.nodeAdded(node, topLevel);
            }
        }
    }

    void nodeUpdated(PacketNode node)
    {
        if (!listeners.isEmpty())
        {
            Iterator<PacketGraphListener> i = listeners.iterator();
            while (i.hasNext())
            {
                PacketGraphListener lsnr = i.next();
                lsnr.nodeUpdated(node);
            }
        }
    }

    public PacketNode getNode(String name)
    {
        return nodeMap.get(name);
    }

    public Collection<PacketNode> getNodes()
    {
        return nodeMap.values();
    }

    public List<PacketNode> getTopLevelNodes()
    {
        return topLevelNodes;
    }

    public PacketFilter getPacketFilter()
    {
        return packetFilter;
    }

    public void setPacketFilter(PacketFilter _packetFilter)
    {
        if (!objEquals(packetFilter, _packetFilter))
        {
            packetFilter = _packetFilter;
            filterChanged();
        }
    }

    public PacketTimeTransform getTimeTransform()
    {
        return timeTransform;
    }

    public void setTimeTransform(PacketTimeTransform _timeTransform)
    {
        if (!objEquals(timeTransform, _timeTransform))
        {
            timeTransform = _timeTransform;
            filterChanged();
        }
    }

    private static boolean objEquals(Object a, Object b)
    {
        return (a != null) ? a.equals(b) : b == null;
    }

    private void filterChanged()
    {
        indexInvalid = true;

        if (!listeners.isEmpty())
        {
            Iterator<PacketGraphListener> i = listeners.iterator();
            while (i.hasNext())
            {
                PacketGraphListener lsnr = i.next();
                lsnr.filterChanged();
            }
        }
    }

    public void addPacket(PacketInfo packet)
    {
        packet.setGraph(this);
        packetList.add(packet);

        if (matchesFilter(packet))
        {
            checkIndex();
            //indexPacket(packet);

            if (!listeners.isEmpty())
            {
                Iterator<PacketGraphListener> i = listeners.iterator();
                while (i.hasNext())
                {
                    PacketGraphListener lsnr = i.next();
                    lsnr.packetAdded(packet);
                }
            }
        }
    }

    private boolean matchesFilter(PacketInfo packet)
    {
        return packetFilter == null || packetFilter.matches(packet);
    }

    void packetUpdated(PacketInfo packet)
    {
        if (matchesFilter(packet))
        {
            if (!listeners.isEmpty())
            {
                Iterator<PacketGraphListener> i = listeners.iterator();
                while (i.hasNext())
                {
                    PacketGraphListener lsnr = i.next();
                    lsnr.packetUpdated(packet);
                }
            }
        }
    }

    public Collection<PacketInfo> getPackets()
    {
        return packetList;
    }

    public PointIterator getPacketTimes()
    {
        checkIndex();
        return packetIndex.pointIterator();
    }

    public Collection findPackets(long fromTime, long toTime)
    {
        checkIndex();
        Interval i = new SimpleInterval(fromTime, toTime);
        Set packetSet = packetIndex.queryInterval(i);
        return packetSet;
    }

    public long getFirstTime()
    {
        checkIndex();
        return !packetIndex.isEmpty() ? packetIndex.firstPoint() : 0;
    }

    public long getLastTime()
    {
        checkIndex();
        return !packetIndex.isEmpty() ? packetIndex.lastPoint() : 0;
    }

    private void checkIndex()
    {
        if (indexInvalid)
        {
            reindexPackets();
            indexInvalid = false;
        }
    }

    private void reindexPackets() {
        packetIndex.clear();

        if (timeTransform != null) {
            timeTransform.reset(Iterators.filter(packetList.iterator(),
                    new Predicate<PacketInfo>() {
                        public boolean apply(PacketInfo packet) {
                            return matchesFilter(packet);
                        }
                    }));
        }

        for (final PacketInfo packet : packetList) {
            if (matchesFilter(packet)) {
                if (timeTransform != null) {
                    timeTransform.transform(packet);
                }
                packetIndex.add(packet);
            }
        }
    }

    public void allPacketsAddedEvent()
    {
        reindexPackets();
    }
}
