package br.com.persist.plugins.robo;

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

public class RoboFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Preferencias.addOutraPreferencia(RoboPreferencia.class);
		Util.criarDiretorio(RoboConstantes.ROBOSCRIPTS);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new RoboConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new RoboPaginaServico();
	}

	private class RoboPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new RoboContainer(null, formulario, null, stringPersistencia);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new RoboServico());
	}

	private class RoboServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuRobo(formulario));
		return lista;
	}

	private class MenuRobo extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuRobo(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.CONFIG);
			setText(RoboMensagens.getString(RoboConstantes.LABEL_ROBO));
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new RoboContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> RoboFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> RoboDialogo.criar(formulario));
		}
	}
}