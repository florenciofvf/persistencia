package br.com.persist.plugins.entrega;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class EntregaFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Preferencias.addOutraPreferencia(EntregaPreferencia.class);
		Util.criarDiretorio(EntregaConstantes.ENTREGAS);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new EntregaConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new EntregaPaginaServico();
	}

	private class EntregaPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new EntregaContainer(null, formulario, null, stringPersistencia);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuEntrega(formulario));
		return lista;
	}

	private class MenuEntrega extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuEntrega(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.BOLA_VERDE);
			setText(EntregaMensagens.getString(EntregaConstantes.LABEL_ENTREGA));
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new EntregaContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> EntregaFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> EntregaDialogo.criar(formulario));
		}
	}
}