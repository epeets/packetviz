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

package com.newisys.apps.pktviz.model.xform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.PacketNode;

/**
 * <PRE>
 * Sequential time transform models time as non-idle ordered
 * events such that each distinct time maps over to one delta
 * cycle in the display.
 * 
 * Example:
 * 
 *  ___________________________________________
 *  |  Input               | Output           |
 *  |  From     To         | From       To    |
 *  -------------------------------------------
 *  |  100      200        |    0        4    |
 *  |  110     1000        |    1        6    |
 *  |  120      300        |    2        5    |
 *  |  140     1100        |    3        7    |
 *  -------------------------------------------
 * 
 * To display properly, this requires a reset() done
 * with all of the packets known beforehand.
 * 
 * </PRE>
 * 
 * @author trevor.robinson
 * @author bill.flanders
 *
 */
public class SeqTimeTransform
    implements PacketTimeTransform
{

    /**
     * Enable spacing of duplicate times to allow
     * for better readability
     */
    private static final boolean ENABLE_TIME_ADJUSTOR = false;

    /**
     * Duplicate from/to map
     */
    private Map<FromTo, Boolean> duplicateFromToMap = new HashMap<FromTo, Boolean>();

    /**
     * Sorted list of times present in the packets
     */
    private LinkedList<Long> times;

    /**
     * Implements table shown above in class javadoc
     */
    private Map<Long, Long> realToSequentialMap;
    private Map<PacketInfo, Boolean> packetsRegisteredMap;

    /**
     * In the case of duplicate "to" times, this integer
     * is used to offset the "to" time in sequential transform
     */
    private int timeAdjustor;

    public SeqTimeTransform()
    {

    }

    public void reset(Iterator packetIterator)
    {
        times = new LinkedList<Long>();
        packetsRegisteredMap = new HashMap<PacketInfo, Boolean>();
        realToSequentialMap = new HashMap<Long, Long>();

        timeAdjustor = 0;

        PacketInfo packetInfo;
        while (packetIterator.hasNext())
        {
            packetInfo = (PacketInfo) packetIterator.next();
            packetsRegisteredMap.put(packetInfo, Boolean.TRUE);
            if (!times.contains(packetInfo.getFromTimeActual()))
            {
                times.add(packetInfo.getFromTimeActual());
            }
            if (!times.contains(packetInfo.getToTimeActual()))
            {
                times.add(packetInfo.getToTimeActual());
            }
        }
        Collections.sort(times);

        long sequentialIndex = 0;
        for (Long realTime : times)
        {
            realToSequentialMap.put(realTime, sequentialIndex);
            sequentialIndex++;
        }
    }

    public void transform(PacketInfo packet)
    {
        // If this is in our map
        if (packetsRegisteredMap.get(packet) != null)
        {
            long fromTime = packet.getFromTimeActual();
            long toTime = packet.getToTimeActual();

            PacketNode fromNode = packet.getFromNode();
            PacketNode toNode = packet.getToNode();
            FromTo fromTo = new FromTo(fromTime, toTime, fromNode, toNode);
            if (duplicateFromToMap.get(fromTo) != null)
            {
                timeAdjustor++;
            }
            else
            {
                duplicateFromToMap.put(fromTo, true);
            }

            if (fromNode.equals(toNode))
            {
                // If this is a horizontal message, add more time
                timeAdjustor++;
            }

            if (ENABLE_TIME_ADJUSTOR)
            {
                packet.setFromTime(getSequentialTime(fromTime) + timeAdjustor);
                packet.setToTime(getSequentialTime(toTime) + timeAdjustor);
            }
            else
            {
                packet.setFromTime(getSequentialTime(fromTime));
                packet.setToTime(getSequentialTime(toTime));
            }
        }
        else
        {
            assert (false) : "Packet not found: " + packet;
        }

    }

    private long getSequentialTime(long time)
    {
        Long timeIndex = realToSequentialMap.get(time);
        assert (timeIndex != -1);
        return timeIndex;
    }

    public boolean equals(Object obj)
    {
        return obj instanceof SeqTimeTransform;
    }

    private static class FromTo
    {
        private long fromTime;
        private long toTime;
        private PacketNode fromNode;
        private PacketNode toNode;

        public FromTo(
            long fromTime,
            long toTime,
            PacketNode fromNode,
            PacketNode toNode)
        {
            super();
            // TODO Auto-generated constructor stub
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        /**
         * Override hashCode.
         *
         * @return the Objects hashcode.
         */
        public int hashCode()
        {
            int hashCode = 1;
            hashCode = 31 * hashCode + (int) (+fromTime ^ (fromTime >>> 32));
            hashCode = 31 * hashCode + (int) (+toTime ^ (toTime >>> 32));
            hashCode = 31 * hashCode
                + (fromNode == null ? 0 : fromNode.hashCode());
            hashCode = 31 * hashCode + (toNode == null ? 0 : toNode.hashCode());
            return hashCode;
        }

        /**
         * Returns <code>true</code> if this <code>FromTo</code> is the same as the o argument.
         *
         * @return <code>true</code> if this <code>FromTo</code> is the same as the o argument.
         */
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null)
            {
                return false;
            }
            if (o.getClass() != getClass())
            {
                return false;
            }
            FromTo castedObj = (FromTo) o;
            return ((this.fromTime == castedObj.fromTime)
                && (this.toTime == castedObj.toTime)
                && (this.fromNode == null ? castedObj.fromNode == null
                    : this.fromNode.equals(castedObj.fromNode)) && (this.toNode == null
                ? castedObj.toNode == null
                : this.toNode.equals(castedObj.toNode)));
        }

    }
}
