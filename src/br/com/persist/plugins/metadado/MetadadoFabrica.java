package br.com.persist.plugins.metadado;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;

public class MetadadoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Util.criarDiretorio(MetadadoConstantes.METADADOS);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new MetadadoConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new MetadadoPaginaServico();
	}

	private class MetadadoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			try {
				return new MetadadoContainer(null, formulario, null);
			} catch (ArgumentoException ex) {
				Util.mensagem(formulario, ex.getMessage());
				return null;
			}
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuMetadado(formulario));
		return lista;
	}

	private class MenuMetadado extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuMetadado(Formulario formulario) {
			super(Constantes.LABEL_METADADOS, Icones.CAMPOS);
			ficharioAcao.setActionListener(e -> adicionarPagina(formulario));
			formularioAcao.setActionListener(e -> criarForm(formulario));
			dialogoAcao.setActionListener(e -> criarDialog(formulario));
		}

		private void adicionarPagina(Formulario formulario) {
			try {
				formulario.adicionarPagina(new MetadadoContainer(null, formulario, null));
			} catch (ArgumentoException ex) {
				Util.mensagem(formulario, ex.getMessage());
			}
		}

		private void criarForm(Formulario formulario) {
			try {
				MetadadoFormulario.criar(formulario, (Conexao) null);
			} catch (ArgumentoException ex) {
				Util.mensagem(formulario, ex.getMessage());
			}
		}

		private void criarDialog(Formulario formulario) {
			try {
				MetadadoDialogo.criar(formulario, (Conexao) null);
			} catch (ArgumentoException ex) {
				Util.mensagem(formulario, ex.getMessage());
			}
		}
	}
}