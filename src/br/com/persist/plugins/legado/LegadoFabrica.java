package br.com.persist.plugins.legado;

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

public class LegadoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Preferencias.addOutraPreferencia(LegadoPreferencia.class);
		Util.criarDiretorio(LegadoConstantes.LEGADO);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new LegadoConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new LegadoPaginaServico();
	}

	private class LegadoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new LegadoContainer(null, formulario, null, stringPersistencia);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new LegadoServico());
	}

	private class LegadoServico extends AbstratoServico {
	}

	@Override
	public void processarMenu(Formulario formulario, Menu menu) {
		menu.add(new MenuLegado(formulario));
	}

	private class MenuLegado extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuLegado(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.REFERENCIA);
			setText(LegadoMensagens.getString(LegadoConstantes.LABEL_LEGADO));
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new LegadoContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> LegadoFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> LegadoDialogo.criar(formulario));
		}
	}
}