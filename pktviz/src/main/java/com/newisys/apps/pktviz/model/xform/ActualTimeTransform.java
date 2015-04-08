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

import com.newisys.apps.pktviz.model.PacketInfo;

public class ActualTimeTransform
    implements PacketTimeTransform
{

    private int slant;

    public ActualTimeTransform()
    {
        this(25);
    }

    public ActualTimeTransform(int _slant)
    {
        slant = _slant;
    }

    public void transform(PacketInfo packet)
    {
        long fromTime = packet.getFromTimeActual();
        long toTime = packet.getToTimeActual();

        if (fromTime == toTime) toTime += slant;

        packet.setFromTime(fromTime);
        packet.setToTime(toTime);
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof ActualTimeTransform))
        {
            return false;
        }

        ActualTimeTransform other = (ActualTimeTransform) obj;
        return (slant == other.slant);
    }

    public void reset(Iterator packetIterator)
    {
        // NO-OP for ActualTimeTransform

    }

    /**
     * Override hashCode.
     *
     * @return the Objects hashcode.
     */
    public int hashCode()
    {
        int hashCode = 1;
        hashCode = 31 * hashCode + slant;
        return hashCode;
    }

}
