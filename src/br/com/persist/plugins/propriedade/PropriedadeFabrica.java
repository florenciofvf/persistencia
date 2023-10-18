package br.com.persist.plugins.propriedade;

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

//		<menu classeFabrica="br.com.persist.plugins.propriedade.PropriedadeFabrica" ativo="true" />
public class PropriedadeFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		br.com.persist.assistencia.Util.criarDiretorio(PropriedadeConstantes.PROPRIEDADES);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new PropriedadePaginaServico();
	}

	private class PropriedadePaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new PropriedadeContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new PropriedadeServico());
	}

	private class PropriedadeServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		if (menu.getItemCount() > 0) {
			menu.addSeparator();
		}
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuPropriedade(formulario));
		return lista;
	}

	private class MenuPropriedade extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuPropriedade(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.EDIT);
			setText(PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new PropriedadeContainer(null, formulario)));
			formularioAcao.setActionListener(e -> PropriedadeFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> PropriedadeDialogo.criar(formulario));
		}
	}
}