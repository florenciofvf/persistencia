package br.com.persist.abstrato;

import java.awt.Dialog;

public interface DialogHandler {
	public void dialogActivatedHandler(Dialog dialog);

	public void dialogClosingHandler(Dialog dialog);

	public void dialogOpenedHandler(Dialog dialog);
}