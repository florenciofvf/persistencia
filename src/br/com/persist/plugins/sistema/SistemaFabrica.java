package br.com.persist.plugins.sistema;

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
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class SistemaFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Preferencias.addOutraPreferencia(SistemaPreferencia.class);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new SistemaConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new SistemaPaginaServico();
	}

	private class SistemaPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new SistemaContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new SistemaServico());
	}

	private class SistemaServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuSistema(formulario));
		return lista;
	}

	private class MenuSistema extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuSistema(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.FIELDS);
			setText(SistemaMensagens.getString(SistemaConstantes.LABEL_SISTEMA));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new SistemaContainer(null, formulario)));
			formularioAcao.setActionListener(e -> SistemaFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> SistemaDialogo.criar(formulario));
		}
	}
}