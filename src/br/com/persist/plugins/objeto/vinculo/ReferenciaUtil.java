package br.com.persist.plugins.objeto.vinculo;

import java.util.List;

import br.com.persist.plugins.objeto.ObjetoException;

public class ReferenciaUtil {
	private ReferenciaUtil() {
	}

	public static boolean contem(Referencia ref, List<Referencia> referencias) throws ObjetoException {
		if (ref == null || referencias == null) {
			throw new ObjetoException("boolean contem(): ref == null || referencias == null");
		}
		for (Referencia r : referencias) {
			if (r.igual(ref)) {
				return true;
			}
		}
		return false;
	}
}