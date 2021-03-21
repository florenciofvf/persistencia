package br.com.persist.plugins.objeto.config;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.componente.TabbedPane;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.macro.MacroProvedor;

public class RelacaoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final BarraButton toolbar = new BarraButton();
	private final ObjetoSuperficie objetoSuperficie;
	private final transient Relacao relacao;

	public RelacaoContainer(Janela janela, ObjetoSuperficie objetoSuperficie, Relacao relacao) {
		this.objetoSuperficie = objetoSuperficie;
		this.relacao = relacao;
		MacroProvedor.limpar();
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new Fichario());
		add(BorderLayout.NORTH, toolbar);
	}

	private class PanelDescricao extends Panel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private TextField txtDeslocXDesc = new TextField();
		private TextField txtDeslocYDesc = new TextField();
		private CheckBox chkDesenharDesc = new CheckBox();
		private final TextArea textArea = new TextArea();
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
			textArea.setText(relacao.getDescricao());
			textArea.addFocusListener(focusListenerDesc);
			textArea.addKeyListener(keyListenerInner);
			add(BorderLayout.CENTER, textArea);
			Box container = Box.createVerticalBox();
			container.add(criarLinha("label.desloc_x_desc", txtDeslocXDesc));
			container.add(criarLinha("label.desloc_y_desc", txtDeslocYDesc));
			container.add(criarLinha("label.desenhar_desc", chkDesenharDesc));
			add(BorderLayout.SOUTH, container);
			add(BorderLayout.NORTH, toolbar);
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
				relacao.setDescricao(textArea.getText());
			}
		};

		private Box criarLinha(String chaveRotulo, JComponent componente) {
			Box box = Box.createHorizontalBox();
			Label label = new Label(chaveRotulo);
			label.setHorizontalAlignment(Label.RIGHT);
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
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(null, COPIAR, COLAR);
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
		}
	}

	private class PanelCorFonte extends Panel implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final Toolbar toolbar = new Toolbar();
		private final JColorChooser colorChooser;

		private PanelCorFonte() {
			colorChooser = new JColorChooser(relacao.getCorFonte());
			colorChooser.getSelectionModel().addChangeListener(this);
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
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(null, COPIAR, COLAR, APLICAR);
				aplicarAcao.rotulo("label.reaplicar_macro");
			}

			@Override
			protected void copiar() {
				Preferencias.setCorFonteCopiado(colorChooser.getColor());
				copiarMensagem(".");
			}

			@Override
			protected void colar() {
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

	private class PanelCor extends Panel implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final Toolbar toolbar = new Toolbar();
		private final JColorChooser colorChooser;

		private PanelCor() {
			colorChooser = new JColorChooser(relacao.getCor());
			colorChooser.getSelectionModel().addChangeListener(this);
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
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(null, COPIAR, COLAR, APLICAR);
				aplicarAcao.rotulo("label.reaplicar_macro");
			}

			@Override
			protected void copiar() {
				Preferencias.setCorCopiado(colorChooser.getColor());
				copiarMensagem(".");
			}

			@Override
			protected void colar() {
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
		private static final long serialVersionUID = 1L;
		private CheckBox chkQuebrado = new CheckBox("label.quebrado");

		private PanelGeral() {
			add(new PanelLado(true));
			add(new PanelLado(false));
			chkQuebrado.addActionListener(this);
			add(BorderLayout.NORTH, chkQuebrado);
			chkQuebrado.setSelected(relacao.isQuebrado());
			add(BorderLayout.CENTER, new ScrollPane(new PanelLados()));
			chkQuebrado.setMargin(new Insets(5, 10, 5, 5));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (chkQuebrado == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				relacao.setQuebrado(chk.isSelected());
				MacroProvedor.linhaQuebrada(chk.isSelected());
			}
			objetoSuperficie.repaint();
		}
	}

	private class PanelLados extends Panel {
		private static final long serialVersionUID = 1L;

		private PanelLados() {
			super(new GridLayout(1, 2));
			add(new PanelLado(true));
			add(new PanelLado(false));
		}
	}

	private class PanelLado extends Panel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final CheckBox chkPonto = new CheckBox("label.ponto");
		private TextField txtChave = new TextField(30);
		private final boolean origem;

		private PanelLado(boolean origem) {
			super(new GridLayout(6, 1));
			this.origem = origem;
			chkPonto.setSelected(origem ? relacao.isPontoOrigem() : relacao.isPontoDestino());
			txtChave.setText(origem ? relacao.getChaveOrigem() : relacao.getChaveDestino());
			chkPonto.addActionListener(e -> processarPonto());
			txtChave.addFocusListener(focusListenerInner);
			txtChave.addActionListener(this);
			Label label = new Label();
			label.setText(origem ? relacao.getOrigem().getId() : relacao.getDestino().getId());
			if (origem) {
				add(new PanelTitulo("label.origem"));
			} else {
				add(new PanelTitulo("label.destino"));
			}
			add(new JSeparator());
			add(new PanelCenter(label));
			add(new PanelCenter(new PanelObjeto(origem ? relacao.getOrigem() : relacao.getDestino())));
			add(new PanelCenter(chkPonto));
			add(new PanelCenter(new PanelChave(txtChave, origem ? relacao.getOrigem() : relacao.getDestino())));
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
		private static final long serialVersionUID = 1L;
		private final Action campos = Action.actionIcon("label.campos", Icones.CAMPOS);
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
			objetoSuperficie.selecionarCampo(objeto, coletor, RelacaoContainer.this);
			if (coletor.size() == 1) {
				textField.setText(coletor.get(0));
			}
		}
	}

	private class PanelObjeto extends Panel {
		private static final long serialVersionUID = 1L;
		private final transient Objeto objeto;

		private PanelObjeto(Objeto objeto) {
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

	private class Fichario extends TabbedPane {
		private static final long serialVersionUID = 1L;

		private Fichario() {
			addTab("label.geral", new PanelGeral());
			addTab("label.descricao", new PanelDescricao());
			addTab("label.cor", new PanelCor());
			addTab("label.cor_fonte", new PanelCorFonte());
		}
	}
}