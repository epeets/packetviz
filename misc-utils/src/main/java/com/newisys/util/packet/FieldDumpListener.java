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


public interface FieldDumpListener {

    /// Dump an address and its decoded fields.
    void dumpAddr(String name, long value);

    /// Dump a binary field of the given size.
    void dumpBits(String name, int value, int size);

    /// Dump a boolean field.
    void dumpBoolean(String name, boolean value);

    /// Dump a data chunk stored in an int array.
    void dumpData(String name, int[] value);

    /// Dump an integer field using the default radix.
    void dumpInt(String name, int value);

    /// Dump an integer field using the given size and radix.
    void dumpInt(String name, int value, int size, int radix);

    /// Dump a long field using the default radix.
    void dumpLong(String name, long value);

    /// Dump a long field using the given size and radix.
    void dumpLong(String name, long value, int size, int radix);

    /// Dump a string field.
    void dumpString(String name, String value);

}
