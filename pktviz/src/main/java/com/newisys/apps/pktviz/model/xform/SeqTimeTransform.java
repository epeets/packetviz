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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.newisys.apps.pktviz.model.PacketInfo;

public class SeqTimeTransform implements PacketTimeTransform {

    private Map<Long, Long> realToSequentialMap;

    public void reset(Iterator<PacketInfo> packetIterator) {
        final Set<Long> times = new TreeSet<Long>();
        while (packetIterator.hasNext()) {
            final PacketInfo packetInfo = packetIterator.next();
            times.add(packetInfo.getFromTimeActual());
            times.add(packetInfo.getToTimeActual());
        }

        realToSequentialMap = new HashMap<Long, Long>(times.size());
        int sequentialIndex = 0;
        for (final Long realTime : times) {
            realToSequentialMap.put(realTime, (long) sequentialIndex++);
        }
    }

    public void transform(PacketInfo packet) {
        packet.setFromTime(getSequentialTime(packet.getFromTimeActual()));
        packet.setToTime(getSequentialTime(packet.getToTimeActual()));
    }

    long getSequentialTime(long time) {
        assert realToSequentialMap != null : "sequential time map not initialized";
        final Long timeIndex = realToSequentialMap.get(time);
        assert timeIndex != null : "time " + time + " not mapped to sequential time";
        return timeIndex;
    }
}
