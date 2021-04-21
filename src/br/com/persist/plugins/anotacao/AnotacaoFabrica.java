package br.com.persist.plugins.anotacao;

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

public class AnotacaoFabrica extends AbstratoFabricaContainer {

	@Override
	public void inicializar() {
		Util.criarDiretorio(AnotacaoConstantes.ANOTACOES);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new AnotacaoPaginaServico();
	}

	private class AnotacaoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new AnotacaoContainer(null, formulario, null);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuAnotacao(formulario));
		return lista;
	}

	private class MenuAnotacao extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAnotacao(Formulario formulario) {
			super(AnotacaoConstantes.LABEL_ANOTACOES, Icones.PANEL4);
			ficharioAcao
					.setActionListener(e -> formulario.adicionarPagina(new AnotacaoContainer(null, formulario, null)));
			formularioAcao.setActionListener(e -> AnotacaoFormulario.criar(formulario, Constantes.VAZIO));
			dialogoAcao.setActionListener(e -> AnotacaoDialogo.criar(formulario));
		}
	}
}