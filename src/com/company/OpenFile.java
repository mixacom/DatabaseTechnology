package com.company;

import javax.swing.*;
import java.io.File;

/**
 * Created by Mikhail on 03.06.2015.
 */
public class OpenFile {

    JFileChooser fileChoose = new JFileChooser();

    public void pickMe() throws Exception {
        if (fileChoose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File newFile = fileChoose.getSelectedFile();

//            EdgesUnit eun = new EdgesUnit(newFile);
        }
    }

}
