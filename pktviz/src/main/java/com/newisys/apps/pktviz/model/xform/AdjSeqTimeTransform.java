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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.PacketNode;

public class AdjSeqTimeTransform extends SeqTimeTransform {

    private Set<FromTo> duplicateFromToSet = new HashSet<FromTo>();
    private int timeAdjustor;

    @Override
    public void reset(Iterator<PacketInfo> packetIterator) {
        super.reset(packetIterator);
        timeAdjustor = 0;
    }

    @Override
    public void transform(PacketInfo packet) {
        long fromTime = packet.getFromTimeActual();
        long toTime = packet.getToTimeActual();

        PacketNode fromNode = packet.getFromNode();
        PacketNode toNode = packet.getToNode();
        FromTo fromTo = new FromTo(fromTime, toTime, fromNode, toNode);
        if (duplicateFromToSet.contains(fromTo)) {
            timeAdjustor++;
        } else {
            duplicateFromToSet.add(fromTo);
        }

        if (fromNode.equals(toNode)) {
            // If this is a horizontal message, add more time
            timeAdjustor++;
        }

        packet.setFromTime(getSequentialTime(fromTime) + timeAdjustor);
        packet.setToTime(getSequentialTime(toTime) + timeAdjustor);
    }

    private static class FromTo {
        private long fromTime;
        private long toTime;
        private PacketNode fromNode;
        private PacketNode toNode;

        public FromTo(long fromTime, long toTime, PacketNode fromNode, PacketNode toNode) {
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        @Override
        public int hashCode() {
            return (int) (fromTime ^ (fromTime >>> 32)) ^ (int) (toTime ^ (toTime >>> 32)) ^
                    fromNode.hashCode() ^ toNode.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof FromTo)) {
                return false;
            }
            FromTo other = (FromTo) o;
            return fromTime == other.fromTime && toTime == other.toTime &&
                    fromNode.equals(other.fromNode) && toNode.equals(other.toNode);
        }
    }
}
