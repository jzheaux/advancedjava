package com.joshcummings.security.random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class InsecureRandom {
	public static void main(String[] args) throws Exception {
		ImageIcon icon = new ImageIcon(InsecureRandom.class.getResource("/RandomNumber.png"));
        JOptionPane.showMessageDialog(
                null,
                "",
                "Random Number Comparisons", JOptionPane.INFORMATION_MESSAGE,
                icon);
	}
}
