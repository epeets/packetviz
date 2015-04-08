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

import com.newisys.util.bitvector.SimpleBitVector;
import com.newisys.util.format.SizedIntegerFormat;

public class EnumFieldDef
extends AbstractLongFieldDef
{
    private String enumValues[];

    public EnumFieldDef(int _startBit, int _endBit, String _name)
    {
        super(_startBit, _endBit, _name);
        enumValues = new String[1 << getSize()];
    }

    public void setEnumValue(int value, String name)
    {
        enumValues[value] = name;
    }

    public void setEnumValue(int base, int mask, String name)
    {
        FieldIterator iter = new FieldIterator(base, mask, getSize());
        while (iter.hasNext()) {
            enumValues[iter.next()] = name;
        }
    }

    public String extractString(SimpleBitVector v)
    {
        if (startBit >= v.size()) return null;
        int value = (int)extractLong(v);
        String s = enumValues[value];
        if (s == null) {
            s = "Undefined(" +  SizedIntegerFormat.format(value, getSize(), 16) + ")";
        }
        return name + "=" + s;
    }
}

