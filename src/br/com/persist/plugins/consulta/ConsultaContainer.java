package br.com.persist.plugins.consulta;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.BACKUP;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.CLONAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.TransferidorTabular;
import br.com.persist.assistencia.Util;
import br.com.persist.assistencia.VazioModelo;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoEvento;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.persistencia.MemoriaModelo;
import br.com.persist.plugins.persistencia.Persistencia;

public class ConsultaContainer extends AbstratoContainer {
	private final transient ConsultaCor consultaCor = new ConsultaCor();
	private final ToolbarTabela toolbarTabela = new ToolbarTabela();
	private final JTable tabela = new JTable(new VazioModelo());
	private final TextEditor textArea = new TextEditor();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private ConsultaFormulario consultaFormulario;
	private final Label labelStatus = new Label();
	private final JComboBox<Conexao> comboConexao;
	private ConsultaDialogo consultaDialogo;
	private final File fileParent;
	private final File file;
	private File backup;

	public ConsultaContainer(Janela janela, Formulario formulario, Conexao conexao, String conteudo) {
		super(formulario);
		file = new File(ConsultaConstantes.CONSULTAS + Constantes.SEPARADOR + ConsultaConstantes.CONSULTAS);
		textArea.setText(conteudo == null ? Constantes.VAZIO : conteudo);
		comboConexao = ConexaoProvedor.criarComboConexao(conexao);
		fileParent = new File(ConsultaConstantes.CONSULTAS);
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.ini(janela);
		montarLayout();
		configurar();
		abrir(conteudo);
	}

	public ConsultaDialogo getConsultaDialogo() {
		return consultaDialogo;
	}

	public void setConsultaDialogo(ConsultaDialogo consultaDialogo) {
		this.consultaDialogo = consultaDialogo;
		if (consultaDialogo != null) {
			consultaFormulario = null;
		}
	}

	public ConsultaFormulario getConsultaFormulario() {
		return consultaFormulario;
	}

	public void setConsultaFormulario(ConsultaFormulario consultaFormulario) {
		this.consultaFormulario = consultaFormulario;
		if (consultaFormulario != null) {
			consultaDialogo = null;
		}
	}

