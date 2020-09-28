package br.com.persist.plugins.conexao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import br.com.persist.formulario.FormularioEvento;

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

		@Override
		public void processar(Formulario formulario, Map<String, Object> args) {
			Boolean fechar = (Boolean) args.get(FormularioEvento.FECHAR_CONEXOES);
			if (Boolean.TRUE.equals(fechar)) {
				try {
					ConexaoProvedor.fecharConexoes();
				} catch (Exception ex) {
					Util.stackTraceAndMessage(formulario.getClass().getName() + ".fechar()", ex, formulario);
				}
			}
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
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