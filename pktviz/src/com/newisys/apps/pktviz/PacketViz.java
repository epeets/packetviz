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

import java.awt.Dimension;
import java.awt.Toolkit;
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

            Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = defaultToolkit.getScreenSize();

            Dimension totalSize = new Dimension(screenSize.width * 15 / 16,
                screenSize.height * 7 / 8);

            int cpdWidth = cpd.getWidth();
            Dimension mfSize = new Dimension(totalSize.width - cpdWidth - 10,
                totalSize.height);
            mf.setSize(mfSize);

            int mfLeft = (screenSize.width - totalSize.width) / 2;
            int mfTop = (screenSize.height - totalSize.height) / 2;
            mf.setLocation(mfLeft, mfTop);

            int cpdLeft = mfLeft + mfSize.width + 10;
            cpd.setLocation(cpdLeft, mfTop);

            int cpdHeight = cpd.getHeight();
            int pdfTop = mfTop + cpdHeight + 10;
            pdd.setLocation(cpdLeft, pdfTop);
            pdd.setSize(cpdWidth, totalSize.height - cpdHeight - 10);

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
