package br.com.persist.plugins.fragmento;

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
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class FragmentoFabrica extends AbstratoFabricaContainer {

	@Override
	public void inicializar() {
		Util.criarDiretorio(Constantes.FRAGMENTOS);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new FragmentoPaginaServico();
	}

	private class FragmentoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new FragmentoContainer(null, formulario, null);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new FragmentoServico());
	}

	private class FragmentoServico extends AbstratoServico {
		@Override
		public void visivelFormulario(Formulario formulario) {
			FragmentoProvedor.inicializar();
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuFragmento(formulario));
		return lista;
	}

	private class MenuFragmento extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuFragmento(Formulario formulario) {
			super(Constantes.LABEL_FRAGMENTO, Icones.FRAGMENTO);
			ficharioAcao
					.setActionListener(e -> formulario.adicionarPagina(new FragmentoContainer(null, formulario, null)));
			formularioAcao.setActionListener(e -> FragmentoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> FragmentoDialogo.criar(formulario));
		}
	}
}