	private void montarLayout() {
		ScrollPane scrollPane = new ScrollPane(textArea);
		scrollPane.setRowHeaderView(new TextEditorLine(textArea));
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, criarPanelTabela());
		split.setDividerLocation(Constantes.SIZE.height / 2);
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, split);
		add(BorderLayout.SOUTH, labelStatus);
		labelStatus.setForeground(Color.BLUE);
	}

	private Panel criarPanelTabela() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbarTabela);
		panel.add(BorderLayout.CENTER, new ScrollPane(tabela));
		return panel;
	}

	private class ToolbarTabela extends BarraButton {
		private ButtonCopiar buttonCopiar = new ButtonCopiar();
		private static final long serialVersionUID = 1L;

		private ToolbarTabela() {
			super.ini(new Nil());
			add(buttonCopiar);
		}

		private class ButtonCopiar extends ButtonPopup {
			private Action umaColunaSemAcao = actionMenu("label.uma_coluna_sem_aspas");
			private Action umaColunaComAcao = actionMenu("label.uma_coluna_com_aspas");
			private Action transferidorAcao = actionMenu("label.transferidor");
			private Action tabularAcao = actionMenu("label.tabular");
			private Action htmlAcao = actionMenu("label.html");
			private Action pipeAcao = actionMenu("label.pipe");
			private static final long serialVersionUID = 1L;

			private ButtonCopiar() {
				super("label.copiar_tabela", Icones.TABLE2);
				addMenuItem(pipeAcao);
				addMenuItem(true, htmlAcao);
				addMenuItem(true, tabularAcao);
				addMenuItem(true, transferidorAcao);
				addMenuItem(true, umaColunaSemAcao);
				addMenuItem(umaColunaComAcao);
				umaColunaSemAcao.setActionListener(e -> umaColuna(false));
				umaColunaComAcao.setActionListener(e -> umaColuna(true));
				transferidorAcao.setActionListener(e -> processar(0));
				tabularAcao.setActionListener(e -> processar(1));
				htmlAcao.setActionListener(e -> processar(2));
				pipeAcao.setActionListener(e -> processar(3));
			}

			private void umaColuna(boolean comAspas) {
				String titulo = comAspas ? Mensagens.getString("label.uma_coluna_com_aspas")
						: Mensagens.getString("label.uma_coluna_sem_aspas");
				Util.copiarColunaUnicaString(titulo, tabela, comAspas, null);
			}

			private void processar(int tipo) {
				List<Integer> indices = Util.getIndicesLinha(tabela);
				TransferidorTabular transferidor = Util.criarTransferidorTabular(tabela, null, indices);
				if (transferidor != null) {
					if (tipo == 0) {
						Util.setTransfered(transferidor);
					} else if (tipo == 1) {
						Util.setContentTransfered(transferidor.getTabular());
					} else if (tipo == 2) {
						Util.setContentTransfered(transferidor.getHtml());
					} else if (tipo == 3) {
						Util.setContentTransfered(transferidor.getPipe());
					}
				}
			}
		}
	}

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.getAtualizarAcao());
		textArea.addKeyListener(keyListenerInner);
	}

	private transient KeyListener keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			consultaCor.processar(textArea.getStyledDocument());
		}
	};

	public String getConteudo() {
		return textArea.getText();
	}

	private void abrir(String conteudo) {
		if (!Util.isEmpty(conteudo)) {
			textArea.setText(conteudo);
			consultaCor.processar(textArea.getStyledDocument());
			return;
		}
		abrirArquivo(file);
		backup = null;
	}

	private void abrirArquivo(File file) {
		toolbar.limparNomeBackup();
		textArea.limpar();
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					textArea.append(linha + Constantes.QL);
					linha = br.readLine();
				}
				consultaCor.processar(textArea.getStyledDocument());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(ConsultaConstantes.PAINEL_SELECT, ex, ConsultaContainer.this);
			}
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		checarSelecionarConexao(args);
	}

	private void checarSelecionarConexao(Map<String, Object> args) {
		Conexao conexao = (Conexao) args.get(ConexaoEvento.SELECIONAR_CONEXAO);
		if (conexao != null) {
			comboConexao.setSelectedItem(conexao);
		}
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action colarApartirNcaractAcao = acaoMenu("label.colar_apartir_n_caracteres");
		private Action colarSemAspasAcao = acaoMenu("label.colar_sem_aspas");
		private final CheckBox chkPesquisaLocal = new CheckBox(true);
		private final TextField txtPesquisa = new TextField(35);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR, BACKUP, ATUALIZAR);
			add(true, comboConexao);
			add(txtPesquisa);
			add(chkPesquisaLocal);
			add(label);
			buttonColar.addSeparator();
			buttonColar.addItem(colarApartirNcaractAcao);
			buttonColar.addItem(colarSemAspasAcao);
			colarApartirNcaractAcao.setActionListener(e -> colarApartirNCaract());
			colarSemAspasAcao.setActionListener(e -> colarSemAspas());
			chkPesquisaLocal.setToolTipText(Mensagens.getString("label.pesquisa_local"));
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
		}

		Action acaoMenu(String chave) {
			return Action.acaoMenu(ConsultaMensagens.getString(chave), null);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(ConsultaContainer.this)) {
				ConsultaFormulario.criar(formulario, ConsultaContainer.this);
			} else if (consultaDialogo != null) {
				consultaDialogo.excluirContainer();
				ConsultaFormulario.criar(formulario, ConsultaContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (consultaFormulario != null) {
				consultaFormulario.excluirContainer();
				formulario.adicionarPagina(ConsultaContainer.this);
			} else if (consultaDialogo != null) {
				consultaDialogo.excluirContainer();
				formulario.adicionarPagina(ConsultaContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (consultaDialogo != null) {
				consultaDialogo.excluirContainer();
			}
			ConsultaFormulario.criar(formulario, (Conexao) comboConexao.getSelectedItem(), getConteudo());
		}

		@Override
		protected void abrirEmFormulario() {
			if (consultaDialogo != null) {
				consultaDialogo.excluirContainer();
			}
			ConsultaFormulario.criar(formulario, null, null);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		protected void baixar() {
			abrir(null);
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(ConsultaContainer.this, Constantes.TRES)) {
				salvarArquivo(backup != null ? backup : file);
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(ConsultaConstantes.PAINEL_SELECT, ex, ConsultaContainer.this);
			}
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textArea, numeros, letras);
			consultaCor.processar(textArea.getStyledDocument());
		}

		@Override
		protected void criarBackup() {
			if (Util.confirmar(ConsultaContainer.this, "label.confirma_criar_backup")) {
				String nome = Util.gerarNomeBackup(fileParent, ConsultaConstantes.CONSULTAS);
				salvarArquivo(new File(fileParent, nome));
			}
		}

		@Override
		protected void abrirBackup() {
			List<String> arquivos = Util.listarNomeBackup(fileParent, ConsultaConstantes.CONSULTAS);
			if (arquivos.isEmpty()) {
				Util.mensagem(ConsultaContainer.this, Mensagens.getString("msg.sem_arq_backup"));
				return;
			}
			Coletor coletor = new Coletor();
			SetLista.view(ConsultaConstantes.CONSULTAS, arquivos, coletor, ConsultaContainer.this,
					new SetLista.Config(true, true));
			if (coletor.size() == 1) {
				File arq = new File(fileParent, coletor.get(0));
				abrirArquivo(arq);
				backup = arq;
				setNomeBackup(coletor.get(0));
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPesquisaLocal.isSelected()) {
					selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
					selecao.selecionar(label);
					return;
				}
				List<String> arquivos = Util.listarNomeBackup(fileParent, ConsultaConstantes.CONSULTAS);
				StringBuilder sb = new StringBuilder();
				for (String arquivo : arquivos) {
					String resultado = Util.pesquisar(new File(fileParent, arquivo), txtPesquisa.getText());
					if (!Util.isEmpty(resultado)) {
						if (sb.length() > 0) {
							sb.append(Constantes.QL);
						}
						sb.append(arquivo + Constantes.QL);
						sb.append(resultado);
					}
				}
				textArea.setText(sb.toString());
			} else {
				label.limpar();
			}
		}

		private void colarApartirNCaract() {
			String string = Util.getContentTransfered();
			if (Util.isEmpty(string)) {
				return;
			}
			Object resp = Util.showInputDialog(ConsultaContainer.this, Mensagens.getString("label.atencao"),
					ConsultaMensagens.getString("label.apartir_n_caracteres"), "1");
			if (resp != null && !Util.isEmpty(resp.toString())) {
				try {
					int total = Util.getInt(resp.toString().trim(), 1);
					string = getStringApartir(string, total);
					Util.insertStringArea(textArea, string);
					consultaCor.processar(textArea.getStyledDocument());
				} catch (Exception ex) {
					Util.stackTraceAndMessage(ConsultaConstantes.PAINEL_SELECT, ex, this);
				}
			}
		}

		private String getStringApartir(String string, int num) {
			String[] strings = string.split(Constantes.QL);
			StringBuilder sb = new StringBuilder();
			for (String s : strings) {
				s = getApartir(s, num);
				sb.append(s + Constantes.QL);
			}
			return sb.toString();
		}

		private String getApartir(String string, int num) {
			if (num > string.length()) {
				num = string.length() + 1;
			}
			if (num < 1) {
				num = 1;
			}
			return string.substring(num - 1);
		}

		private void colarSemAspas() {
			String string = Util.getContentTransfered();
			if (Util.isEmpty(string)) {
				return;
			}
			string = getString(string);
			Util.insertStringArea(textArea, string);
			consultaCor.processar(textArea.getStyledDocument());
		}

		private String getString(String string) {
			String[] strings = string.split(Constantes.QL);
			StringBuilder sb = new StringBuilder();
			for (String s : strings) {
				s = get(s);
				sb.append(s + Constantes.QL);
			}
			return sb.toString();
		}

		private String get(String string) {
			int pos = string.indexOf('"');
			if (pos != -1) {
				string = string.substring(pos + 1);
			}
			pos = string.lastIndexOf('"');
			if (pos != -1) {
				string = string.substring(0, pos);
			}
			return string;
		}

		@Override
		protected void atualizar() {
			if (!Util.isEmpty(textArea.getText())) {
				Conexao conexao = (Conexao) comboConexao.getSelectedItem();
				if (conexao != null) {
					String consulta = Util.getString(textArea);
					atualizar(conexao, consulta);
				} else {
					Util.mensagem(ConsultaContainer.this, Constantes.CONEXAO_NULA);
				}
			}
		}

		private void atualizar(Conexao conexao, String consulta) {
			try {
				Connection conn = ConexaoProvedor.getConnection(conexao);
				MemoriaModelo modelo = Persistencia.criarMemoriaModelo(conn, consulta);
				tabela.setModel(modelo);
				Util.ajustar(tabela, getGraphics());
				labelStatus.setText(
						"[" + Util.getDataHora() + "] TOTAL DE REGISTROS SELECIONADOS [" + modelo.getRowCount() + "]");
				textArea.requestFocus();
			} catch (Exception ex) {
				labelStatus.limpar();
				Util.stackTraceAndMessage(ConsultaConstantes.PAINEL_SELECT, ex, this);
			}
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.dialogOpenedHandler(dialog);
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return ConsultaFabrica.class;
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
				return ConsultaMensagens.getString(ConsultaConstantes.LABEL_CONSULTA_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_CONSULTA);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_CONSULTA);
			}

			@Override
			public Icon getIcone() {
				return Icones.TABELA;
			}
		};
	}
}