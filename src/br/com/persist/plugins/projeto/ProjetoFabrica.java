package br.com.persist.plugins.projeto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class ProjetoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Util.criarDiretorio(ProjetoConstantes.PROJETOS);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new ProjetoConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new ProjetoPaginaServico();
	}

	private class ProjetoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new ProjetoContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new ProjetoServico());
	}

	private class ProjetoServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuProjeto(formulario));
		return lista;
	}

	private class MenuProjeto extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuProjeto(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.EXPANDIR);
			setText(ProjetoMensagens.getString(ProjetoConstantes.LABEL_PROJETO));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new ProjetoContainer(null, formulario)));
			formularioAcao.setActionListener(e -> ProjetoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> ProjetoDialogo.criar(formulario));
		}
	}
}