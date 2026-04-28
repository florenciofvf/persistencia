package br.com.persist.plugins.expressao.biblionativo;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class NDesktop {
	private static final Desktop DESKTOP = Desktop.getDesktop();

	private NDesktop() {
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