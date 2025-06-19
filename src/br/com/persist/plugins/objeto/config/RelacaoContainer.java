package br.com.persist.plugins.objeto.config;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR0;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.HoraUtil;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.componente.TabbedPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoMensagens;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.ObjetoSuperficieUtil;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.macro.MacroProvedor;
import br.com.persist.plugins.objeto.vinculo.Marcador;

public class RelacaoContainer extends Panel {
	private static final String LABEL_SEL_COR_PANEL_SWATCH = "label.sel_cor_panel_swatch";
	private static final long serialVersionUID = 1L;
	private final ObjetoSuperficie objetoSuperficie;
	private final Toolbar toolbar = new Toolbar();
	private final transient Relacao relacao;
	private String ultimoCampoSelecionado;

	public RelacaoContainer(Janela janela, ObjetoSuperficie objetoSuperficie, Relacao relacao)
			throws AssistenciaException {
		this.objetoSuperficie = Objects.requireNonNull(objetoSuperficie);
		this.relacao = Objects.requireNonNull(relacao);
		MacroProvedor.limpar();
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() throws AssistenciaException {
		add(BorderLayout.CENTER, new Fichario());
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
	}

	private class PanelDescricao extends Panel implements ActionListener {
		private final TextEditor textEditor = new TextEditor();
		private TextField txtDeslocXDesc = new TextField();
		private TextField txtDeslocYDesc = new TextField();
		private CheckBox chkDesenharDesc = new CheckBox();
		private static final long serialVersionUID = 1L;
		private final Toolbar toolbar = new Toolbar();

		private PanelDescricao() {
			txtDeslocXDesc.setText(Constantes.VAZIO + relacao.getDeslocamentoXDesc());
			txtDeslocYDesc.setText(Constantes.VAZIO + relacao.getDeslocamentoYDesc());
			chkDesenharDesc.setSelected(relacao.isDesenharDescricao());
			txtDeslocXDesc.addFocusListener(focusListenerInner);
			txtDeslocYDesc.addFocusListener(focusListenerInner);
			chkDesenharDesc.addActionListener(this);
			txtDeslocXDesc.addActionListener(this);
			txtDeslocYDesc.addActionListener(this);
			textEditor.setText(relacao.getDescricao());
			textEditor.addFocusListener(focusListenerDesc);
			textEditor.addKeyListener(keyListenerInner);
			ScrollPane scrollPane = new ScrollPane(textEditor);
			scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
			Panel panelScroll = new Panel();
			panelScroll.add(BorderLayout.CENTER, scrollPane);
			add(BorderLayout.CENTER, new ScrollPane(panelScroll));
			Box container = Box.createVerticalBox();
			container.add(criarLinhaRotulo("label.desloc_x_desc", txtDeslocXDesc));
			container.add(criarLinhaRotulo("label.desloc_y_desc", txtDeslocYDesc));
			container.add(criarLinha("label.desenhar_desc", true, chkDesenharDesc));
			add(BorderLayout.SOUTH, container);
			add(BorderLayout.NORTH, toolbar);
			configDestaqueMacro();
		}

		private void configDestaqueMacro() {
			Marcador.aplicarBordaMacro(txtDeslocXDesc);
			Marcador.aplicarBordaMacro(txtDeslocYDesc);
			Marcador.aplicarBordaMacro(chkDesenharDesc);
		}

		private transient FocusListener focusListenerInner = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				actionPerformed(new ActionEvent(e.getSource(), 0, null));
			}
		};

		@Override
		public void actionPerformed(ActionEvent e) {
			if (txtDeslocXDesc == e.getSource()) {
				relacao.setDeslocamentoXDesc(Util.getInt(txtDeslocXDesc.getText(), relacao.getDeslocamentoXDesc()));
				MacroProvedor.deslocarXIdDescricao(relacao.getDeslocamentoXDesc());
			} else if (txtDeslocYDesc == e.getSource()) {
				relacao.setDeslocamentoYDesc(Util.getInt(txtDeslocYDesc.getText(), relacao.getDeslocamentoYDesc()));
				MacroProvedor.deslocarYIdDescricao(relacao.getDeslocamentoYDesc());
			} else if (chkDesenharDesc == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				relacao.setDesenharDescricao(chk.isSelected());
				MacroProvedor.desenharIdDescricao(chk.isSelected());
			}
			objetoSuperficie.repaint();
		}

