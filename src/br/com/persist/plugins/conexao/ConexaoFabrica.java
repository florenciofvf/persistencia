package br.com.persist.plugins.conexao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;

public class ConexaoFabrica extends AbstratoFabricaContainer {

	@Override
	public PaginaServico getPaginaServico() {
		return new ConexaoPaginaServico();
	}

	private class ConexaoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new ConexaoContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new ConexaoServico());
	}

	private class ConexaoServico extends AbstratoServico {
		@Override
		public void visivelFormulario(Formulario formulario) {
			ConexaoProvedor.inicializar();
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuConexao(formulario));
		return lista;
	}

	private class MenuConexao extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuConexao(Formulario formulario) {
			super(Constantes.LABEL_CONEXAO, Icones.BANCO);

			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new ConexaoContainer(null, formulario)));
			formularioAcao.setActionListener(e -> ConexaoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> ConexaoDialogo.criar(formulario));
		}
	}
}