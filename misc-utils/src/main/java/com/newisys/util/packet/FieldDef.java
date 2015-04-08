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

public abstract class FieldDef
{
    protected int startBit;
    protected int endBit;
    protected String name;

    public FieldDef(int _startBit, int _endBit, String _name)
    {
        if (_startBit > _endBit) {
            throw new IllegalArgumentException("Start bit is greater than end bit");
        }

        startBit = _startBit;
        endBit = _endBit;
        name = _name;
    }

    public int getStartBit()
    {
        return startBit;
    }

    public int getEndBit()
    {
        return endBit;
    }

    public int getSize()
    {
        return endBit - startBit + 1;
    }

    public String getName()
    {
        return name;
    }

    public abstract String extractString(SimpleBitVector v);

    public String toString()
    {
        return name + "[" + startBit + ":" + endBit + "]";
    }
}

