package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.Instrucao;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.PanelCenter;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TabbedPane;
import br.com.persist.comp.TextArea;
import br.com.persist.comp.TextField;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Superficie;
import br.com.persist.dialogo.ChaveBuscaDialogo;
import br.com.persist.dialogo.IconeDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class ObjetoConfigContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final BarraButton toolbar = new BarraButton();
	private final transient Objeto objeto;
	private final Superficie superficie;

	public ObjetoConfigContainer(IJanela janela, Superficie superficie, Objeto objeto) {
		this.superficie = superficie;
		Formulario.macro.limpar();
		this.objeto = objeto;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new Fichario());
		add(BorderLayout.NORTH, toolbar);
	}

	private class PanelGeral extends Panel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private TextField txtBuscaAutomatica = new TextField();
		private TextField txtLinkAutomatico = new TextField();
		private TextField txtFinalConsulta = new TextField();
		private TextField txtChaveamento = new TextField();
		private TextField txtComplemento = new TextField();
		private TextField txtSequencias = new TextField();
		private CheckBox chkTransparente = new CheckBox();
		private CheckBox chkCopiarDestac = new CheckBox();
		private TextField txtMapeamento = new TextField();
		private TextField txtDeslocXId = new TextField();
		private TextField txtDeslocYId = new TextField();
		private TextField txtIntervalo = new TextField();
		private CheckBox chkDesenharId = new CheckBox();
		private CheckBox chkColunaInfo = new CheckBox();
		private CheckBox chkAbrirAuto = new CheckBox();
		private CheckBox chkLinkAuto = new CheckBox();
		private TextField txtTabela = new TextField();
		private TextField txtChaves = new TextField();
		private CheckBox chkCCSC = new CheckBox();
		private TextField txtId = new TextField();
		private TextField txtX = new TextField();
		private TextField txtY = new TextField();
		private Label labelIcone = new Label();

		PanelGeral() {
			txtBuscaAutomatica.setText(objeto.getBuscaAutomatica());
			chkCopiarDestac.setSelected(objeto.isCopiarDestacado());
			txtDeslocXId.setText("" + objeto.getDeslocamentoXId());
			txtDeslocYId.setText("" + objeto.getDeslocamentoYId());
			txtLinkAutomatico.setText(objeto.getLinkAutomatico());
			chkTransparente.setSelected(objeto.isTransparente());
			txtFinalConsulta.setText(objeto.getFinalConsulta());
			txtIntervalo.setText("" + objeto.getIntervalo());
			chkDesenharId.setSelected(objeto.isDesenharId());
			chkColunaInfo.setSelected(objeto.isColunaInfo());
			txtChaveamento.setText(objeto.getChaveamento());
			txtComplemento.setText(objeto.getComplemento());
			chkAbrirAuto.setSelected(objeto.isAbrirAuto());
			txtSequencias.setText(objeto.getSequencias());
			txtMapeamento.setText(objeto.getMapeamento());
			chkLinkAuto.setSelected(objeto.isLinkAuto());
			txtTabela.setText(objeto.getTabela2());
			txtChaves.setText(objeto.getChaves());
			chkCCSC.setSelected(objeto.isCcsc());
			txtX.setText("" + objeto.getX());
			txtY.setText("" + objeto.getY());
			txtId.setText(objeto.getId());

			txtBuscaAutomatica.addFocusListener(focusListenerInner);
			txtLinkAutomatico.addFocusListener(focusListenerInner);
			txtFinalConsulta.addFocusListener(focusListenerInner);
			txtChaveamento.addFocusListener(focusListenerInner);
			txtComplemento.addFocusListener(focusListenerInner);
			txtMapeamento.addFocusListener(focusListenerInner);
			txtSequencias.addFocusListener(focusListenerInner);
			txtDeslocXId.addFocusListener(focusListenerInner);
			txtDeslocYId.addFocusListener(focusListenerInner);
			txtIntervalo.addFocusListener(focusListenerInner);
			txtTabela.addFocusListener(focusListenerInner);
			txtChaves.addFocusListener(focusListenerInner);
			txtId.addFocusListener(focusListenerInner);
			txtX.addFocusListener(focusListenerInner);
			txtY.addFocusListener(focusListenerInner);

			txtBuscaAutomatica.addActionListener(this);
			txtLinkAutomatico.addActionListener(this);
			txtFinalConsulta.addActionListener(this);
			chkTransparente.addActionListener(this);
			chkCopiarDestac.addActionListener(this);
			txtChaveamento.addActionListener(this);
			txtComplemento.addActionListener(this);
			chkDesenharId.addActionListener(this);
			chkColunaInfo.addActionListener(this);
			txtMapeamento.addActionListener(this);
			txtSequencias.addActionListener(this);
			chkAbrirAuto.addActionListener(this);
			txtDeslocXId.addActionListener(this);
			txtDeslocYId.addActionListener(this);
			txtIntervalo.addActionListener(this);
			chkLinkAuto.addActionListener(this);
			txtTabela.addActionListener(this);
			txtChaves.addActionListener(this);
			chkCCSC.addActionListener(this);
			txtId.addActionListener(this);
			txtX.addActionListener(this);
			txtY.addActionListener(this);

			if (objeto.getIcon() != null) {
				labelIcone.setIcon(objeto.getIcon());
			}

			PanelCenter panelIcone = new PanelCenter(labelIcone);
			panelIcone.setBorder(BorderFactory.createEtchedBorder());
			panelIcone.addMouseListener(new IconeListener(objeto, labelIcone));

			Box container = Box.createVerticalBox();
			container.add(criarLinha("label.icone", panelIcone));
			container.add(criarLinha("label.id", txtId));
			container.add(criarLinha("label.x", txtX));
			container.add(criarLinha("label.y", txtY));
			container.add(criarLinha("label.desloc_x_id", txtDeslocXId));
			container.add(criarLinha("label.desloc_y_id", txtDeslocYId));
			container.add(criarLinha("label.intervalo", txtIntervalo));
			container.add(criarLinha("label.tabela", txtTabela));
			container.add(criarLinha("label.chaves", txtChaves));
			container.add(criarLinha("label.sequencias", txtSequencias, Mensagens.getString("hint.sequencias")));
			container.add(criarLinha("label.chaveamento", txtChaveamento, Mensagens.getString("hint.chaveamento")));
			container.add(criarLinha("label.buscaAuto", txtBuscaAutomatica, Mensagens.getString("hint.buscaAuto")));
			container.add(criarLinha("label.mapeamento", txtMapeamento, Mensagens.getString("hint.mapeamento")));
			container.add(criarLinha("label.linkAuto", txtLinkAutomatico, Mensagens.getString("hint.linkAuto")));
			container.add(criarLinha("label.complemento", txtComplemento));
			container.add(criarLinha("label.final_consulta", txtFinalConsulta));
			container.add(criarLinha("label.coluna_info", chkColunaInfo));
			container.add(criarLinha("label.abrir_auto", chkAbrirAuto));
			container.add(criarLinha("label.link_auto", chkLinkAuto));
			container.add(criarLinha("label.ccsc", chkCCSC, Mensagens.getString("hint.ccsc")));
			container.add(criarLinha("label.desenhar_id", chkDesenharId));
			container.add(criarLinha("label.transparente", chkTransparente));
			container.add(criarLinha("label.copiar_destacado", chkCopiarDestac));

			txtBuscaAutomatica.addMouseListener(buscaAutomaticaListener);
			txtLinkAutomatico.addMouseListener(linkAutomaticoListener);
			txtChaveamento.addMouseListener(chaveamentoListener);
			txtMapeamento.addMouseListener(mapeamentoListener);

			add(BorderLayout.CENTER, container);
		}

		private transient MouseListener buscaAutomaticaListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					ChaveBuscaDialogo form = new ChaveBuscaDialogo((Dialog) null, objeto,
							ChaveBuscaContainer.Tipo.BUSCA);
					form.setLocationRelativeTo(ObjetoConfigContainer.this);
					form.setVisible(true);

					txtBuscaAutomatica.setText(objeto.getBuscaAutomatica());
				}
			}
		};

		private transient MouseListener linkAutomaticoListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					ChaveBuscaDialogo form = new ChaveBuscaDialogo((Dialog) null, objeto,
							ChaveBuscaContainer.Tipo.LINK);
					form.setLocationRelativeTo(ObjetoConfigContainer.this);
					form.setVisible(true);

					txtLinkAutomatico.setText(objeto.getLinkAutomatico());
				}
			}
		};

		private transient MouseListener chaveamentoListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					ChaveBuscaDialogo form = new ChaveBuscaDialogo((Dialog) null, objeto,
							ChaveBuscaContainer.Tipo.CHAVE);
					form.setLocationRelativeTo(ObjetoConfigContainer.this);
					form.setVisible(true);

					txtChaveamento.setText(objeto.getChaveamento());
				}
			}
		};

		private transient MouseListener mapeamentoListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					ChaveBuscaDialogo form = new ChaveBuscaDialogo((Dialog) null, objeto,
							ChaveBuscaContainer.Tipo.MAPA);
					form.setLocationRelativeTo(ObjetoConfigContainer.this);
					form.setVisible(true);

					txtMapeamento.setText(objeto.getMapeamento());
				}
			}
		};

		private transient FocusListener focusListenerInner = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				actionPerformed(new ActionEvent(e.getSource(), 0, null));
			}
		};

		@Override
		public void actionPerformed(ActionEvent e) {
			if (txtX == e.getSource()) {
				objeto.setX(Util.getInt(txtX.getText(), objeto.getX()));
				Formulario.macro.xLocal(objeto.getX());

			} else if (txtY == e.getSource()) {
				objeto.setY(Util.getInt(txtY.getText(), objeto.getY()));
				Formulario.macro.yLocal(objeto.getY());

			} else if (txtDeslocXId == e.getSource()) {
				objeto.setDeslocamentoXId(Util.getInt(txtDeslocXId.getText(), objeto.getDeslocamentoXId()));
				Formulario.macro.deslocarXIdDescricao(objeto.getDeslocamentoXId());

			} else if (txtDeslocYId == e.getSource()) {
				objeto.setDeslocamentoYId(Util.getInt(txtDeslocYId.getText(), objeto.getDeslocamentoYId()));
				Formulario.macro.deslocarYIdDescricao(objeto.getDeslocamentoYId());

			} else if (txtIntervalo == e.getSource()) {
				objeto.setIntervalo(Util.getInt(txtIntervalo.getText(), objeto.getIntervalo()));

			} else if (txtBuscaAutomatica == e.getSource()) {
				objeto.setBuscaAutomatica(txtBuscaAutomatica.getText());

			} else if (txtLinkAutomatico == e.getSource()) {
				objeto.setLinkAutomatico(txtLinkAutomatico.getText());

			} else if (txtChaveamento == e.getSource()) {
				objeto.setChaveamento(txtChaveamento.getText());

			} else if (txtMapeamento == e.getSource()) {
				objeto.setMapeamento(txtMapeamento.getText());

			} else if (txtFinalConsulta == e.getSource()) {
				objeto.setFinalConsulta(txtFinalConsulta.getText());

			} else if (txtComplemento == e.getSource()) {
				objeto.setComplemento(txtComplemento.getText());

			} else if (txtTabela == e.getSource()) {
				objeto.setTabela(txtTabela.getText());

			} else if (txtChaves == e.getSource()) {
				objeto.setChaves(txtChaves.getText());

			} else if (txtSequencias == e.getSource()) {
				objeto.setSequencias(txtSequencias.getText());
			}

			actionPerformedCont(e);

			superficie.repaint();
		}

		private void actionPerformedCont(ActionEvent e) {
			if (txtId == e.getSource()) {
				String id = txtId.getText();

				if (!Util.estaVazio(id)) {
					Objeto obj = new Objeto();
					obj.setId(id);

					if (!superficie.contem(obj)) {
						objeto.setId(id);
						superficie.alinharNome(objeto);
					}
				}
			} else if (chkDesenharId == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setDesenharId(chk.isSelected());
				Formulario.macro.desenharIdDescricao(chk.isSelected());

			} else if (chkColunaInfo == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setColunaInfo(chk.isSelected());
				Formulario.macro.colunaInfo(chk.isSelected());

			} else if (chkAbrirAuto == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setAbrirAuto(chk.isSelected());
				Formulario.macro.abrirAuto(chk.isSelected());

			} else if (chkLinkAuto == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setLinkAuto(chk.isSelected());
				Formulario.macro.linkAuto(chk.isSelected());

			} else if (chkCCSC == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setCcsc(chk.isSelected());
				Formulario.macro.confirmarCsc(chk.isSelected());

			} else if (chkTransparente == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setTransparente(chk.isSelected());
				Formulario.macro.transparencia(chk.isSelected());

			} else if (chkCopiarDestac == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setCopiarDestacado(chk.isSelected());
				Formulario.macro.copiarDestacado(chk.isSelected());
			}
		}

		private Box criarLinha(String chaveRotulo, JComponent componente) {
			return criarLinha(chaveRotulo, componente, null);
		}

		private Box criarLinha(String chaveRotulo, JComponent componente, String hint) {
			Box box = Box.createHorizontalBox();

			Label label = new Label(chaveRotulo);
			label.setHorizontalAlignment(Label.RIGHT);
			label.setPreferredSize(new Dimension(100, 0));
			label.setMinimumSize(new Dimension(100, 0));

			if (!Util.estaVazio(hint)) {
				label.setToolTipText(hint);
			}

			box.add(label);

			if (componente instanceof CheckBox) {
				box.add(componente);
				box.add(Box.createHorizontalGlue());

			} else if (componente instanceof JPanel) {
				box.add(Box.createHorizontalStrut(3));
				box.add(componente);
				box.add(Box.createHorizontalStrut(2));

			} else {
				box.add(componente);
			}

			return box;
		}
	}

	private class PanelDesc extends Panel {
		private static final long serialVersionUID = 1L;
		private final TextArea textArea = new TextArea();

		PanelDesc() {
			textArea.setText(objeto.getDescricao());
			textArea.addKeyListener(keyListenerInner);
			add(BorderLayout.CENTER, textArea);
		}

		private transient KeyListener keyListenerInner = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				objeto.setDescricao(textArea.getText());
			}
		};
	}

	private class PanelInstrucao extends Panel {
		private static final long serialVersionUID = 1L;
		private final Panel panelLista;

		PanelInstrucao() {
			panelLista = new Panel(new GridLayout(0, 1, 0, 20));

			add(BorderLayout.NORTH, new PanelNome());
			add(BorderLayout.CENTER, new ScrollPane(panelLista));

			for (Instrucao i : objeto.getInstrucoes()) {
				panelLista.add(new ScrollPane(new PanelInst(i)));
			}
		}

		class PanelNome extends Panel implements ActionListener {
			private static final long serialVersionUID = 1L;
			TextField nome = new TextField();

			PanelNome() {
				Label label = new Label("label.nome_enter");
				label.setToolTipText(Mensagens.getString("hint.instrucoes"));
				add(BorderLayout.WEST, label);
				add(BorderLayout.CENTER, nome);
				nome.addActionListener(this);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Util.estaVazio(nome.getText())) {
					Instrucao i = new Instrucao(nome.getText().trim());
					objeto.addInstrucao(i);
					panelLista.add(new PanelInst(i));
					SwingUtilities.updateComponentTreeUI(ObjetoConfigContainer.this);
				}
			}
		}

		class PanelInst extends Panel {
			private static final long serialVersionUID = 1L;
			final transient Instrucao instrucao;
			TextField nome = new TextField();
			TextArea valor = new TextArea();

			PanelInst(Instrucao i) {
				this.instrucao = i;
				nome.setEnabled(false);
				nome.setText(i.getNome());
				valor.setText(i.getValor());

				add(BorderLayout.NORTH, nome);
				add(BorderLayout.CENTER, valor);

				valor.addKeyListener(keyListenerInner);
			}

			private transient KeyListener keyListenerInner = new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					instrucao.setValor(valor.getText());
				}
			};
		}
	}

	private class PanelCorFonte extends Panel implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final JColorChooser colorChooser;

		PanelCorFonte() {
			colorChooser = new JColorChooser(objeto.getCorFonte());
			colorChooser.getSelectionModel().addChangeListener(this);
			add(BorderLayout.CENTER, colorChooser);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			objeto.setCorFonte(colorChooser.getColor());
			Formulario.macro.corFonte(objeto.getCorFonte());
			superficie.repaint();
		}
	}

	private class PanelCor extends Panel implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final JColorChooser colorChooser;

		PanelCor() {
			colorChooser = new JColorChooser(objeto.getCor());
			colorChooser.getSelectionModel().addChangeListener(this);
			add(BorderLayout.CENTER, colorChooser);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			objeto.setCor(colorChooser.getColor());
			Formulario.macro.corFundo(objeto.getCor());
			superficie.repaint();
		}
	}

	private class IconeListener extends MouseAdapter {
		private final Objeto objeto;
		private final Label label;

		IconeListener(Objeto objeto, Label label) {
			this.objeto = objeto;
			this.label = label;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			IconeDialogo form = new IconeDialogo((Dialog) null, objeto, label);
			form.setLocationRelativeTo(ObjetoConfigContainer.this);
			form.setVisible(true);
			superficie.repaint();
		}
	}

	private class Fichario extends TabbedPane {
		private static final long serialVersionUID = 1L;

		Fichario() {
			addTab("label.geral", new PanelGeral());
			addTab("label.desc", new PanelDesc());
			addTab("label.cor", new PanelCor());
			addTab("label.cor_fonte", new PanelCorFonte());
			addTab("label.instrucoes", new PanelInstrucao());
		}
	}
}