		private transient FocusListener focusListenerDesc = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				keyListenerInner.keyReleased(null);
				objetoSuperficie.repaint();
			}
		};

		private transient KeyListener keyListenerInner = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				relacao.setDescricao(textEditor.getText());
			}
		};

		private Box criarLinhaRotulo(String rotulo, JComponent componente) {
			return criarLinha(ObjetoMensagens.getString(rotulo), false, componente);
		}

		private Box criarLinha(String rotulo, boolean chaveRotulo, JComponent componente) {
			Box box = Box.createHorizontalBox();
			Label label = new Label(rotulo, chaveRotulo);
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			label.setPreferredSize(new Dimension(160, 0));
			label.setMinimumSize(new Dimension(160, 0));
			box.add(label);
			if (componente instanceof CheckBox) {
				box.add(componente);
				box.add(Box.createHorizontalGlue());
			} else {
				box.add(componente);
			}
			return box;
		}

		private class Toolbar extends BarraButton {
			private Action diferencaAction = acaoMenu("label.diferenca_em_horas", null);
			private Action reiniciarAction = acaoMenu("label.reiniciar_horas", null);
			private Action somarAction = acaoMenu("label.somar_em_horas", null);
			private static final String CHAVE_ERRO = "msg.padrao_valor_invalido";
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(new Nil(), COPIAR, COLAR);
				add(diferencaAction);
				add(somarAction);
				add(reiniciarAction);
				diferencaAction.setActionListener(e -> diferenca());
				reiniciarAction.setActionListener(e -> reiniciar());
				somarAction.setActionListener(e -> somar());
			}

			private void reiniciar() {
				try {
					relacao.reiniciarHoras(false, objetoSuperficie);
					diferenca();
				} catch (AssistenciaException ex) {
					Util.mensagem(RelacaoContainer.this, ex.getMessage());
				}
			}

			private void diferenca() {
				int destino = 0;
				int origem = 0;
				try {
					origem = HoraUtil.getSegundos(relacao.getOrigem().getId());
				} catch (Exception e) {
					Util.mensagem(RelacaoContainer.this, ObjetoMensagens.getString(CHAVE_ERRO, "ORIGEM"));
					return;
				}
				try {
					destino = HoraUtil.getSegundos(relacao.getDestino().getId());
				} catch (Exception e) {
					Util.mensagem(RelacaoContainer.this, ObjetoMensagens.getString(CHAVE_ERRO, "DESTINO"));
					return;
				}
				int diff = HoraUtil.getDiff(origem, destino);
				textEditor.setText(HoraUtil.formatar(diff));
				focusListenerDesc.focusLost(null);
				chkDesenharDesc.setSelected(true);
				actionPerformed(new ActionEvent(chkDesenharDesc, 0, null));
			}

			private void somar() {
				int destino = 0;
				int origem = 0;
				try {
					origem = HoraUtil.getSegundos(relacao.getOrigem().getId());
				} catch (Exception e) {
					Util.mensagem(RelacaoContainer.this, ObjetoMensagens.getString(CHAVE_ERRO, "ORIGEM"));
					return;
				}
				try {
					destino = HoraUtil.getSegundos(relacao.getDestino().getId());
				} catch (Exception e) {
					Util.mensagem(RelacaoContainer.this, ObjetoMensagens.getString(CHAVE_ERRO, "DESTINO"));
					return;
				}
				textEditor.setText(HoraUtil.formatar(origem + destino));
				focusListenerDesc.focusLost(null);
				chkDesenharDesc.setSelected(true);
				actionPerformed(new ActionEvent(chkDesenharDesc, 0, null));
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
			}
		}

		Action acaoMenu(String chave, Icon icon) {
			return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
		}
	}

	private class PanelCorFonte extends Panel implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final Toolbar toolbar = new Toolbar();
		private final JColorChooser colorChooser;

		private PanelCorFonte() {
			colorChooser = new JColorChooser(relacao.getCorFonte());
			colorChooser.getSelectionModel().addChangeListener(this);
			Marcador.aplicarBordaMacro(colorChooser);
			add(BorderLayout.CENTER, colorChooser);
			add(BorderLayout.NORTH, toolbar);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			relacao.setCorFonte(colorChooser.getColor());
			MacroProvedor.corFonte(relacao.getCorFonte());
			objetoSuperficie.repaint();
		}

		private class Toolbar extends BarraButton {
			private Action selCorPanelSwatch = acaoIcon(LABEL_SEL_COR_PANEL_SWATCH, Icones.SUCESSO);
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(new Nil(), COPIAR, COLAR0, APLICAR);
				aplicarAcao.text(ObjetoMensagens.getString("label.reaplicar_macro"));
				addButton(selCorPanelSwatch);
				selCorPanelSwatch.setActionListener(e -> selectCorPanelSwatch());
			}

			private void selectCorPanelSwatch() {
				if (relacao.getCorFonte() != null) {
					AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
					if (panels != null && panels.length > 0) {
						processar(panels[0], relacao.getCorFonte());
					}
				}
			}

			@Override
			protected void copiar() {
				Preferencias.setCorFonteCopiado(colorChooser.getColor());
				copiarMensagem(".");
			}

			@Override
			protected void colar0() {
				relacao.setCorFonte(Preferencias.getCorFonteCopiado());
				MacroProvedor.corFonte(relacao.getCorFonte());
				colorChooser.setColor(relacao.getCorFonte());
				objetoSuperficie.repaint();
			}

			@Override
			protected void aplicar() {
				stateChanged(null);
			}
		}
	}

	private void processar(AbstractColorChooserPanel panel, Color cor) {
		if (panel.getDisplayName().contains("Swatch")) {
			ItemCorUtil util = new ItemCorUtil();
			ItemCor itemCor = util.getItem(cor);
			processar(panel, itemCor);
		}
	}

	private void processar(AbstractColorChooserPanel panel, ItemCor itemCor) {
		if (itemCor != null) {
			Robot robot = null;
			try {
				robot = new Robot();
			} catch (Exception ex) {
				Util.mensagem(RelacaoContainer.this, ex.getMessage());
				return;
			}
			selecionarCor(robot, panel, itemCor);
		}
	}

	private void selecionarCor(Robot robot, AbstractColorChooserPanel panel, ItemCor itemCor) {
		panel.requestFocusInWindow();
		keyPress(robot, KeyEvent.VK_TAB, 1);
		keyPress(robot, KeyEvent.VK_DOWN, itemCor.linha);
		keyPress(robot, KeyEvent.VK_RIGHT, itemCor.coluna);
		keyPress(robot, KeyEvent.VK_SPACE, 1);
	}

	private void keyPress(Robot robot, int vk, int total) {
		for (int i = 0; i < total; i++) {
			robot.keyPress(vk);
			robot.keyRelease(vk);
			robot.delay(100);
		}
	}

	private class PanelCorFundo extends Panel implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final Toolbar toolbar = new Toolbar();
		private final JColorChooser colorChooser;

		private PanelCorFundo() {
			colorChooser = new JColorChooser(relacao.getCor());
			colorChooser.getSelectionModel().addChangeListener(this);
			Marcador.aplicarBordaMacro(colorChooser);
			add(BorderLayout.CENTER, colorChooser);
			add(BorderLayout.NORTH, toolbar);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			relacao.setCor(colorChooser.getColor());
			MacroProvedor.corFundo(relacao.getCor());
			objetoSuperficie.repaint();
		}

		private class Toolbar extends BarraButton {
			private Action selCorPanelSwatch = acaoIcon(LABEL_SEL_COR_PANEL_SWATCH, Icones.SUCESSO);
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(new Nil(), COPIAR, COLAR0, APLICAR);
				aplicarAcao.text(ObjetoMensagens.getString("label.reaplicar_macro"));
				addButton(selCorPanelSwatch);
				selCorPanelSwatch.setActionListener(e -> selectCorPanelSwatch());
			}

			private void selectCorPanelSwatch() {
				if (relacao.getCor() != null) {
					AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
					if (panels != null && panels.length > 0) {
						processar(panels[0], relacao.getCor());
					}
				}
			}

			@Override
			protected void copiar() {
				Preferencias.setCorCopiado(colorChooser.getColor());
				copiarMensagem(".");
			}

			@Override
			protected void colar0() {
				relacao.setCor(Preferencias.getCorCopiado());
				MacroProvedor.corFundo(relacao.getCor());
				colorChooser.setColor(relacao.getCor());
				objetoSuperficie.repaint();
			}

			@Override
			protected void aplicar() {
				stateChanged(null);
			}
		}
	}

	private class PanelGeral extends Panel implements ActionListener {
		private Button btnParaFrente = new Button("label.para_frente");
		private CheckBox chkQuebrado = new CheckBox("label.quebrado");
		private static final long serialVersionUID = 1L;

		private PanelGeral() throws AssistenciaException {
			btnParaFrente.addActionListener(this);
			chkQuebrado.addActionListener(this);
			add(BorderLayout.NORTH, new PanelCenter(chkQuebrado, btnParaFrente));
			chkQuebrado.setSelected(relacao.isQuebrado());
			add(BorderLayout.CENTER, new ScrollPane(new PanelLados()));
			chkQuebrado.setMargin(new Insets(5, 10, 5, 5));
			Marcador.aplicarBordaMacro(chkQuebrado);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (chkQuebrado == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				relacao.setQuebrado(chk.isSelected());
				MacroProvedor.linhaQuebrada(chk.isSelected());
			} else if (btnParaFrente == e.getSource()) {
				ObjetoSuperficieUtil.paraFrente(objetoSuperficie, relacao);
			}
			objetoSuperficie.repaint();
		}
	}

	private class PanelLados extends Panel {
		private static final long serialVersionUID = 1L;

		private PanelLados() throws AssistenciaException {
			super(new GridLayout(1, 2));
			add(new PanelLado(true));
			add(new PanelLado(false));
		}
	}

	private class PanelLado extends Panel implements ActionListener {
		private final CheckBox chkPonto = new CheckBox("label.ponto");
		private static final long serialVersionUID = 1L;
		private TextField txtChave = new TextField(30);
		private final boolean origem;

		private PanelLado(boolean origem) throws AssistenciaException {
			super(new GridLayout(6, 1));
			this.origem = origem;
			chkPonto.setSelected(origem ? relacao.isPontoOrigem() : relacao.isPontoDestino());
			txtChave.setText(origem ? relacao.getChaveOrigem() : relacao.getChaveDestino());
			chkPonto.addActionListener(e -> processarPonto());
			txtChave.addFocusListener(focusListenerInner);
			txtChave.addActionListener(this);
			Label label = new Label();
			label.setText(origem ? relacao.getOrigem().getId() : relacao.getDestino().getId());
			add(origem ? new PanelTitulo("label.origem") : new PanelTitulo("label.destino"));
			add(new JSeparator());
			add(new PanelCenter(label));
			add(new PanelCenter(new PanelObjeto(origem ? relacao.getOrigem() : relacao.getDestino())));
			add(new PanelCenter(chkPonto));
			add(new PanelCenter(new PanelChave(txtChave, origem ? relacao.getOrigem() : relacao.getDestino())));
			Marcador.aplicarBordaMacro(chkPonto);
		}

		private transient FocusListener focusListenerInner = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				actionPerformed(new ActionEvent(e.getSource(), 0, null));
			}
		};

		@Override
		public void actionPerformed(ActionEvent e) {
			if (txtChave == e.getSource()) {
				if (origem) {
					relacao.setChaveOrigem(txtChave.getText());
				} else {
					relacao.setChaveDestino(txtChave.getText());
				}
			}
		}

		private void processarPonto() {
			if (origem) {
				MacroProvedor.pontoOrigem(chkPonto.isSelected());
				relacao.setPontoOrigem(chkPonto.isSelected());
			} else {
				MacroProvedor.pontoDestino(chkPonto.isSelected());
				relacao.setPontoDestino(chkPonto.isSelected());
			}
			objetoSuperficie.repaint();
		}
	}

	private class PanelTitulo extends Panel {
		private static final long serialVersionUID = 1L;

		private PanelTitulo(String chave) {
			setLayout(new GridBagLayout());
			add(new Label(chave));
		}
	}

	private class PanelChave extends Panel {
		private final Action campos = Action.actionIcon("label.campos", Icones.CAMPOS);
		private static final long serialVersionUID = 1L;
		private final transient Objeto objeto;
		private final TextField textField;

		private PanelChave(TextField textField, Objeto objeto) {
			add(BorderLayout.WEST, new Label("label.pk_fk"));
			add(BorderLayout.EAST, new Button(campos));
			add(BorderLayout.CENTER, textField);
			this.textField = textField;
			this.objeto = objeto;
			campos.setActionListener(e -> exibirCampos());
		}

		private void exibirCampos() {
			Coletor coletor = new Coletor();
			objetoSuperficie.selecionarCampo(objeto, coletor, RelacaoContainer.this, ultimoCampoSelecionado);
			if (coletor.size() == 1) {
				String string = coletor.get(0);
				ultimoCampoSelecionado = string;
				textField.setText(string);
				textField.postActionEvent();
			}
		}
	}

	private class PanelObjeto extends Panel {
		private static final long serialVersionUID = 1L;
		private final transient Objeto objeto;

		private PanelObjeto(Objeto objeto) throws AssistenciaException {
			super(null);
			final int lado = Objeto.DIAMETRO + 10;
			this.objeto = new Objeto(5, 5, objeto.getCor(), objeto.getIcone());
			setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(lado, lado));
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			Stroke stroke = g2.getStroke();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			objeto.desenhar(this, g2, stroke);
		}
	}

	Action acaoIcon(String chave, Icon icon) {
		return Action.acaoIcon(ObjetoMensagens.getString(chave), icon);
	}

	private class Fichario extends TabbedPane {
		private static final long serialVersionUID = 1L;

		private Fichario() throws AssistenciaException {
			addTab("label.geral", new PanelGeral());
			addTab("label.descricao", new PanelDescricao());
			addTab("label.cor_fundo", new PanelCorFundo());
			addTab("label.cor_fonte", new PanelCorFonte());
		}
	}
}