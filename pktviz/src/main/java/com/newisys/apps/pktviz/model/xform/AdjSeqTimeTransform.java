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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.Maps;
import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.PacketNode;

public class AdjSeqTimeTransform implements PacketTimeTransform {

    private static final class AnalysisTickInfo {

        final Map<PacketNode, int[]> nodeAllocMap = Maps.newHashMap();
        int fromPackets;
        int toPackets;
    }

    private static final class FinalTickInfo {

        final long baseTime;
        final Map<PacketNode, int[]> nodeAllocMap;

        public FinalTickInfo(long baseTime, int expectedNodes) {
            this.baseTime = baseTime;
            nodeAllocMap = new LinkedHashMap<PacketNode, int[]>(expectedNodes);
        }
    }

    private Map<Long, FinalTickInfo> tickInfoMap;

    public void reset(Iterator<PacketInfo> packetIterator) {
        final SortedMap<Long, AnalysisTickInfo> analysisMap = Maps.newTreeMap();
        while (packetIterator.hasNext()) {
            final PacketInfo packet = packetIterator.next();
            final long fromTime = packet.getFromTimeActual();
            final AnalysisTickInfo fromInfo = getAnalysisTickInfo(analysisMap, fromTime);
            final int fromAlloc = mapIncrement(fromInfo.nodeAllocMap, packet.getFromNode(), 1);
            ++fromInfo.fromPackets;

            final long toTime = packet.getToTimeActual();
            final AnalysisTickInfo toInfo = getAnalysisTickInfo(analysisMap, toTime);
            mapIncrement(toInfo.nodeAllocMap, packet.getToNode(), fromTime == toTime ? fromAlloc
                    : 1);
            ++toInfo.toPackets;
        }

        tickInfoMap = Maps.newHashMapWithExpectedSize(analysisMap.size());
        int spanCount = 0;
        long seqTime = 0;
        for (final Map.Entry<Long, AnalysisTickInfo> entry : analysisMap.entrySet()) {
            final Long realTime = entry.getKey();
            final AnalysisTickInfo info = entry.getValue();
            tickInfoMap.put(realTime, new FinalTickInfo(seqTime, info.nodeAllocMap.size()));
            spanCount += info.fromPackets - info.toPackets;
            seqTime += mapMax(info.nodeAllocMap, 1) + 1;
        }
    }

    private static AnalysisTickInfo getAnalysisTickInfo(Map<Long, AnalysisTickInfo> map, long tick) {
        AnalysisTickInfo info = map.get(tick);
        if (info == null) {
            info = new AnalysisTickInfo();
            map.put(tick, info);
        }
        return info;
    }

    private static <T> int mapIncrement(Map<T, int[]> map, T key, int min) {
        int[] count = map.get(key);
        if (count == null) {
            count = new int[] { min };
            map.put(key, count);
        } else {
            count[0] = Math.max(min, count[0] + 1);
        }
        return count[0];
    }

    private static int mapMax(Map<?, int[]> map, int seed) {
        int max = seed;
        for (final int[] count : map.values()) {
            if (count[0] > max) {
                max = count[0];
            }
        }
        return max;
    }

    public void transform(PacketInfo packet) {
        final long fromTime = packet.getFromTimeActual();
        final FinalTickInfo fromInfo = getFinalTickInfo(fromTime);
        final int fromAlloc = mapIncrement(fromInfo.nodeAllocMap, packet.getFromNode(), 1);
        packet.setFromTime(fromInfo.baseTime + fromAlloc - 1);

        final long toTime = packet.getToTimeActual();
        final FinalTickInfo toInfo = getFinalTickInfo(toTime);
        final int toAlloc = mapIncrement(toInfo.nodeAllocMap, packet.getToNode(),
                fromTime == toTime ? fromAlloc : 1);
        packet.setToTime(toInfo.baseTime + toAlloc - 1);
    }

    public FinalTickInfo getFinalTickInfo(long time) {
        assert tickInfoMap != null : "sequential time map not initialized";
        final FinalTickInfo tickInfo = tickInfoMap.get(time);
        assert tickInfo != null : "time " + time + " not mapped to sequential time";
        return tickInfo;
    }
}
