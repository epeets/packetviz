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

package com.newisys.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An efficient Iterator over one (or zero) objects.
 */
public class SingleObjectIterator implements Iterator
{
    private Object value;

    /**
     * Constructs an empty (zero object) Iterator.
     */
    public SingleObjectIterator()
    {
        this.value = null;
    }

    /**
     * Constructs a single object Iterator returning the given Object.
     * 
     * @param value the Object to return from the Iterator
     */
    public SingleObjectIterator(Object value)
    {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        throw new UnsupportedOperationException("remove() not supported");
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        return value != null;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next()
    {
        if (value == null)
        {
            throw new NoSuchElementException();
        }
        Object next = value;
        value = null;
        return next;
    }
}
