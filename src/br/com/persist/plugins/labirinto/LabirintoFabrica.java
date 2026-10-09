package br.com.persist.plugins.labirinto;

import java.util.Arrays;
import java.util.List;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class LabirintoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Preferencias.addOutraPreferencia(LabirintoPreferencia.class);
		Util.criarDiretorio(LabirintoConstantes.LABIRINTO);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new LabirintoConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new LabirintoPaginaServico();
	}

	private class LabirintoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new LabirintoContainer(null, formulario, null, stringPersistencia);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new LabirintoServico());
	}

	private class LabirintoServico extends AbstratoServico {
	}

	@Override
	public void processarMenu(Formulario formulario, Menu menu) {
		menu.add(new MenuLabirinto(formulario));
	}

	private class MenuLabirinto extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuLabirinto(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.VERTICAL);
			setText(LabirintoMensagens.getString(LabirintoConstantes.LABEL_LABIRINTO));
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new LabirintoContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> LabirintoFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> LabirintoDialogo.criar(formulario));
		}
	}
}