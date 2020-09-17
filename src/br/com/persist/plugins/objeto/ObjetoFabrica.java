package br.com.persist.plugins.objeto;

import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.plugins.arquivo.ArquivoEvento;
import br.com.persist.plugins.arquivo.ArquivoProvedor;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.metadado.MetadadoEvento;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.PosicaoDimensao;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class ObjetoFabrica extends AbstratoFabricaContainer {

	@Override
	public PaginaServico getPaginaServico() {
		return new ObjetoPaginaServico();
	}

	private class ObjetoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			File file = ArquivoProvedor.restaurarStringPersistencia(stringPersistencia);
			ObjetoContainer container = criarObjetoContainer(formulario);
			container.abrirArquivo(file);
			return container;
		}
	}

	private ObjetoContainer criarObjetoContainer(Formulario formulario) {
		ObjetoContainer container = new ObjetoContainer(null, formulario);
		container.setAbortarFecharComESCSuperficie(true);
		return container;
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new ObjetoServico());
	}

	private class ObjetoServico extends AbstratoServico {
		@Override
		public void processar(Formulario formulario, Map<String, Object> args) {
			checarArquivo(formulario, args);
			checarMetadado(formulario, args);
		}

		private void checarArquivo(Formulario formulario, Map<String, Object> args) {
			File file = (File) args.get(ArquivoEvento.ABRIR_ARQUIVO);

			if (file != null) {
				Boolean fichario = (Boolean) args.get(ArquivoEvento.FICHARIO);

				if (Boolean.TRUE.equals(fichario)) {
					Pagina pagina = getPaginaServico().criarPagina(formulario, file.getAbsolutePath());
					ObjetoProvedor.setParentFile(file.getParentFile());
					formulario.adicionarPagina(pagina);
				} else {
					abrirNoFormulario(formulario, file);
				}
			}
		}

		private void checarMetadado(Formulario formulario, Map<String, Object> args) {
			Metadado metadado = (Metadado) args.get(MetadadoEvento.ABRIR_METADADO);

			if (metadado != null) {
				String metodo = (String) args.get(MetadadoEvento.METODO);
				Boolean boolCircular = (Boolean) args.get(MetadadoEvento.CIRCULAR);
				boolean circular = Boolean.TRUE.equals(boolCircular);

				if (MetadadoEvento.ABRIR_EXPORTACAO_METADADO_FORM.equals(metodo)) {
					ObjetoFormulario form = ObjetoFormulario.criar(formulario,
							new File(Mensagens.getString("label.abrir_exportacao")));
					form.abrirExportacaoImportacaoMetadado(metadado, true, circular);
					posicao(formulario, form);

				} else if (MetadadoEvento.ABRIR_IMPORTACAO_METADADO_FORM.equals(metodo)) {
					ObjetoFormulario form = ObjetoFormulario.criar(formulario,
							new File(Mensagens.getString("label.abrir_importacao")));
					form.abrirExportacaoImportacaoMetadado(metadado, false, circular);
					posicao(formulario, form);

				} else if (MetadadoEvento.EXPORTAR_METADADO_RAIZ_FORM.equals(metodo) && metadado.getEhRaiz()
						&& !metadado.estaVazio()) {
					ObjetoFormulario form = ObjetoFormulario.criar(formulario,
							new File(Mensagens.getString("label.exportar")));
					form.exportarMetadadoRaiz(metadado);
					posicao(formulario, form);

				} else if (MetadadoEvento.ABRIR_EXPORTACAO_METADADO_FICH.equals(metodo)) {
					ObjetoContainer container = criarObjetoContainer(formulario);
					container.abrirExportacaoImportacaoMetadado(metadado, true, circular);
					container.setTituloTemporario(Mensagens.getString("label.abrir_exportacao"));
					formulario.adicionarPagina(container);

				} else if (MetadadoEvento.ABRIR_IMPORTACAO_METADADO_FICH.equals(metodo)) {
					ObjetoContainer container = criarObjetoContainer(formulario);
					container.abrirExportacaoImportacaoMetadado(metadado, false, circular);
					container.setTituloTemporario(Mensagens.getString("label.abrir_importacao"));
					formulario.adicionarPagina(container);

				} else if (MetadadoEvento.EXPORTAR_METADADO_RAIZ_FICH.equals(metodo) && metadado.getEhRaiz()
						&& !metadado.estaVazio()) {
					ObjetoContainer container = criarObjetoContainer(formulario);
					container.exportarMetadadoRaiz(metadado);
					container.setTituloTemporario(Mensagens.getString("label.exportar"));
					formulario.adicionarPagina(container);
				}
			}
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		if (menu.getItemCount() > 0) {
			menu.addSeparator();
		}

		List<JMenuItem> lista = new ArrayList<>();

		JMenuItem itemNovo = new JMenuItem(Mensagens.getString(Constantes.LABEL_NOVO), Icones.CUBO);
		itemNovo.addActionListener(e -> novo(formulario));
		lista.add(itemNovo);

		lista.add(new MenuAbrir(formulario));
		return lista;
	}

	private class MenuAbrir extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAbrir(Formulario formulario) {
			super("label.abrir", Icones.ABRIR, false);
			formularioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
			ficharioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));

			formularioAcao.setActionListener(e -> abrirEmFormulario(formulario));
			ficharioAcao.setActionListener(e -> abrirNoFichario(formulario));
		}

		private void abrirNoFichario(Formulario formulario) {
			File[] files = getSelectedFiles(formulario, true);

			if (files != null) {
				for (File file : files) {
					Pagina pagina = getPaginaServico().criarPagina(formulario, file.getAbsolutePath());
					ObjetoProvedor.setParentFile(file.getParentFile());
					formulario.adicionarPagina(pagina);
				}
			}
		}

		private void abrirEmFormulario(Formulario formulario) {
			File[] files = getSelectedFiles(formulario, true);

			if (files != null) {
				for (File file : files) {
					abrirNoFormulario(formulario, file);
				}
			}
		}

		private File[] getSelectedFiles(Formulario formulario, boolean multiSelection) {
			JFileChooser fileChooser = Util.criarFileChooser(ObjetoProvedor.getParentFile(), multiSelection);
			int opcao = fileChooser.showOpenDialog(formulario);
			if (opcao != JFileChooser.APPROVE_OPTION) {
				return new File[0];
			}
			return fileChooser.getSelectedFiles();
		}
	}

	private void novo(Formulario formulario) {
		Pagina pagina = getPaginaServico().criarPagina(formulario, Constantes.VAZIO);
		formulario.adicionarPagina(pagina);
	}

	public static void abrirNoFormulario(Formulario formulario, File file) {
		if (file == null || !file.isFile()) {
			return;
		}

		ObjetoFormulario form = ObjetoFormulario.criar(formulario, file);
		form.abrirArquivo(file);
		posicao(formulario, form);
	}

	public static void abrirNoFormulario(Formulario formulario, String stringPersistencia, Graphics g,
			InternalConfig config) {
		File file = ArquivoProvedor.restaurarStringPersistencia(stringPersistencia);

		if (file == null || !file.isFile()) {
			return;
		}

		try {
			ObjetoColetor objetoColetor = new ObjetoColetor();
			XML.processar(file, new ObjetoHandler(objetoColetor));
			abrirNoFormulario(formulario, file, objetoColetor, g, config);
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, formulario);
		}
	}

	private static void abrirNoFormulario(Formulario formulario, File file, ObjetoColetor coletor, Graphics g,
			InternalConfig config) {
		ObjetoFormulario form = ObjetoFormulario.criar(formulario, file);
		form.abrirArquivo(file, coletor, g, config);
		posicao(formulario, form);
	}

	private static void posicao(Formulario formulario, ObjetoFormulario form) {
		formulario.checarPreferenciasLarguraAltura();
		PosicaoDimensao pd = formulario.criarPosicaoDimensaoSeValido();

		if (pd != null) {
			form.setBounds(pd.getX(), pd.getY(), pd.getLargura(), pd.getAltura());
		} else {
			form.setLocationRelativeTo(formulario);
		}

		form.setVisible(true);
	}
}