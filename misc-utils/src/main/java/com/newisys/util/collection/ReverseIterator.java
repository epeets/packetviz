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
import java.util.List;
import java.util.ListIterator;

public final class ReverseIterator<E>
    implements Iterator<E>
{
    private final ListIterator<E> base;

    public ReverseIterator(ListIterator<E> base)
    {
        this.base = base;
    }

    public boolean hasNext()
    {
        return base.hasPrevious();
    }

    public E next()
    {
        return base.previous();
    }

    public void remove()
    {
        base.remove();
    }

    public static <U> Iterator<U> reverseIterator(List<U> list)
    {
        return new ReverseIterator<U>(list.listIterator(list.size()));
    }
}
