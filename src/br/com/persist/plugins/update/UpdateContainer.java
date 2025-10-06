package br.com.persist.plugins.update;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
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
import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoEvento;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.consulta.ConsultaCor;
import br.com.persist.plugins.persistencia.Persistencia;

public class UpdateContainer extends AbstratoContainer {
	private final transient ConsultaCor consultaCor = new ConsultaCor();
	private final TextEditor textEditor = new TextEditor();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final Label labelStatus = new Label();
	private final JComboBox<Conexao> comboConexao;
	private UpdateFormulario updateFormulario;
	private UpdateDialogo updateDialogo;
	private final File fileParent;
	private final File file;
	private File backup;

	public UpdateContainer(Janela janela, Formulario formulario, Conexao conexao, String conteudo) {
		super(formulario);
		file = new File(UpdateConstantes.ATUALIZACOES + Constantes.SEPARADOR + UpdateConstantes.ATUALIZACOES);
		textEditor.setText(conteudo == null ? Constantes.VAZIO : conteudo);
		comboConexao = ConexaoProvedor.criarComboConexao(conexao);
		fileParent = new File(UpdateConstantes.ATUALIZACOES);
		toolbar.ini(janela);
		montarLayout();
		configurar();
		abrir(conteudo);
	}

	public UpdateDialogo getUpdateDialogo() {
		return updateDialogo;
	}

	public void setUpdateDialogo(UpdateDialogo updateDialogo) {
		this.updateDialogo = updateDialogo;
		if (updateDialogo != null) {
			updateFormulario = null;
		}
	}

	public UpdateFormulario getUpdateFormulario() {
		return updateFormulario;
	}

