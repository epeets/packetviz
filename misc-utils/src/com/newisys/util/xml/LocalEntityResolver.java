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

package com.newisys.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Attempts to resolve entities by interpreting the system ID as a path, and 
 * looking for a local file with the same name. Local files are searched for in
 * the directory given to the constructor. If the entity has no system ID, or
 * a local file is not found, an optional base resolver is used. If no base
 * resolver is given, resolveEntity() returns null, which selects the default
 * entity resolution behavior.
 */
public class LocalEntityResolver implements EntityResolver
{
    private final File basePath;
    private final EntityResolver baseResolver;

    /**
     * Constructs a LocalEntityResolver that searches for local files in the
     * given directory.
     * @param basePath directory to search for local files
     */
    public LocalEntityResolver(File basePath)
    {
        this(basePath, null);
    }

    /**
     * Constructs a LocalEntityResolver that searches for local files in the
     * given directory, and uses the given base resolver if a local file is not
     * found.
     * @param basePath directory to search for local files
     * @param baseResolver fallback resolver if a local file is not found
     */
    public LocalEntityResolver(File basePath, EntityResolver baseResolver)
    {
        this.basePath = basePath;
        this.baseResolver = baseResolver;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException
    {
        if (systemId != null)
        {
            URL url = new URL(systemId);
            File urlPath = new File(url.getPath());
            File localFile = new File(basePath, urlPath.getName());
            if (localFile.exists())
            {
                return new InputSource(new FileInputStream(localFile));
            }
        }
        if (baseResolver != null)
        {
            return baseResolver.resolveEntity(publicId, systemId);
        }
        else
        {
            return null;
        }
    }
}