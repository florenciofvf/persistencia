package br.com.persist.plugins.instrucao;

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

public class InstrucaoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		br.com.persist.assistencia.Preferencias.addOutraPreferencia(InstrucaoPreferencia.class);
		br.com.persist.assistencia.Util.criarDiretorio(InstrucaoConstantes.INSTRUCAO);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new InstrucaoPaginaServico();
	}

	private class InstrucaoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new InstrucaoContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new InstrucaoServico());
	}

	private class InstrucaoServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		if (menu.getItemCount() > 0) {
			menu.addSeparator();
		}
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuInstrucao(formulario));
		return lista;
	}

	private class MenuInstrucao extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuInstrucao(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.FRAGMENTO);
			setText(InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new InstrucaoContainer(null, formulario)));
			formularioAcao.setActionListener(e -> InstrucaoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> InstrucaoDialogo.criar(formulario));
		}
	}
}