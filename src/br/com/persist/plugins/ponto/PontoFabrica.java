package br.com.persist.plugins.ponto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class PontoFabrica extends AbstratoFabricaContainer {
	@Override
	public PaginaServico getPaginaServico() {
		return new PontoPaginaServico();
	}

	private class PontoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new PontoContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new PontoServico());
	}

	private class PontoServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuPonto(formulario));
		return lista;
	}

	private class MenuPonto extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuPonto(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.TIMER);
			setText(PontoMensagens.getString(PontoConstantes.LABEL_PONTO));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new PontoContainer(null, formulario)));
			formularioAcao.setActionListener(e -> PontoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> PontoDialogo.criar(formulario));
		}
	}
}