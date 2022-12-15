package br.com.persist.painel;

import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;

public class PainelUtil {
	private PainelUtil() {
	}

	public static PainelSetor getSetor(DropTargetDropEvent e, PainelSetor... setores) {
		Point p = e.getLocation();
		for (PainelSetor setor : setores) {
			if (setor.contem(p.x, p.y)) {
				return setor;
			}
		}
		return null;
	}
}