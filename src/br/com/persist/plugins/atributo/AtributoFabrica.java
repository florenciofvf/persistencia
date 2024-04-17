package br.com.persist.plugins.atributo;

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

public class AtributoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		br.com.persist.assistencia.Preferencias.addOutraPreferencia(AtributoPreferencia.class);
		br.com.persist.assistencia.Util.criarDiretorio(AtributoConstantes.ATRIBUTO);
	}

	@Override
	public br.com.persist.abstrato.AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new AtributoConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new AtributoPaginaServico();
	}

	private class AtributoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new AtributoContainer(null, formulario, null, stringPersistencia);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new AtributoServico());
	}

	private class AtributoServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuAtributo(formulario));
		return lista;
	}

	private class MenuAtributo extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAtributo(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.REGION);
			setText(AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO));
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new AtributoContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> AtributoFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> AtributoDialogo.criar(formulario));
		}
	}
}