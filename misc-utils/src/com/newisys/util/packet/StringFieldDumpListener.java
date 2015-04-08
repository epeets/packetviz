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


public class StringFieldDumpListener
extends AbstractFieldDumpListener
{
    private String valueDelim;
    private String fieldDelim;
    private StringBuffer buf;

    public StringFieldDumpListener() {
        this("=", ", ");
    }

    public StringFieldDumpListener(String _valueDelim, String _fieldDelim) {
        valueDelim = _valueDelim;
        fieldDelim = _fieldDelim;
        buf = new StringBuffer(80);
    }

    public void clear() {
        buf.setLength(0);
    }

	public void dumpString(String name, String value) {
        if (buf.length() > 0) buf.append(fieldDelim);
        buf.append(name);
        buf.append(valueDelim);
        buf.append(value);
	}

	public String toString() {
		return buf.toString();
	}

}
