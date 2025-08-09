package br.com.persist.plugins.navegacao;

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
import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class NavegacaoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Preferencias.addOutraPreferencia(NavegacaoPreferencia.class);
		Util.criarDiretorio(NavegacaoConstantes.NAVEGACOES);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new NavegacaoConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new NavegacaoPaginaServico();
	}

	private class NavegacaoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new NavegacaoContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new NavegacaoServico());
	}

	private class NavegacaoServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuNavegacao(formulario));
		return lista;
	}

	private class MenuNavegacao extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuNavegacao(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.URL);
			setText(NavegacaoMensagens.getString(NavegacaoConstantes.LABEL_NAVEGACAO));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new NavegacaoContainer(null, formulario)));
			formularioAcao.setActionListener(e -> NavegacaoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> NavegacaoDialogo.criar(formulario));
		}
	}
}