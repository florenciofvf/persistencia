package br.com.persist.plugins.consulta;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
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
import br.com.persist.assistencia.TransferidorTabular;
import br.com.persist.assistencia.Util;
import br.com.persist.assistencia.VazioModelo;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextArea;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoEvento;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.persistencia.MemoriaModelo;
import br.com.persist.plugins.persistencia.Persistencia;

public class ConsultaContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final ToolbarTabela toolbarTabela = new ToolbarTabela();
	private final JTable tabela = new JTable(new VazioModelo());
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private ConsultaFormulario consultaFormulario;
	private final Label labelStatus = new Label();
	private final JComboBox<Conexao> comboConexao;
	private ConsultaDialogo consultaDialogo;
	private final File file;

	public ConsultaContainer(Janela janela, Formulario formulario, Conexao conexao, String conteudo) {
		super(formulario);
		file = new File(Constantes.CONSULTAS + Constantes.SEPARADOR + Constantes.CONSULTAS);
		textArea.setText(conteudo == null ? Constantes.VAZIO : conteudo);
		comboConexao = ConexaoProvedor.criarComboConexao(conexao);
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
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textArea, criarPanelTabela());
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
		private static final long serialVersionUID = 1L;
		private ButtonCopiar buttonCopiar = new ButtonCopiar();

		private ToolbarTabela() {
			super.ini(null);
			add(buttonCopiar);
		}

		private class ButtonCopiar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action umaColunaSemAcao = Action.actionMenu("label.uma_coluna_sem_aspas", null);
			private Action umaColunaComAcao = Action.actionMenu("label.uma_coluna_com_aspas", null);
			private Action transferidorAcao = Action.actionMenu("label.transferidor", null);
			private Action tabularAcao = Action.actionMenu("label.tabular", null);
			private Action htmlAcao = Action.actionMenu("label.html", null);

			private ButtonCopiar() {
				super("label.copiar_tabela", Icones.TABLE2);
				addMenuItem(htmlAcao);
				addMenuItem(true, tabularAcao);
				addMenuItem(true, transferidorAcao);
				addMenuItem(true, umaColunaSemAcao);
				addMenuItem(umaColunaComAcao);
				umaColunaSemAcao.setActionListener(e -> umaColuna(false));
				umaColunaComAcao.setActionListener(e -> umaColuna(true));
				transferidorAcao.setActionListener(e -> processar(0));
				tabularAcao.setActionListener(e -> processar(1));
				htmlAcao.setActionListener(e -> processar(2));
			}

			private void umaColuna(boolean comAspas) {
				List<Integer> indices = Util.getIndicesLinha(tabela);
				String string = Util.copiarColunaUnicaString(tabela, indices, comAspas,
						comAspas ? Mensagens.getString("label.uma_coluna_com_aspas")
								: Mensagens.getString("label.uma_coluna_sem_aspas"));
				Util.setContentTransfered(string);
			}

			private void processar(int tipo) {
				List<Integer> indices = Util.getIndicesLinha(tabela);
				TransferidorTabular transferidor = Util.criarTransferidorTabular(tabela, indices);
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

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.getAtualizarAcao());
	}

	public String getConteudo() {
		return textArea.getText();
	}

	private void abrir(String conteudo) {
		if (!Util.estaVazio(conteudo)) {
			textArea.setText(conteudo);
			return;
		}
		textArea.limpar();
		abrirArquivo();
	}

	private void abrirArquivo() {
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					textArea.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_SELECT, ex, ConsultaContainer.this);
			}
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		checarSelecionarConexao(formulario, args);
	}

	private void checarSelecionarConexao(Formulario formulario, Map<String, Object> args) {
		Conexao conexao = (Conexao) args.get(ConexaoEvento.SELECIONAR_CONEXAO);
		if (conexao != null) {
			comboConexao.setSelectedItem(conexao);
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		protected void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR, ATUALIZAR);
			add(true, comboConexao);
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
				salvarArquivo();
			}
		}

		private void salvarArquivo() {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_SELECT, ex, ConsultaContainer.this);
			}
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar() {
			Util.getContentTransfered(textArea.getTextAreaInner());
		}

		@Override
		protected void atualizar() {
			if (!Util.estaVazio(textArea.getText())) {
				Conexao conexao = (Conexao) comboConexao.getSelectedItem();
				if (conexao != null) {
					String consulta = Util.getString(textArea.getTextAreaInner());
					atualizar(conexao, consulta);
				}
			}
		}

		private void atualizar(Conexao conexao, String consulta) {
			try {
				Connection conn = ConexaoProvedor.getConnection(conexao);
				MemoriaModelo modelo = Persistencia.criarMemoriaModelo(conn, consulta);
				tabela.setModel(modelo);
				Util.ajustar(tabela, getGraphics());
				labelStatus.setText("REGISTROS [" + modelo.getRowCount() + "]");
				textArea.requestFocus();
			} catch (Exception ex) {
				labelStatus.limpar();
				Util.stackTraceAndMessage(Constantes.PAINEL_SELECT, ex, this);
			}
		}
	}

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
				return Mensagens.getString(Constantes.LABEL_CONSULTA_MIN);
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