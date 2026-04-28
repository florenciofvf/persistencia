package br.com.persist.plugins.expressao;

import java.io.File;
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
import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;

public class ExpressaoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Preferencias.addOutraPreferencia(ExpressaoPreferencia.class);
		Util.criarDiretorio(ExpressaoConstantes.EXPRESSOES);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new ExpressaoConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new ExpressaoPaginaServico();
	}

	private class ExpressaoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new ExpressaoContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new ExpressaoServico());
	}

	private class ExpressaoServico extends AbstratoServico {
		@Override
		public void processar(Formulario formulario, Map<String, Object> args) {
			if (args.get(ExpressaoEvento.BIBLIO_PARA_OBJETO_REQUEST) == null) {
				return;
			}
			List<String> lista = listarNomeBiblio();
			if (lista.isEmpty()) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (String item : lista) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(item);
			}
			args.put(ExpressaoEvento.BIBLIO_PARA_OBJETO_RESPONSE, sb.toString());
		}

		private List<String> listarNomeBiblio() {
			final String paraObjeto = "paraObjeto";
			List<String> lista = new ArrayList<>();
			File file = new File(CacheBiblioteca.COMPILADOS, paraObjeto);
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (File item : files) {
						lista.add(paraObjeto + "." + get(item.getName()));
					}
				}
			}
			return lista;
		}

		private String get(String name) {
			if (name.endsWith(Biblioteca.EXTENSAO)) {
				name = name.substring(0, name.length() - Biblioteca.EXTENSAO.length());
			}
			return name;
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuExpressao(formulario));
		return lista;
	}

	private class MenuExpressao extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuExpressao(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.ESTRELA);
			setText(ExpressaoMensagens.getString(ExpressaoConstantes.LABEL_EXPRESSAO));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new ExpressaoContainer(null, formulario)));
			formularioAcao.setActionListener(e -> ExpressaoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> ExpressaoDialogo.criar(formulario));
		}
	}
}