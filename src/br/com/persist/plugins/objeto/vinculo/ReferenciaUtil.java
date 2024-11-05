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

	public static int getIndice(Referencia ref, List<Referencia> referencias) throws ObjetoException {
		if (ref == null || referencias == null) {
			throw new ObjetoException("int getIndice(): ref == null || referencias == null");
		}
		for (int i = 0; i < referencias.size(); i++) {
			Referencia r = referencias.get(i);
			if (r.igual(ref)) {
				return i;
			}
		}
		return -1;
	}
}