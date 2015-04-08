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

public class LongFieldDef
extends AbstractLongFieldDef
{
    private int radix;

    public LongFieldDef(int _startBit, int _endBit, String _name)
    {
        this(_startBit, _endBit, _name, 10);
    }

    public LongFieldDef(int _startBit, int _endBit, String _name, int _radix)
    {
        super(_startBit, _endBit, _name);
        radix = _radix;
    }

    public String extractString(SimpleBitVector v)
    {
        if (startBit >= v.size()) return null;

        return name + "=" + SizedIntegerFormat.format(extractLong(v), getSize(), radix);
    }
}

