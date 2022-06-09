package br.com.persist.plugins.checagem;

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

public class ChecagemFabrica extends AbstratoFabricaContainer {

	@Override
	public void inicializar() {
		Util.criarDiretorio(ChecagemConstantes.CHECAGENS);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new RequisicaoPaginaServico();
	}

	private class RequisicaoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new ChecagemContainer(null, formulario, null, stringPersistencia);
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
			super(Constantes.LABEL_VAZIO, Icones.SUCESSO);
			setText(ChecagemMensagens.getString(ChecagemConstantes.LABEL_CHECAGEM));
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new ChecagemContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> ChecagemFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> ChecagemDialogo.criar(formulario));
		}
	}
}