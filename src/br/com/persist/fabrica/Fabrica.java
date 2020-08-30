package br.com.persist.fabrica;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.util.Constantes;

public class Fabrica {
	private static final Logger LOG = Logger.getGlobal();

	private Fabrica() {
	}

	public static FabricaContainer criar(String classeFabricaEContainerDetalhe) {
		try {
			int pos = classeFabricaEContainerDetalhe.indexOf(Constantes.SEP);
			Class<?> klass = Class.forName(classeFabricaEContainerDetalhe.substring(0, pos));
			return (FabricaContainer) klass.newInstance();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
			return null;
		}
	}
}