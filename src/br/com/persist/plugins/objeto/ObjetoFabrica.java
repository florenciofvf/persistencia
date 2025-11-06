package br.com.persist.plugins.objeto;

import java.awt.Window;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.plugins.arquivo.ArquivoEvento;
import br.com.persist.plugins.arquivo.ArquivoProvedor;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.metadado.MetadadoEvento;
import br.com.persist.plugins.metadado.MetadadoException;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.macro.MacroProvedor;

public class ObjetoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Preferencias.addOutraPreferencia(ObjetoPreferencia.class);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new ObjetoConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new ObjetoPaginaServico();
	}

	private class ObjetoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			File file = ArquivoProvedor.restaurarStringPersistencia(stringPersistencia);
			ObjetoContainer container = criarObjetoContainer(formulario);
			container.abrirArquivo(file, null);
			return container;
		}
	}

	private ObjetoContainer criarObjetoContainer(Formulario formulario) {
		return new ObjetoContainer(null, formulario);
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new ObjetoServico());
	}

	private class ObjetoServico extends AbstratoServico {
		@Override
		public void windowOpenedHandler(Window window) {
			MacroProvedor.inicializar();
		}

		@Override
		public void processar(Formulario formulario, Map<String, Object> args) {
			checarArquivo(formulario, args);
			try {
				checarMetadado(formulario, args);
			} catch (MetadadoException | ObjetoException | AssistenciaException ex) {
				Util.mensagemFormulario(formulario, ex.getMessage());
			}
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
					abrirNoFormulario(formulario, file, null);
				}
			}
		}

		private void checarMetadado(Formulario formulario, Map<String, Object> args)
				throws MetadadoException, ObjetoException, AssistenciaException {
			Metadado metadado = (Metadado) args.get(MetadadoEvento.ABRIR_METADADO);
			if (metadado != null) {
				String metodo = (String) args.get(MetadadoEvento.METODO);
				Boolean boolCircular = (Boolean) args.get(MetadadoEvento.CIRCULAR);
				Conexao conexao = (Conexao) args.get(MetadadoEvento.CONEXAO);
				boolean circular = Boolean.TRUE.equals(boolCircular);
				if (MetadadoEvento.ABRIR_EXPORTACAO_METADADO_FORM.equals(metodo)) {
					abrirExportacaoMetadadoFormulario(formulario, conexao, metadado, circular);
				} else if (MetadadoEvento.ABRIR_EXPORTACAO_METADADO_FICH.equals(metodo)) {
					abrirExportacaoMetadadoFichario(formulario, conexao, metadado, circular);
				} else if (MetadadoEvento.ABRIR_IMPORTACAO_METADADO_FORM.equals(metodo)) {
					abrirImportacaoMetadadoFormulario(formulario, conexao, metadado, circular);
				} else if (MetadadoEvento.ABRIR_IMPORTACAO_METADADO_FICH.equals(metodo)) {
					abrirImportacaoMetadadoFichario(formulario, conexao, metadado, circular);
				} else if (MetadadoEvento.EXPORTAR_METADADO_RAIZ_FORM.equals(metodo) && metadado.getEhRaiz()
						&& !metadado.estaVazio()) {
					exportarMetadadoRaizFormulario(formulario, metadado);
				} else if (MetadadoEvento.EXPORTAR_METADADO_RAIZ_FICH.equals(metodo) && metadado.getEhRaiz()
						&& !metadado.estaVazio()) {
					exportarMetadadoRaizFichario(formulario, metadado);
				}
			}
		}

		private void abrirExportacaoMetadadoFormulario(Formulario formulario, Conexao conexao, Metadado metadado,
				boolean circular) throws MetadadoException, ObjetoException, AssistenciaException {
			ObjetoFormulario form = ObjetoFormulario.criar(formulario,
					new File(ObjetoMensagens.getString("label.abrir_exportacao")));
			form.abrirExportacaoImportacaoMetadado(conexao, metadado, true, circular);
		}

		private void abrirExportacaoMetadadoFichario(Formulario formulario, Conexao conexao, Metadado metadado,
				boolean circular) throws MetadadoException, ObjetoException, AssistenciaException {
			ObjetoContainer container = criarObjetoContainer(formulario);
			AtomicReference<String> tituloTemp = new AtomicReference<>();
			container.abrirExportacaoImportacaoMetadado(conexao, metadado, true, circular, tituloTemp);
			if (Util.isEmpty(tituloTemp.get())) {
				container.setTituloTemporario(ObjetoMensagens.getString("label.abrir_exportacao"));
			}
			formulario.adicionarPagina(container);
		}

		private void abrirImportacaoMetadadoFormulario(Formulario formulario, Conexao conexao, Metadado metadado,
				boolean circular) throws MetadadoException, ObjetoException, AssistenciaException {
			ObjetoFormulario form = ObjetoFormulario.criar(formulario,
					new File(ObjetoMensagens.getString("label.abrir_importacao")));
			form.abrirExportacaoImportacaoMetadado(conexao, metadado, false, circular);
		}

		private void abrirImportacaoMetadadoFichario(Formulario formulario, Conexao conexao, Metadado metadado,
				boolean circular) throws MetadadoException, ObjetoException, AssistenciaException {
			ObjetoContainer container = criarObjetoContainer(formulario);
			AtomicReference<String> tituloTemp = new AtomicReference<>();
			container.abrirExportacaoImportacaoMetadado(conexao, metadado, false, circular, tituloTemp);
			if (Util.isEmpty(tituloTemp.get())) {
				container.setTituloTemporario(ObjetoMensagens.getString("label.abrir_importacao"));
			}
			formulario.adicionarPagina(container);
		}

		private void exportarMetadadoRaizFormulario(Formulario formulario, Metadado metadado)
				throws AssistenciaException {
			ObjetoFormulario form = ObjetoFormulario.criar(formulario, new File(Mensagens.getString("label.exportar")));
			form.exportarMetadadoRaiz(metadado);
		}

		private void exportarMetadadoRaizFichario(Formulario formulario, Metadado metadado)
				throws AssistenciaException {
			ObjetoContainer container = criarObjetoContainer(formulario);
			container.exportarMetadadoRaiz(metadado);
			container.setTituloTemporario(Mensagens.getString("label.exportar"));
			formulario.adicionarPagina(container);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
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
			super("label.abrir", Icones.ABRIR);
			formularioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
			ficharioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
			formularioAcao.setActionListener(e -> abrirEmFormulario(formulario));
			ficharioAcao.setActionListener(e -> abrirNoFichario(formulario));
			dialogoAcao.setActionListener(e -> abrirEmDialogo(formulario));
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
					abrirNoFormulario(formulario, file, null);
				}
			}
		}

		private void abrirEmDialogo(Formulario formulario) {
			File[] files = getSelectedFiles(formulario, true);
			if (files != null) {
				for (File file : files) {
					abrirNoDialogo(formulario, file, null);
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

	public static void abrirNoFormulario(Formulario formulario, File file, InternalConfig config) {
		if (file == null || !file.isFile()) {
			return;
		}
		ObjetoFormulario form = ObjetoFormulario.criar(formulario, file);
		form.abrirArquivo(file, config);
	}

	public static void abrirNoDialogo(Formulario formulario, File file, InternalConfig config) {
		if (file == null || !file.isFile()) {
			return;
		}
		ObjetoDialogo form = ObjetoDialogo.criar(formulario, file);
		form.abrirArquivo(file, config);
	}

	public static void abrirNoFormulario(Formulario formulario, String stringPersistencia, InternalConfig config) {
		File file = ArquivoProvedor.restaurarStringPersistencia(stringPersistencia);
		if (file != null) {
			if (file.exists() && file.isFile()) {
				abrirArquivo(formulario, config, file);
			} else {
				Util.mensagem(formulario, Mensagens.getString("msg.arquivo_invalido", file.getAbsolutePath()));
			}
		}
	}

	private static void abrirArquivo(Formulario formulario, InternalConfig config, File file) {
		try {
			ObjetoColetor objetoColetor = new ObjetoColetor();
			XML.processar(file, new ObjetoHandler(objetoColetor));
			abrirNoFormulario(formulario, file, objetoColetor, config);
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, formulario);
		}
	}

	private static void abrirNoFormulario(Formulario formulario, File file, ObjetoColetor coletor,
			InternalConfig config) throws XMLException, ObjetoException, AssistenciaException {
		ObjetoFormulario form = ObjetoFormulario.criar(formulario, file);
		form.abrirArquivo(file, coletor, config);
	}
}