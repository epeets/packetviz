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

public class VariantFieldDef
extends FieldDef
{
    private AbstractLongFieldDef tagFieldDef;
    private FieldDef varFieldDefs[];

    public VariantFieldDef(int _startBit, int _endBit,
        AbstractLongFieldDef _tagFieldDef)
    {
        this(_startBit, _endBit, "Variant", _tagFieldDef);
    }

    public VariantFieldDef(int _startBit, int _endBit, String _name,
        AbstractLongFieldDef _tagFieldDef)
    {
        super(_startBit, _endBit, _name);
        tagFieldDef = _tagFieldDef;
        varFieldDefs = new FieldDef[1 << getSize()];
    }

    private void checkSubfield(FieldDef def)
    {
        if (def.startBit < startBit || def.endBit > endBit) {
            throw new IllegalArgumentException("Subfield (" + def.toString() +
                ") is not within superfield (" + toString() + ")");
        }
    }

    public void setFieldDef(int value, FieldDef def)
    {
        checkSubfield(def);
        varFieldDefs[value] = def;
    }

    public void setFieldDef(int base, int mask, FieldDef def)
    {
        checkSubfield(def);
        FieldIterator iter = new FieldIterator(base, mask, getSize());
        while (iter.hasNext()) {
            varFieldDefs[iter.next()] = def;
        }
    }

    public String extractString(SimpleBitVector v)
    {
        if (startBit >= v.size()) return null;

        int value = (int)tagFieldDef.extractLong(v);
        FieldDef def = varFieldDefs[value];
        return (def != null) ? def.extractString(v) : null;
    }
}

