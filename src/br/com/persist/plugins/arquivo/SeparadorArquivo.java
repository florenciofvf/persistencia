package br.com.persist.plugins.arquivo;

import br.com.persist.abstrato.AbstratoPagina;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;

public class SeparadorArquivo extends AbstratoPagina {
	public static final String STRING_PERSISTENCIA = "\\o/fvf\\o/";

	@Override
	public String getStringPersistencia() {
		return STRING_PERSISTENCIA;
	}

	@Override
	public void tabActivatedHandler(Fichario fichario) {
		int cont = 0;
		while (cont < fichario.getTabCount()) {
			if (fichario.getPagina(cont).getComponent() != null) {
				fichario.setSelectedIndex(cont);
				break;
			}
			cont++;
		}
	}

	@Override
	public Class<?> getClasseFabrica() {
		return ArquivoFabrica.class;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public boolean isAtivo() {
				return false;
			}
		};
	}
}