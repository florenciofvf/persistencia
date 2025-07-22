package br.com.persist.plugins.instrucao.biblionativo;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class IDesktop {
	private static final Desktop DESKTOP = Desktop.getDesktop();

	private IDesktop() {
	}

	@Biblio(0)
	public static void open(Object absoluto) throws IOException {
		if (absoluto == null) {
			return;
		}
		DESKTOP.open(new File(absoluto.toString()));
	}

	@Biblio(1)
	public static void edit(Object absoluto) throws IOException {
		if (absoluto == null) {
			return;
		}
		DESKTOP.edit(new File(absoluto.toString()));
	}
}