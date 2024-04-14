package br.com.persist.plugins.objeto.vinculo;

import java.util.List;

public class ReferenciaUtil {
	private ReferenciaUtil() {
	}

	public static boolean contem(Referencia ref, List<Referencia> referencias) {
		if (ref == null || referencias == null) {
			throw new IllegalStateException();
		}
		for (Referencia r : referencias) {
			if (r.igual(ref)) {
				return true;
			}
		}
		return false;
	}
}