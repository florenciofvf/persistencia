package br.com.persist.consulta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Label;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextArea;
import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Fichario.InfoConexao;
import br.com.persist.icone.Icones;
import br.com.persist.objeto.Objeto;
import br.com.persist.persistencia.Persistencia;
import br.com.persist.principal.Formulario;
import br.com.persist.tabela.RegistroModelo;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.tabela.VazioModelo;
import br.com.persist.util.Action;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.TransferidorDados;
import br.com.persist.util.Util;

public class ConsultaContainer extends AbstratoContainer
		implements Fichario.IFicharioSalvar, Fichario.IFicharioConexao {
	private static final long serialVersionUID = 1L;
	private static final File file = new File("consultas/consultas");
	private static final String PAINEL_SELECT = "PAINEL SELECT";
	private final JTable tabela = new JTable(new VazioModelo());
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private ConsultaFormulario consultaFormulario;
	private final JComboBox<Conexao> cmbConexao;
	private Label labelStatus = new Label();

	public ConsultaContainer(IJanela janela, Formulario formulario, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor, boolean abrirArquivo) {
		super(formulario);
		textArea.setText(Util.substituir(instrucao, mapaChaveValor));
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.ini(janela, mapaChaveValor, abrirArquivo);
		montarLayout();
		config();

		if ((mapaChaveValor == null || mapaChaveValor.isEmpty()) && abrirArquivo) {
			abrir();
		}
	}

	public ConsultaFormulario getConsultaFormulario() {
		return consultaFormulario;
	}

	public void setConsultaFormulario(ConsultaFormulario consultaFormulario) {
		this.consultaFormulario = consultaFormulario;
	}

	private void config() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao);
	}

	@Override
	public void selecionarConexao(Conexao conexao) {
		if (conexao != null) {
			cmbConexao.setSelectedItem(conexao);
		}
	}

	@Override
	public InfoConexao getInfoConexao() {
		Conexao conexao = getConexaoPadrao();
		String conexaoAtual = conexao == null ? "null" : conexao.getNome();
		String nomeAba = getFileSalvarAberto().getAbsolutePath();
		return new InfoConexao(conexaoAtual, null, nomeAba);
	}

	public Conexao getConexaoPadrao() {
		return (Conexao) cmbConexao.getSelectedItem();
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textArea, new ScrollPane(tabela));
		split.setDividerLocation(Constantes.SIZE.height / 2);

		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, split);
		add(BorderLayout.SOUTH, labelStatus);
		labelStatus.setForeground(Color.BLUE);
	}

	public String getConteudo() {
		return textArea.getText();
	}

	public void setConteudo(String conteudo) {
		if (!Util.estaVazio(conteudo)) {
			textArea.setText(conteudo);
		}
	}

	private void abrir() {
		textArea.limpar();

		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();

				while (linha != null) {
					textArea.append(linha + Constantes.QL2);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_SELECT, ex, ConsultaContainer.this);
			}
		}
	}

	@Override
	protected void destacarEmFormulario() {
		if (formulario != null) {
			formulario.getFichario().getConsulta().destacarEmFormulario(formulario, this);
		}
	}

	@Override
	protected void clonarEmFormulario() {
		if (formulario != null) {
			formulario.getFichario().getConsulta().clonarEmFormulario(formulario, this);
		}
	}

	@Override
	protected void abrirEmFormulario() {
		ConsultaFormulario.criar(formulario, formulario, getConexaoPadrao(), null);
	}

	@Override
	protected void retornoAoFichario() {
		if (consultaFormulario != null) {
			consultaFormulario.retornoAoFichario();
		}
	}

	@Override
	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconAtualizar();
		private ButtonCopiar copiar = new ButtonCopiar();

		protected void ini(IJanela janela, Map<String, String> mapaChaveValor, boolean abrirArquivo) {
			super.ini(janela, true, (mapaChaveValor == null || mapaChaveValor.isEmpty()) && abrirArquivo);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_CONSULTA);
			configBaixarAcao(e -> abrir());

			addButton(atualizarAcao);
			add(copiar);
			configCopiar1Acao(true);
			add(true, cmbConexao);

			eventos();
		}

		@Override
		protected void copiar1() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiar1Mensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar1() {
			Util.getContentTransfered(textArea.getTextAreaInner());
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		@Override
		protected void salvar() {
			if (!Util.confirmaSalvar(ConsultaContainer.this, Constantes.TRES)) {
				return;
			}

			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_SELECT, ex, ConsultaContainer.this);
			}
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizar());
		}

		private class ButtonCopiar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action transfAcao = Action.actionMenu("label.transferidor", null);
			private Action tabularAcao = Action.actionMenu("label.tabular", null);
			private Action htmlAcao = Action.actionMenu("label.html", null);

			private ButtonCopiar() {
				super("label.copiar_tabela", Icones.COPIA);

				addMenuItem(htmlAcao);
				addMenuItem(true, tabularAcao);
				addMenuItem(true, transfAcao);

				transfAcao.setActionListener(e -> processar(0));
				tabularAcao.setActionListener(e -> processar(1));
				htmlAcao.setActionListener(e -> processar(2));
			}

			private void processar(int tipo) {
				List<Integer> indices = TabelaUtil.getIndices(tabela);
				TransferidorDados transferidor = TabelaUtil.getTransferidorDados(tabela, indices);

				if (transferidor != null) {
					if (tipo == 0) {
						Util.setTransfered(transferidor);
					} else if (tipo == 1) {
						Util.setContentTransfered(transferidor.getTabular());
					} else if (tipo == 2) {
						Util.setContentTransfered(transferidor.getHtml());
					}
				}
			}
		}
	}

	public void atualizar() {
		if (Util.estaVazio(textArea.getText())) {
			return;
		}

		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		String consulta = Util.getString(textArea.getTextAreaInner());

		try {
			Connection conn = Conexao.getConnection(conexao);
			RegistroModelo modeloRegistro = Persistencia.criarModeloRegistro(conn, consulta, new String[0],
					new Objeto(), conexao);
			tabela.setModel(modeloRegistro);
			TabelaUtil.ajustar(tabela, getGraphics());
			labelStatus.setText("REGISTROS [" + modeloRegistro.getRowCount() + "]");
			textArea.requestFocus();
		} catch (Exception ex) {
			labelStatus.limpar();
			Util.stackTraceAndMessage(PAINEL_SELECT, ex, this);
		}
	}
}