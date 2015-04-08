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


import com.newisys.util.format.SizedIntegerFormat;

public abstract class AbstractFieldDumpListener 
    implements FieldDumpListener
{
    public void dumpAddr(String name, long value) {
        dumpString(name, SizedIntegerFormat.format(value, 40, 16));
    }

    public void dumpBits(String name, int value, int size) {
        dumpString(name, SizedIntegerFormat.format(value, size, 2));
    }

    public void dumpBoolean(String name, boolean value) {
        dumpString(name, value ? "1" : "0");
    }

    public void dumpData(String name, int[] value) {
        StringBuffer buf = new StringBuffer(2 + value.length * 8);

        buf.append(SizedIntegerFormat.getRadixPrefix(16));

        for (int i = 0; i < value.length; ++i) {
            long u = ((long) value[i]) & 0xffffffffL;
            buf.append(SizedIntegerFormat.format(u, 32, 16, false));
        }

        dumpString(name, buf.toString());
    }

    public void dumpInt(String name, int value) {
        dumpString(name, String.valueOf(value));
    }

    public void dumpInt(String name, int value, int size, int radix) {
        dumpString(name, SizedIntegerFormat.format(value, size, radix));
    }

	public void dumpLong(String name, long value) {
        dumpString(name, String.valueOf(value));
	}

    public void dumpLong(String name, long value, int size, int radix) {
        dumpString(name, SizedIntegerFormat.format(value, size, radix));
    }

}
