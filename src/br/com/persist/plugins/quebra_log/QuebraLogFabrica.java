package br.com.persist.plugins.quebra_log;

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

public class QuebraLogFabrica extends AbstratoFabricaContainer {
	@Override
	public PaginaServico getPaginaServico() {
		return new QuebraLogPaginaServico();
	}

	private class QuebraLogPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new QuebraLogContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new QuebraLogServico());
	}

	private class QuebraLogServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		if (menu.getItemCount() > 0) {
			menu.addSeparator();
		}
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuQuebraLog(formulario));
		return lista;
	}

	private class MenuQuebraLog extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuQuebraLog(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.ALINHA_ESQUERDO);
			setText(QuebraLogMensagens.getString(QuebraLogConstantes.LABEL_QUEBRA_LOG));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new QuebraLogContainer(null, formulario)));
			formularioAcao.setActionListener(e -> QuebraLogFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> QuebraLogDialogo.criar(formulario));
		}
	}
}
