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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a mapping of unique names to Symbols.
 */
public class SymbolTable
{
    /**
     * Underlying map of names (class String) to symbols (class Symbol).
     */
    private HashMap<String, Symbol> symbolMap = new HashMap<String, Symbol>();

    /**
     * Cached unmodifiable map used to expose a view of the symbol map.
     */
    private Map<String, Symbol> unmodSymbolMap;

    /**
     * Returns an unmodifable view of the symbol map.
     * 
     * @return an unmodifiable view of the symbol map
     */
    public Map getSymbolMap()
    {
        if (unmodSymbolMap == null) {
            unmodSymbolMap = Collections.unmodifiableMap(symbolMap);
        }
        return unmodSymbolMap;
    }

    /**
     * Looks up a symbol by name, returning null if not found.
     *
     * @param name the symbol name
     * @return returns the Symbol corresponding to <code>name</code> or null
     *      if no symbol is associated with <code>name</code>
     */
    public Symbol getSymbol(String name)
    {
        checkName(name);
        return symbolMap.get(name);
    }

    /**
     * Adds a new symbol to the table. Throws a SymbolException if the symbol
     * has a duplicate name.
     * 
     * @param symbol the Symbol to add
     * @throws SymbolException if this SymbolTable already contains <code>symbol</code>
     */
    public void addSymbol(Symbol symbol) throws SymbolException
    {
        String name = symbol.name;
        checkName(name);
        if (symbolMap.containsKey(name)) {
            throw new SymbolException("Duplicate name: " + name);
        }
        symbolMap.put(name, symbol);
        symbol.table = this;
    }

    /**
     * Removes a symbol from the table. Throws a SymbolException if the symbol
     * is not in the table.
     * 
     * @param symbol the Symbol to remove
     * @throws SymbolException if this SymbolTable doesn't contain <code>symbol</code>
     */
    public void removeSymbol(Symbol symbol) throws SymbolException
    {
        if (symbol.table != this) {
            throw new SymbolException("Symbol not in this table");
        }
        symbolMap.remove(symbol.name);
        symbol.table = null;
    }

    /**
     * Renames a symbol in the table. Throws a SymbolException if the symbol
     * has a duplicate name.
     * 
     * @param symbol the Symbol to rename
     * @param name the new name for <code>symbol</code>
     * @throws SymbolException if this SymbolTable doesn't contain <code>symbol</code>
     * 
     */
    public void renameSymbol(Symbol symbol, String name) throws SymbolException
    {
        if (symbol.table != this) {
            throw new SymbolException("Symbol not in this table");
        }
        checkName(name);
        if (symbolMap.containsKey(name)) {
            throw new SymbolException("Duplicate name: " + name);
        }
        symbolMap.remove(symbol.name);
        symbol.name = name;
        symbolMap.put(name, symbol);
    }

    /**
     * Ensures that the symbol name is not null.
     * 
     * @param name the name of the Symbol
     */
    private void checkName(String name)
    {
        if (name == null) {
            throw new NullPointerException();
        }
    }

}

