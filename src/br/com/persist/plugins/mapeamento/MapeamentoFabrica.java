package br.com.persist.plugins.mapeamento;

import java.awt.Window;
import java.util.Arrays;
import java.util.List;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class MapeamentoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Util.criarDiretorio(MapeamentoConstantes.MAPEAMENTOS);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new MapeamentoPaginaServico();
	}

	private class MapeamentoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new MapeamentoContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new MapeamentoServico());
	}

	private class MapeamentoServico extends AbstratoServico {
		@Override
		public void windowOpenedHandler(Window window) {
			MapeamentoProvedor.inicializar();
		}
	}

	@Override
	public void processarMenu(Formulario formulario, Menu menu) {
		menu.add(new MenuMapeamento(formulario));
	}

	private class MenuMapeamento extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuMapeamento(Formulario formulario) {
			super(Constantes.LABEL_MAPEAMENTOS, Icones.REFERENCIA);
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new MapeamentoContainer(null, formulario)));
			formularioAcao.setActionListener(e -> MapeamentoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> MapeamentoDialogo.criar(formulario));
		}
	}
}