package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Mikhail on 03.06.2015.
 */
public class openwithgui {
    private JButton load_button;
    private JPanel root;

    public openwithgui() {

        load_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenFile of = new OpenFile();

                try {
                    of.pickMe();
                }
                catch (Exception exc) { }
            }
        });
    }
}
