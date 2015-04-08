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
 * An iterator that chains together the iteration of multiple sub-iterators.
 * @param <T> the element type
 */
public class ChainIterator<T>
    implements Iterator<T>
{
    private final Iterator<Iterator< ? extends T>> iterIter;
    private Iterator< ? extends T> curIter;

    /**
     * Constructs a ChainIterator with the given Iterator over Iterators.
     * @param iterIter an Iterator over Iterators
     */
    public ChainIterator(Iterator<Iterator< ? extends T>> iterIter)
    {
        assert (iterIter != null);
        this.iterIter = iterIter;
        findNextIter();
    }

    private void findNextIter()
    {
        while (curIter == null || !curIter.hasNext())
        {
            if (iterIter.hasNext())
            {
                curIter = iterIter.next();
            }
            else
            {
                curIter = null;
                break;
            }
        }
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        if (curIter == null)
        {
            throw new IllegalStateException("No current element");
        }
        curIter.remove();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        return curIter != null;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public T next()
    {
        if (curIter != null)
        {
            T next = curIter.next();
            findNextIter();
            return next;
        }
        else
        {
            throw new NoSuchElementException();
        }
    }
}
