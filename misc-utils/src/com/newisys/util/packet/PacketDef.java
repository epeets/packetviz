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

package com.newisys.util.packet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.newisys.util.bitvector.SimpleBitVector;

public class PacketDef
{
    private int maxBits;
    private List<FieldDef> fields;

    public PacketDef(int _maxBits)
    {
        maxBits = _maxBits;
        fields = new LinkedList<FieldDef>();
    }

    public void addFieldDef(FieldDef def)
    {
        if (def.endBit >= maxBits) {
            throw new IllegalArgumentException("Field is not within packet");
        }
        fields.add(def);
    }

    public String extractString(SimpleBitVector v)
    {
        StringBuffer buf = null;
        for (Iterator i = fields.iterator(); i.hasNext(); ) {
            FieldDef def = (FieldDef)i.next();
            String s = def.extractString(v);
            if (s != null) {
                if (buf != null) {
                    buf.append(',');
                } else {
                    buf = new StringBuffer();
                }
                buf.append(s);
            }
        }
        return (buf != null) ? buf.toString() : null;
    }
}

