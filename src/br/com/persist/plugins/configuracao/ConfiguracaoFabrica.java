package br.com.persist.plugins.configuracao;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class ConfiguracaoFabrica extends AbstratoFabricaContainer {
	@Override
	public PaginaServico getPaginaServico() {
		return new ConfiguracaoPaginaServico();
	}

	private class ConfiguracaoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new ConfiguracaoContainer(null, formulario);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuConfiguracao(formulario));
		return lista;
	}

	private class MenuConfiguracao extends MenuPadrao1 {
		private Action importarAcao = actionMenu("label.importar", Icones.BAIXAR2);
		private Action exportarAcao = actionMenu("label.exportar", Icones.TOP);
		private static final long serialVersionUID = 1L;

		private MenuConfiguracao(Formulario formulario) {
			super(Constantes.LABEL_CONFIGURACOES, Icones.CONFIG);
			addSeparator();
			addMenuItem(exportarAcao);
			addMenuItem(importarAcao);
			ficharioAcao
					.setActionListener(e -> formulario.adicionarPagina(new ConfiguracaoContainer(null, formulario)));
			formularioAcao.setActionListener(e -> ConfiguracaoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> ConfiguracaoDialogo.criar(formulario));
			exportarAcao.setActionListener(e -> exportar());
			importarAcao.setActionListener(e -> importar());
		}

		private void exportar() {
			try {
				Preferencias.exportar();
				Util.mensagem(this, "SUCESSO");
			} catch (Exception ex) {
				Util.stackTraceAndMessage(getClass().getName(), ex, this);
			}
		}

		private void importar() {
			try {
				Preferencias.importar();
				Util.mensagem(this, "SUCESSO");
			} catch (Exception ex) {
				Util.stackTraceAndMessage(getClass().getName(), ex, this);
			}
		}
	}
}