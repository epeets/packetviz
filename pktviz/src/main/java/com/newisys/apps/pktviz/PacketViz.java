/*
 * PacketViz packet visualization for the Java (TM) Platform
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

package com.newisys.apps.pktviz;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.UIManager;

public class PacketViz
{
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            MainFrame mf = new MainFrame();
            ControlPanelDialog cpd = mf.getControlPanelDialog();
            PacketDumpDialog pdd = mf.getPacketDumpDialog();

            if (args.length > 1)
            {
                File packetFile = getFileFromArg(args[1]);
                mf.setPropsFile(new File(args[1]));
            }
            if (args.length > 0)
            {
                mf.openPacketGraph(new File(args[0]));
            }

            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice dsd = ge.getDefaultScreenDevice();
            final GraphicsConfiguration dc = dsd.getDefaultConfiguration();
            final Rectangle screenBounds = dc.getBounds();

            final Rectangle totalBounds = new Rectangle(screenBounds);
            totalBounds.grow(-screenBounds.width / 32, -screenBounds.height / 16);

            int cpdWidth = cpd.getWidth();
            final Rectangle mfBounds = new Rectangle(totalBounds);
            mfBounds.width -= cpdWidth - 10;
            mf.setBounds(mfBounds);

            int cpdLeft = mfBounds.x + mfBounds.width + 10;
            cpd.setLocation(cpdLeft, mfBounds.y);

            int cpdHeight = cpd.getHeight();
            int pdfTop = mfBounds.y + cpdHeight + 10;
            pdd.setLocation(cpdLeft, pdfTop);
            pdd.setSize(cpdWidth, mfBounds.height - cpdHeight - 10);

            mf.show();
            cpd.show();
            pdd.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // Exit upon recieving an exception on this level
            System.exit(1);
        }
    }

    private static File getFileFromArg(String path)
    {
        File file;
        file = new File(path);
        if(file.exists())
        {
            return file;
        }
        else
        {
            //assume relative path
            file = new File(System.getProperty("user.path"), path);
            if(file.exists())
            {
                return file;
            }
        }
        System.err.printf("Could not find file at path=%s\n", path);
        return null;
    }
}
