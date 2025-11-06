package br.com.persist.plugins.anexo;

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

public class AnexoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Util.criarDiretorio(AnexoConstantes.ANEXOS);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new AnexoPaginaServico();
	}

	private class AnexoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new AnexoContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new AnexoServico());
	}

	private class AnexoServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuAnexo(formulario));
		return lista;
	}

	private class MenuAnexo extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAnexo(Formulario formulario) {
			super(Constantes.LABEL_ANEXOS, Icones.ANEXO);
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new AnexoContainer(null, formulario)));
			formularioAcao.setActionListener(e -> AnexoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> AnexoDialogo.criar(formulario));
		}
	}
}