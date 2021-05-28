package br.com.persist.plugins.requisicao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.CLONAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.NOVO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Base64Util;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TabbedPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Parser;
import br.com.persist.parser.ParserDialogo;
import br.com.persist.parser.ParserListener;
import br.com.persist.parser.Texto;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class RequisicaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final RequisicaoFichario fichario = new RequisicaoFichario();
	private static final File file = new File("requisicoes");
	private static final Logger LOG = Logger.getGlobal();
	private RequisicaoFormulario requisicaoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private RequisicaoDialogo requisicaoDialogo;

	public RequisicaoContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
		abrir(conteudo, idPagina);
	}

	public RequisicaoDialogo getRequisicaoDialogo() {
		return requisicaoDialogo;
	}

	public void setRequisicaoDialogo(RequisicaoDialogo requisicaoDialogo) {
		this.requisicaoDialogo = requisicaoDialogo;
		if (requisicaoDialogo != null) {
			requisicaoFormulario = null;
		}
	}

	public RequisicaoFormulario getRequisicaoFormulario() {
		return requisicaoFormulario;
	}

	public void setRequisicaoFormulario(RequisicaoFormulario requisicaoFormulario) {
		this.requisicaoFormulario = requisicaoFormulario;
		if (requisicaoFormulario != null) {
			requisicaoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
	}

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao);
	}

	public String getConteudo() {
		Pagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		Pagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return null;
	}

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	private void abrir(String conteudo, String idPagina) {
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					Pagina pagina = new Pagina(f);
					fichario.adicionarPagina(pagina);
				}
			}
		}
		fichario.setConteudo(conteudo, idPagina);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	static Action actionMenu(String chave) {
		return Action.acaoMenu(RequisicaoMensagens.getString(chave), null);
	}

	static Action actionIcon(String chave, Icon icon) {
		return Action.acaoIcon(RequisicaoMensagens.getString(chave), icon);
	}

	static Action actionIcon(String chave) {
		return actionIcon(chave, null);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action variaveisAcao = actionIcon("label.variaveis_sistema", Icones.BOLA_AMARELA);
		private Action retornar64Acao = actionIcon("label.retornar_base64", Icones.BOLA_AMARELA);
		private Action formatarAcao = actionIcon("label.formatar_frag_json", Icones.BOLA_VERDE);
		private Action excluirAtivoAcao = Action.actionIcon("label.excluir2", Icones.EXCLUIR);
		private Action base64Acao = actionIcon("label.criar_base64", Icones.BOLA_AMARELA);
		private Action modeloAcao = Action.actionIcon("label.modelo", Icones.BOLA_VERDE);
		private Action atualizarAcao = Action.actionIcon("label.requisicao", Icones.URL);
		private CheckBox chkCopiarAccessToken = new CheckBox();
		private CheckBox chkRespostaImagem = new CheckBox();
		private CheckBox chkRespostaJson = new CheckBox();

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, SALVAR);
			addButton(excluirAtivoAcao);
			add(chkRespostaImagem);
			add(chkRespostaJson);
			add(chkCopiarAccessToken);
			addButton(true, atualizarAcao);
			addButton(true, formatarAcao);
			addButton(modeloAcao);
			addButton(true, base64Acao);
			addButton(retornar64Acao);
			addButton(true, variaveisAcao);
			chkRespostaImagem.setToolTipText(RequisicaoMensagens.getString("label.resposta_imagem"));
			chkRespostaJson.setToolTipText(RequisicaoMensagens.getString("label.resposta_json"));
			String hint = RequisicaoMensagens.getString("label.copiar_access_token",
					RequisicaoMensagens.getString("label.resposta_json"));
			chkCopiarAccessToken.setToolTipText(hint);
			ButtonGroup grupo = new ButtonGroup();
			grupo.add(chkRespostaImagem);
			grupo.add(chkRespostaJson);
			eventos();
		}

		private void eventos() {
			chkRespostaImagem.addActionListener(
					e -> Preferencias.setBoolean("requisicao_response_imagem", chkRespostaImagem.isSelected()));
			chkRespostaJson.addActionListener(
					e -> Preferencias.setBoolean("requisicao_response_json", chkRespostaJson.isSelected()));
			chkCopiarAccessToken.addActionListener(
					e -> Preferencias.setBoolean("copiar_access_token", chkCopiarAccessToken.isSelected()));
			chkRespostaImagem.setSelected(Preferencias.getBoolean("requisicao_response_imagem"));
			chkRespostaJson.setSelected(Preferencias.getBoolean("requisicao_response_json"));
			chkCopiarAccessToken.setSelected(Preferencias.getBoolean("copiar_access_token"));
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
			retornar64Acao.setActionListener(e -> retornar64());
			variaveisAcao.setActionListener(e -> variaveis());
			atualizarAcao.setActionListener(e -> atualizar());
			formatarAcao.setActionListener(e -> formatar());
			base64Acao.setActionListener(e -> base64());
			modeloAcao.setActionListener(e -> modelo());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(RequisicaoContainer.this)) {
				RequisicaoFormulario.criar(formulario, RequisicaoContainer.this);
			} else if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
				RequisicaoFormulario.criar(formulario, RequisicaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (requisicaoFormulario != null) {
				requisicaoFormulario.excluirContainer();
				formulario.adicionarPagina(RequisicaoContainer.this);
			} else if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
				formulario.adicionarPagina(RequisicaoContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
			}
			RequisicaoFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
			}
			RequisicaoFormulario.criar(formulario, null, null);
		}

		void formularioVisivel() {
			buttonDestacar.estadoFormulario();
		}

		void paginaVisivel() {
			buttonDestacar.estadoFichario();
		}

		void dialogoVisivel() {
			buttonDestacar.estadoDialogo();
		}

		@Override
		protected void novo() {
			Object resp = Util.getValorInputDialog(RequisicaoContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(RequisicaoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					Pagina pagina = new Pagina(f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			Pagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(Pagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			Pagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(RequisicaoContainer.this,
					RequisicaoMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
				int indice = fichario.getSelectedIndex();
				ativa.excluir();
				fichario.remove(indice);
			}
		}

		@Override
		protected void atualizar() {
			Pagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				ativa.atualizar();
			}
		}

		private void formatar() {
			Pagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				ativa.formatar();
			}
		}

		private void modelo() {
			ParserDialogo form = ParserDialogo.criar((Dialog) null, parserListener);
			form.setLocationRelativeTo(formulario);
			form.setVisible(true);
		}

		private void base64() {
			Pagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				ativa.base64();
			}
		}

		private void retornar64() {
			Pagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				ativa.retornar64();
			}
		}

		private void variaveis() {
			Pagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				ativa.variaveis();
			}
		}
	}

	private transient ParserListener parserListener = new ParserListener() {
		@Override
		public void setParserTipo(Tipo tipo) {
			LOG.log(Level.FINEST, "setParserTipo");
		}

		@Override
		public boolean somenteModelo() {
			return true;
		}

		@Override
		public String getModelo() {
			return RequisicaoMensagens.getString("requisicao.modelo");
		}

		@Override
		public String getTitle() {
			return RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO);
		}
	};

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.paginaVisivel();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
	}

	public void dialogoVisivel() {
		toolbar.dialogoVisivel();
	}

	@Override
	public String getStringPersistencia() {
		Pagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return RequisicaoFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO_MIN);
			}

			@Override
			public String getTitulo() {
				return RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO);
			}

			@Override
			public String getHint() {
				return RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO);
			}

			@Override
			public Icon getIcone() {
				return Icones.URL;
			}
		};
	}

	private class RequisicaoFichario extends JTabbedPane {
		private static final long serialVersionUID = 1L;

		private void adicionarPagina(Pagina pagina) {
			addTab(pagina.getNome(), pagina);
			int ultimoIndice = getTabCount() - 1;
			setSelectedIndex(ultimoIndice);
		}

		private void excluirPaginas() {
			while (getTabCount() > 0) {
				removeTabAt(0);
			}
		}

		private int getIndiceAtivo() {
			return getSelectedIndex();
		}

		private Pagina getPaginaAtiva() {
			int indice = getSelectedIndex();
			if (indice != -1) {
				return (Pagina) getComponentAt(indice);
			}
			return null;
		}

		private Pagina getPagina(String idPagina) {
			for (int i = 0; i < getTabCount(); i++) {
				Component cmp = getComponentAt(i);
				if (cmp instanceof Pagina) {
					Pagina p = (Pagina) cmp;
					if (p.getNome().equals(idPagina)) {
						return p;
					}
				}
			}
			return null;
		}

		private int getIndicePagina(Pagina pagina) {
			for (int i = 0; i < getTabCount(); i++) {
				Component cmp = getComponentAt(i);
				if (cmp instanceof Pagina) {
					Pagina p = (Pagina) cmp;
					if (p == pagina) {
						return i;
					}
				}
			}
			return -1;
		}

		private void setConteudo(String conteudo, String idPagina) {
			Pagina pagina = getPagina(idPagina);
			if (pagina != null) {
				if (!Util.estaVazio(conteudo)) {
					pagina.areaParametros.setText(conteudo);
				}
				setSelectedIndex(getIndicePagina(pagina));
			}
		}
	}

	private class Pagina extends Panel {
		private static final long serialVersionUID = 1L;
		private final ToolbarParametro toolbarParametro = new ToolbarParametro();
		private final ToolbarResultado toolbarResultado = new ToolbarResultado();
		private final JTextPane areaParametros = new JTextPane();
		private final JTextPane areaResultados = new JTextPane();
		private final TabbedPane tabbedPane = new TabbedPane();
		private final Label labelImagem = new Label();
		private ScrollPane scrollPaneArea;
		private final File file;

		private Pagina(File file) {
			this.file = file;
			montarLayout();
			abrir();
		}

		private void montarLayout() {
			JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanelParametro(), criarPanelResultado());
			split.setDividerLocation(Constantes.SIZE.height / 2);
			add(BorderLayout.CENTER, split);
		}

		private Panel criarPanelParametro() {
			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, toolbarParametro);
			Panel panelArea = new Panel();
			panelArea.add(BorderLayout.CENTER, areaParametros);
			scrollPaneArea = new ScrollPane(panelArea);
			panel.add(BorderLayout.CENTER, scrollPaneArea);
			return panel;
		}

		private int getValueScrollPane() {
			return scrollPaneArea.getVerticalScrollBar().getValue();
		}

		private void setValueScrollPane(int value) {
			SwingUtilities.invokeLater(() -> scrollPaneArea.getVerticalScrollBar().setValue(value));
		}

		private Panel criarPanelResultado() {
			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, toolbarResultado);
			Panel panelArea = new Panel();
			panelArea.add(BorderLayout.CENTER, areaResultados);
			tabbedPane.addTab("label.texto", Icones.TEXTO, new ScrollPane(panelArea));
			tabbedPane.addTab("label.imagem", Icones.ICON, new ScrollPane(labelImagem));
			panel.add(BorderLayout.CENTER, tabbedPane);
			return panel;
		}

		private class ToolbarParametro extends BarraButton {
			private static final long serialVersionUID = 1L;
			private Action vAccessTokenAcao = actionMenu("label.atualizar_access_token_var");

			private ToolbarParametro() {
				super.ini(null, LIMPAR, BAIXAR, COPIAR, COLAR);
				buttonColar.addSeparator();
				buttonColar.addItem(vAccessTokenAcao);
				vAccessTokenAcao.setActionListener(e -> atualizarVar());
			}

			private void atualizarVar() {
				String string = Util.getContentTransfered();
				if (!Util.estaVazio(string)) {
					setAccesToken(string);
				}
			}

			@Override
			protected void limpar() {
				areaParametros.setText(Constantes.VAZIO);
			}

			@Override
			protected void baixar() {
				abrir();
			}

			@Override
			protected void copiar() {
				String string = Util.getString(areaParametros);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				areaParametros.requestFocus();
			}

			@Override
			protected void colar(boolean numeros, boolean letras) {
				Util.getContentTransfered(areaParametros, numeros, letras);
			}
		}

		private class ToolbarResultado extends BarraButton {
			private static final long serialVersionUID = 1L;

			private ToolbarResultado() {
				super.ini(null, LIMPAR, COPIAR, COLAR);
			}

			@Override
			protected void limpar() {
				areaResultados.setText(Constantes.VAZIO);
			}

			@Override
			protected void copiar() {
				String string = Util.getString(areaResultados);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				areaResultados.requestFocus();
			}

			@Override
			protected void colar(boolean numeros, boolean letras) {
				Util.getContentTransfered(areaResultados, numeros, letras);
			}
		}

		private String getConteudo() {
			return areaParametros.getText();
		}

		private String getNome() {
			return file.getName();
		}

		private void abrir() {
			areaParametros.setText(Constantes.VAZIO);
			if (file.exists()) {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
					StringBuilder sb = new StringBuilder();
					int value = getValueScrollPane();
					String linha = br.readLine();
					while (linha != null) {
						sb.append(linha + Constantes.QL);
						linha = br.readLine();
					}
					areaParametros.setText(sb.toString());
					setValueScrollPane(value);
				} catch (Exception ex) {
					Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
				}
			}
		}

		private void excluir() {
			if (file.exists()) {
				Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
				try {
					Files.delete(path);
				} catch (IOException e) {
					Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, e, RequisicaoContainer.this);
				}
			}
		}

		private void salvar(AtomicBoolean atomic) {
			if (!Util.confirmaSalvarMsg(RequisicaoContainer.this, Constantes.TRES,
					RequisicaoMensagens.getString("msg.confirmar_salvar_ativa"))) {
				return;
			}
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(areaParametros.getText());
				atomic.set(true);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
			}
		}

		private void formatar() {
			if (!Util.estaVazio(areaParametros.getText())) {
				String string = Util.getString(areaParametros);
				areaResultados.setText(Constantes.VAZIO);
				formatar(string);
			}
		}

		private void formatar(String string) {
			try {
				Parser parser = new Parser();
				Tipo json = parser.parse(string);
				StyledDocument styledDoc = areaResultados.getStyledDocument();
				if (styledDoc instanceof AbstractDocument) {
					AbstractDocument doc = (AbstractDocument) styledDoc;
					json.toString(doc, false, 0);
				}
				areaParametros.requestFocus();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
			}
		}

		private void base64() {
			if (!Util.estaVazio(areaParametros.getText())) {
				String string = Util.getString(areaParametros);
				areaResultados.setText(Constantes.VAZIO);
				base64(string);
			}
		}

		private void retornar64() {
			if (!Util.estaVazio(areaParametros.getText())) {
				String string = Util.getString(areaParametros);
				areaResultados.setText(Constantes.VAZIO);
				retornar64(string);
			}
		}

		private void variaveis() {
			StringBuilder sb = new StringBuilder();
			Properties properties = System.getProperties();
			Set<String> chaves = properties.stringPropertyNames();
			for (String chave : chaves) {
				Object valor = properties.get(chave);
				sb.append(chave + "=" + (valor != null ? valor.toString() : "") + Constantes.QL);
			}
			areaResultados.setText(sb.toString());
		}

		private void base64(String string) {
			try {
				areaResultados.setText(Base64Util.criarBase64(string));
				areaParametros.requestFocus();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
			}
		}

		private void retornar64(String string) {
			try {
				areaResultados.setText(Base64Util.retornarBase64(string));
				areaParametros.requestFocus();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
			}
		}

		private void atualizar() {
			if (!Util.estaVazio(areaParametros.getText())) {
				String string = Util.getString(areaParametros);
				areaResultados.setText(Constantes.VAZIO);
				atualizar(string);
			}
		}

		private void atualizar(String string) {
			try {
				Parser parser = new Parser();
				Variavel vAccessToken = VariavelProvedor.getVariavel(RequisicaoConstantes.VAR_ACCESS_TOKEN);
				if (vAccessToken != null) {
					string = substituir(string, vAccessToken);
				}
				Tipo parametros = parser.parse(string);
				byte[] resposta = requisicao(parametros);
				processarResposta(parser, resposta);
				areaParametros.requestFocus();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
			}
		}

		private void processarResposta(Parser parser, byte[] resposta) throws BadLocationException {
			if (resposta.length > 0 && toolbar.chkRespostaJson.isSelected()) {
				tabbedPane.setSelectedIndex(0);
				processarJSON(parser, Util.getString(resposta));
			} else if (resposta.length > 0 && toolbar.chkRespostaImagem.isSelected()) {
				tabbedPane.setSelectedIndex(1);
				labelImagem.setIcon(new ImageIcon(resposta));
			} else {
				tabbedPane.setSelectedIndex(0);
				areaResultados.setText(Util.getString(resposta));
			}
		}

		private void processarJSON(Parser parser, String resposta) throws BadLocationException {
			StyledDocument styledDoc = areaResultados.getStyledDocument();
			Tipo json = parser.parse(resposta);
			if (styledDoc instanceof AbstractDocument) {
				AbstractDocument doc = (AbstractDocument) styledDoc;
				json.toString(doc, false, 0);
			}
			String accessToken = getAccessToken(json);
			setAccesToken(accessToken);
		}

		private byte[] requisicao(Tipo parametros) throws IOException {
			if (parametros instanceof Objeto) {
				Objeto objeto = (Objeto) parametros;
				Tipo tipoUrl = objeto.getValor("url");
				String url = tipoUrl instanceof Texto ? tipoUrl.toString() : null;
				Map<String, String> mapHeader = getMapHeader(objeto);
				String bodyParams = getBodyParams(objeto);
				return requisicao(url, mapHeader, bodyParams);
			}
			return new byte[0];
		}

		private String getBodyParams(Objeto objeto) {
			Tipo tipoBody = objeto.getValor("body");
			String bodyParams = null;
			if (tipoBody instanceof Objeto) {
				Objeto objBody = (Objeto) tipoBody;
				Tipo params = objBody.getValor("parameters");
				bodyParams = params instanceof Texto ? params.toString() : null;
			}
			return bodyParams;
		}

		private Map<String, String> getMapHeader(Objeto objeto) {
			Map<String, String> mapHeader = null;
			Tipo tipoHeader = objeto.getValor("header");
			if (tipoHeader instanceof Objeto) {
				Objeto objHeader = (Objeto) tipoHeader;
				mapHeader = objHeader.getAtributosString();
			}
			return mapHeader;
		}

		private byte[] requisicao(String url, Map<String, String> header, String parametros) throws IOException {
			if (Util.estaVazio(url)) {
				return new byte[0];
			}
			URL url2 = new URL(url);
			URLConnection conn = url2.openConnection();
			String verbo = setRequestPropertyAndGetVerbo(header, conn);
			checarDoOutput(parametros, conn, verbo);
			conn.connect();
			sePost(parametros, conn, verbo);
			return Util.getArrayBytes(conn.getInputStream());
		}

		private void sePost(String parametros, URLConnection conn, String verbo) throws IOException {
			if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
				OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
				osw.write(parametros);
				osw.flush();
			}
		}

		private void checarDoOutput(String parametros, URLConnection conn, String verbo) {
			if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
				conn.setDoOutput(true);
			}
		}

		private String setRequestPropertyAndGetVerbo(Map<String, String> header, URLConnection conn) {
			String verbo = null;
			if (header != null) {
				verbo = header.get("Request-Method");
				for (Map.Entry<String, String> entry : header.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			return verbo;
		}

		private String getAccessToken(Tipo tipo) {
			if (tipo instanceof Objeto) {
				Objeto objeto = (Objeto) tipo;
				Tipo tipoAccessToken = objeto.getValor("access_token");
				return tipoAccessToken instanceof Texto ? tipoAccessToken.toString() : null;
			}
			return null;
		}

		private void setAccesToken(String accessToken) {
			if (!Util.estaVazio(accessToken)) {
				Variavel vAccessToken = VariavelProvedor.getVariavel(RequisicaoConstantes.VAR_ACCESS_TOKEN);
				if (vAccessToken == null) {
					vAccessToken = new Variavel(RequisicaoConstantes.VAR_ACCESS_TOKEN, accessToken);
					VariavelProvedor.adicionar(vAccessToken);
				} else {
					vAccessToken.setValor(accessToken);
				}
				if (toolbar.chkCopiarAccessToken.isSelected()) {
					Util.setContentTransfered(accessToken);
				}
			}
		}

		private String substituir(String instrucao, Variavel v) {
			if (instrucao == null) {
				instrucao = Constantes.VAZIO;
			}
			return instrucao.replaceAll("#" + v.getNome() + "#", v.getValor());
		}
	}
}