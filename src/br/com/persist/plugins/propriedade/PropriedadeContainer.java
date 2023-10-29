package br.com.persist.plugins.propriedade;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.componente.TextPane;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.update.UpdateConstantes;

public class PropriedadeContainer extends AbstratoContainer {
	private final PainelResultado painelResultado = new PainelResultado();
	private PropriedadeFormulario propriedadeFormulario;
	private final TextPane textArea = new TextPane();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private PropriedadeDialogo propriedadeDialogo;
	private final File file;

	public PropriedadeContainer(Janela janela, Formulario formulario) {
		super(formulario);
		file = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR + PropriedadeConstantes.PROPRIEDADES
				+ ".xml");
		toolbar.ini(janela);
		montarLayout();
		abrir();
	}

	public PropriedadeDialogo getPropriedadeDialogo() {
		return propriedadeDialogo;
	}

	public void setPropriedadeDialogo(PropriedadeDialogo propriedadeDialogo) {
		this.propriedadeDialogo = propriedadeDialogo;
		if (propriedadeDialogo != null) {
			propriedadeFormulario = null;
		}
	}

	public PropriedadeFormulario getPropriedadeFormulario() {
		return propriedadeFormulario;
	}

	public void setPropriedadeFormulario(PropriedadeFormulario propriedadeFormulario) {
		this.propriedadeFormulario = propriedadeFormulario;
		if (propriedadeFormulario != null) {
			propriedadeDialogo = null;
		}
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanel(), criarPanelResultado());
		SwingUtilities.invokeLater(() -> split.setDividerLocation(.5));
		add(BorderLayout.CENTER, split);
	}

	private Panel criarPanel() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbar);
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, textArea);
		ScrollPane scrollPane = new ScrollPane(panelArea);
		panel.add(BorderLayout.CENTER, scrollPane);
		return panel;
	}

	private Panel criarPanelResultado() {
		Panel panel = new Panel();
		panel.add(BorderLayout.CENTER, painelResultado);
		return panel;
	}

	private class PainelResultado extends Panel {
		private static final long serialVersionUID = 1L;
		private final ToolbarPesquisa toolbarPesquisa;
		private JTextPane textPane = new JTextPane();

		private PainelResultado() {
			toolbarPesquisa = new ToolbarPesquisa(textPane);
			add(BorderLayout.NORTH, toolbarPesquisa);
			add(BorderLayout.CENTER, new ScrollPane(textPane));
			Action copiar2Action = toolbarPesquisa.addCopiar2();
			copiar2Action.hint(PropriedadeMensagens.getString("label.copiar_tudo"));
			copiar2Action.setActionListener(e -> copiar2());
		}

		private void setText(String string) {
			textPane.setText(string);
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));
		}

		private void processar(Raiz raiz) {
			textPane.setText(Constantes.VAZIO);
			try {
				raiz.processar(null, textPane.getStyledDocument());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		private void copiar2() {
			String string = textPane.getText();
			Util.setContentTransfered(string);
			toolbarPesquisa.copiar2Mensagem(string);
			textPane.setSelectionStart(0);
			textPane.setSelectionEnd(string.length());
			textPane.requestFocus();
		}
	}

	private void abrir() {
		abrirArquivo(file);
	}

	private void abrirArquivo(File file) {
		textArea.limpar();
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					textArea.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private Action gerarAcao = acaoIcon("label.gerar_conteudo", Icones.EXECUTAR);
		private final TextField txtPesquisa = new TextField(35);
		private transient Selecao selecao;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, LIMPAR, BAIXAR, SALVAR);
			addButton(gerarAcao);
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
			gerarAcao.setActionListener(e -> gerar());
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(PropriedadeMensagens.getString(chave), icon);
		}

		private void gerar() {
			String string = textArea.getText();
			if (Util.estaVazio(string)) {
				painelResultado.setText("Editor vazio.");
				return;
			}
			try {
				Raiz raiz = PropriedadeUtil.criarRaiz(string);
				painelResultado.processar(raiz);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(PropriedadeContainer.this)) {
				PropriedadeFormulario.criar(formulario, PropriedadeContainer.this);
			} else if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
				PropriedadeFormulario.criar(formulario, PropriedadeContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (propriedadeFormulario != null) {
				propriedadeFormulario.excluirContainer();
				formulario.adicionarPagina(PropriedadeContainer.this);
			} else if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
				formulario.adicionarPagina(PropriedadeContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
			}
			PropriedadeFormulario.criar(formulario);
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
		protected void limpar() {
			textArea.setText(Constantes.VAZIO);
			selecao = null;
			label.limpar();
		}

		@Override
		public void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		@Override
		public void salvar() {
			if (Util.confirmaSalvar(PropriedadeContainer.this, Constantes.TRES)) {
				salvarArquivo(file);
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(UpdateConstantes.PAINEL_UPDATE, ex, PropriedadeContainer.this);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.estaVazio(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
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
		return PropriedadeFabrica.class;
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
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE_MIN);
			}

			@Override
			public String getTitulo() {
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE);
			}

			@Override
			public String getHint() {
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE);
			}

			@Override
			public Icon getIcone() {
				return Icones.EDIT;
			}
		};
	}
}