package br.com.persist.abstrato;

import javax.swing.JInternalFrame;

public interface WindowInternalHandler {
	public void windowInternalActivatedHandler(JInternalFrame internal);

	public void windowInternalClosingHandler(JInternalFrame internal);

	public void windowInternalOpenedHandler(JInternalFrame internal);
}