package br.com.persist.plugins.requisicao;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class RequisicaoFabrica extends AbstratoFabricaContainer {

	@Override
	public void inicializar() {
		Util.criarDiretorio(RequisicaoConstantes.REQUISICOES);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new RequisicaoPaginaServico();
	}

	private class RequisicaoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new RequisicaoContainer(null, formulario, null, stringPersistencia);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuRequisicao(formulario));
		return lista;
	}

	private class MenuRequisicao extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuRequisicao(Formulario formulario) {
			super(Constantes.LABEL_REQUISICAO, Icones.URL);
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new RequisicaoContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> RequisicaoFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> RequisicaoDialogo.criar(formulario));
		}
	}
}