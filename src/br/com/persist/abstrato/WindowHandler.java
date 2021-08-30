package br.com.persist.abstrato;

import java.awt.Window;

public interface WindowHandler {
	public void windowActivatedHandler(Window window);

	public void windowClosingHandler(Window window);

	public void windowOpenedHandler(Window window);
}