	public void setUpdateFormulario(UpdateFormulario updateFormulario) {
		this.updateFormulario = updateFormulario;
		if (updateFormulario != null) {
			updateDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		ScrollPane scrollPane = new ScrollPane(textEditor);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		add(BorderLayout.SOUTH, labelStatus);
		labelStatus.setForeground(Color.BLUE);
		textEditor.setListener(e -> toolbar.focusInputPesquisar());
	}

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.updateAcao);
		textEditor.addKeyListener(keyListenerInner);
	}

	private transient KeyListener keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			consultaCor.processar(textEditor.getStyledDocument());
		}
	};

	public String getConteudo() {
		return textEditor.getText();
	}

	private void abrir(String conteudo) {
		if (!Util.isEmpty(conteudo)) {
			textEditor.setText(conteudo);
			consultaCor.processar(textEditor.getStyledDocument());
			return;
		}
		abrirArquivo(file);
		backup = null;
	}

	private void abrirArquivo(File file) {
		toolbar.limparNomeBackup();
		textEditor.limpar();
		if (file.exists()) {
			try {
				textEditor.setText(ArquivoUtil.getString(file));
				consultaCor.processar(textEditor.getStyledDocument());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(UpdateConstantes.PAINEL_UPDATE, ex, UpdateContainer.this);
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
		private Action colarSemAspasAcao = acaoMenu("label.colar_sem_aspas");
		private final CheckBox chkPesquisaLocal = new CheckBox(true);
		private Action updateAcao = Action.actionIconUpdate();
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR, BACKUP);
			addButton(updateAcao);
			add(true, comboConexao);
			add(txtPesquisa);
			add(chkPesquisaLocal);
			add(label);
			buttonColar.addSeparator();
			buttonColar.addItem(colarSemAspasAcao);
			colarSemAspasAcao.setActionListener(e -> colarSemAspas());
			chkPesquisaLocal.setToolTipText(Mensagens.getString("label.pesquisa_local"));
			updateAcao.setActionListener(e -> atualizar());
			txtPesquisa.addActionListener(this);
		}

		Action acaoMenu(String chave) {
			return Action.acaoMenu(UpdateMensagens.getString(chave), null);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(UpdateContainer.this)) {
				UpdateFormulario.criar(formulario, UpdateContainer.this);
			} else if (updateDialogo != null) {
				updateDialogo.excluirContainer();
				UpdateFormulario.criar(formulario, UpdateContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (updateFormulario != null) {
				updateFormulario.excluirContainer();
				formulario.adicionarPagina(UpdateContainer.this);
			} else if (updateDialogo != null) {
				updateDialogo.excluirContainer();
				formulario.adicionarPagina(UpdateContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (updateDialogo != null) {
				updateDialogo.excluirContainer();
			}
			UpdateFormulario.criar(formulario, (Conexao) comboConexao.getSelectedItem(), getConteudo());
		}

		@Override
		protected void abrirEmFormulario() {
			if (updateDialogo != null) {
				updateDialogo.excluirContainer();
			}
			UpdateFormulario.criar(formulario, null, null);
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
			textEditor.limpar();
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(UpdateContainer.this, Constantes.TRES)) {
				salvarArquivo(backup != null ? backup : file);
			}
		}

		private void salvarArquivo(File file) {
			try {
				ArquivoUtil.salvar(textEditor, file);
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(UpdateConstantes.PAINEL_UPDATE, ex, UpdateContainer.this);
			}
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textEditor);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textEditor.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textEditor, numeros, letras);
			consultaCor.processar(textEditor.getStyledDocument());
		}

		@Override
		protected void criarBackup() {
			if (Util.confirmar(UpdateContainer.this, "label.confirma_criar_backup")) {
				String nome = Util.gerarNomeBackup(fileParent, UpdateConstantes.ATUALIZACOES);
				salvarArquivo(new File(fileParent, nome));
			}
		}

		@Override
		protected void abrirBackup() {
			List<String> arquivos = Util.listarNomeBackup(fileParent, UpdateConstantes.ATUALIZACOES);
			if (arquivos.isEmpty()) {
				Util.mensagem(UpdateContainer.this, Mensagens.getString("msg.sem_arq_backup"));
				return;
			}
			Coletor coletor = new Coletor();
			SetLista.view(UpdateConstantes.ATUALIZACOES, arquivos, coletor, UpdateContainer.this,
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
					selecao = Util.getSelecao(textEditor, selecao, txtPesquisa.getText());
					selecao.selecionar(label);
					return;
				}
				List<String> arquivos = Util.listarNomeBackup(fileParent, UpdateConstantes.ATUALIZACOES);
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
				textEditor.setText(sb.toString());
			} else {
				label.limpar();
			}
		}

		private void colarSemAspas() {
			String string = Util.getContentTransfered();
			if (Util.isEmpty(string)) {
				return;
			}
			string = getString(string);
			Util.insertStringArea(textEditor, string);
			consultaCor.processar(textEditor.getStyledDocument());
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
		public void atualizar() {
			Conexao conexao = (Conexao) comboConexao.getSelectedItem();
			if (conexao == null) {
				Util.mensagem(UpdateContainer.this, Constantes.CONEXAO_NULA);
				return;
			}
			if (Preferencias.isDesconectado()) {
				Util.mensagem(UpdateContainer.this, Constantes.DESCONECTADO);
				return;
			}
			if (!Util.isEmpty(textEditor.getText())) {
				String instrucao = Util.getString(textEditor);
				atualizar(conexao, instrucao);
			}
		}

		private void atualizar(Conexao conexao, String instrucao) {
			try {
				Connection conn = ConexaoProvedor.getConnection(conexao);
				int atualizados = Persistencia.executar(conn, instrucao);
				labelStatus
						.setText("[" + Util.getDataHora() + "] TOTAL DE REGISTROS ATUALIZADOS [" + atualizados + "]");
				textEditor.requestFocus();
			} catch (Exception ex) {
				labelStatus.limpar();
				Util.stackTraceAndMessage(UpdateConstantes.PAINEL_UPDATE, ex, this);
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
		return UpdateFabrica.class;
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
				return UpdateMensagens.getString(UpdateConstantes.LABEL_ATUALIZAR_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_ATUALIZAR);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_ATUALIZAR);
			}

			@Override
			public Icon getIcone() {
				return Icones.UPDATE;
			}
		};
	}
}