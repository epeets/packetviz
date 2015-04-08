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

public class GroupFieldDef
extends FieldDef
{
    private List<FieldDef> subfields;

    public GroupFieldDef(int _startBit, int _endBit)
    {
        this(_startBit, _endBit, "Group");
    }

    public GroupFieldDef(int _startBit, int _endBit, String _name)
    {
        super(_startBit, _endBit, _name);
        subfields = new LinkedList<FieldDef>();
    }

    private void checkSubfield(FieldDef def)
    {
        if (def.startBit < startBit || def.endBit > endBit) {
            throw new IllegalArgumentException("Subfield (" + def.toString() +
                ") is not within superfield (" + toString() + ")");
        }
    }

    public void addFieldDef(FieldDef def)
    {
        checkSubfield(def);
        subfields.add(def);
    }

    public String extractString(SimpleBitVector v)
    {
        StringBuffer buf = null;
        for (Iterator i = subfields.iterator(); i.hasNext(); ) {
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

