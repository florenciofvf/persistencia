package br.com.persist.assistencia;

import java.awt.Component;

import javax.swing.SwingUtilities;

public class SwingUtilitario {

	private SwingUtilitario() {
	}

	public static void updateComponentTreeUI(Component c) {
		SwingUtilities.updateComponentTreeUI(c);
	}

	public static void invokeLater(Runnable r) {
		SwingUtilities.invokeLater(r);
	}
}