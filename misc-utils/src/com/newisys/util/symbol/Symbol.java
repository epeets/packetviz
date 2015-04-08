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

package com.newisys.util.symbol;

/**
 * Base class for named object that can be put in a SymbolTable.
 */
public class Symbol
{
    /**
     * Name used to refer to this symbol.
     */
    String name;

    /**
     * Symbol table this symbol is currently in.
     */
    SymbolTable table;

    public Symbol()
    {
    }

    public Symbol(String _name)
    {
        name = _name;
    }

    public final String getName()
    {
        return name;
    }

    public final void setName(String _name) throws SymbolException
    {
        if (table == null) {
            name = _name;
        } else {
            table.renameSymbol(this, _name);
        }
    }

    public String toString()
    {
        return getClass().getName() + "(" + name + ")";
    }

}

