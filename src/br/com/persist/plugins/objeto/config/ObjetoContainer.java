package br.com.persist.plugins.objeto.config;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
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

import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.LabelLinkListener;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelLeft;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TabbedPane;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.macro.MacroProvedor;

public class ObjetoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final BarraButton toolbar = new BarraButton();
	private final ObjetoSuperficie objetoSuperficie;
	private final transient Objeto objeto;
	private final Fichario fichario;

	public ObjetoContainer(Janela janela, ObjetoSuperficie objetoSuperficie, Objeto objeto) {
		this.objetoSuperficie = objetoSuperficie;
		this.objeto = objeto;
		fichario = new Fichario();
		MacroProvedor.limpar();
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, fichario);
		add(BorderLayout.NORTH, toolbar);
	}

	private class PanelGeral extends Panel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private CheckBox chkTransparente = new CheckBox();
		private CheckBox chkCopiarDestac = new CheckBox();
		private TextField txtDeslocXId = new TextField();
		private TextField txtDeslocYId = new TextField();
		private TextField txtIntervalo = new TextField();
		private CheckBox chkDesenharId = new CheckBox();
		private TextField txtArquivo = new TextField();
		private TextField txtId = new TextField();
		private TextField txtX = new TextField();
		private TextField txtY = new TextField();
		private Label labelIcone = new Label();

		private PanelGeral() {
			final String VAZIO = Constantes.VAZIO;
			txtDeslocXId.setText(VAZIO + objeto.getDeslocamentoXId());
			txtDeslocYId.setText(VAZIO + objeto.getDeslocamentoYId());
			chkCopiarDestac.setSelected(objeto.isClonarAoDestacar());
			chkTransparente.setSelected(objeto.isTransparente());
			txtIntervalo.setText(VAZIO + objeto.getIntervalo());
			chkDesenharId.setSelected(objeto.isDesenharId());
			txtArquivo.setText(objeto.getArquivo());
			txtX.setText(VAZIO + objeto.getX());
			txtY.setText(VAZIO + objeto.getY());
			txtId.setText(objeto.getId());
			txtDeslocXId.addFocusListener(focusListenerInner);
			txtDeslocYId.addFocusListener(focusListenerInner);
			txtIntervalo.addFocusListener(focusListenerInner);
			txtArquivo.addFocusListener(focusListenerInner);
			txtId.addFocusListener(focusListenerInner);
			txtX.addFocusListener(focusListenerInner);
			txtY.addFocusListener(focusListenerInner);
			chkTransparente.addActionListener(this);
			chkCopiarDestac.addActionListener(this);
			chkDesenharId.addActionListener(this);
			txtDeslocXId.addActionListener(this);
			txtDeslocYId.addActionListener(this);
			txtIntervalo.addActionListener(this);
			txtArquivo.addActionListener(this);
			txtId.addActionListener(this);
			txtX.addActionListener(this);
			txtY.addActionListener(this);
			if (objeto.getIcon() != null) {
				labelIcone.setIcon(objeto.getIcon());
			}
			PanelLeft panelIcone = new PanelLeft(labelIcone);
			panelIcone.addMouseListener(new IconeListener(objeto, labelIcone));
			Box container = Box.createVerticalBox();
			container.add(criarLinha("label.icone", panelIcone));
			container.add(criarLinha("label.id", txtId));
			container.add(criarLinha("label.x", txtX));
			container.add(criarLinha("label.y", txtY));
			container.add(criarLinha("label.desloc_x_id", txtDeslocXId));
			container.add(criarLinha("label.desloc_y_id", txtDeslocYId));
			container.add(criarLinha("label.intervalo", txtIntervalo));
			container.add(criarLinhaComLink("label.arquivo", txtArquivo,
					Mensagens.getString("hint.arquivo_absoluto_relativo"),
					PanelGeral.this::mensagemPropriedadeArquivo));
			container.add(criarLinha("label.desenhar_id", chkDesenharId));
			container.add(criarLinha("label.transparente", chkTransparente));
			container.add(criarLinha("label.copiar_destacado", chkCopiarDestac));
			add(BorderLayout.CENTER, container);
		}

		private void mensagemPropriedadeArquivo(Label label) {
			Util.mensagem(ObjetoContainer.this, Mensagens.getString("msg.propriedade_arquivo"));
		}

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
				MacroProvedor.xLocal(objeto.getX());
			} else if (txtY == e.getSource()) {
				objeto.setY(Util.getInt(txtY.getText(), objeto.getY()));
				MacroProvedor.yLocal(objeto.getY());
			} else if (txtDeslocXId == e.getSource()) {
				objeto.setDeslocamentoXId(Util.getInt(txtDeslocXId.getText(), objeto.getDeslocamentoXId()));
				MacroProvedor.deslocarXIdDescricao(objeto.getDeslocamentoXId());
			} else if (txtDeslocYId == e.getSource()) {
				objeto.setDeslocamentoYId(Util.getInt(txtDeslocYId.getText(), objeto.getDeslocamentoYId()));
				MacroProvedor.deslocarYIdDescricao(objeto.getDeslocamentoYId());
			} else if (txtIntervalo == e.getSource()) {
				objeto.setIntervalo(Util.getInt(txtIntervalo.getText(), objeto.getIntervalo()));
			}
			actionPerformedCont(e);
			objetoSuperficie.repaint();
		}

		private void actionPerformedCont(ActionEvent e) {
			if (txtId == e.getSource()) {
				String id = txtId.getText();
				if (!Util.estaVazio(id)) {
					Objeto obj = new Objeto();
					obj.setId(id);

					if (!objetoSuperficie.contem(obj)) {
						objeto.setId(id);
						objetoSuperficie.alinharNome(objeto);
					}
				}
			} else if (txtArquivo == e.getSource()) {
				objeto.setArquivo(txtArquivo.getText());
			} else if (chkDesenharId == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setDesenharId(chk.isSelected());
				MacroProvedor.desenharIdDescricao(chk.isSelected());
			}
			actionPerformedCont2(e);
		}

		private void actionPerformedCont2(ActionEvent e) {
			if (chkTransparente == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setTransparente(chk.isSelected());
				MacroProvedor.transparencia(chk.isSelected());
			} else if (chkCopiarDestac == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setClonarAoDestacar(chk.isSelected());
				MacroProvedor.copiarDestacado(chk.isSelected());
			}
		}
	}

	private class PanelBanco extends Panel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private TextField txtFinalConsulta = new TextField();
		private CheckBox chkAjusteAutoForm = new CheckBox();
		private TextField txtChaveamento = new TextField();
		private TextField txtSelectAlter = new TextField();
		private TextField txtComplemento = new TextField();
		private TextField txtSequencias = new TextField();
		private TextField txtMapeamento = new TextField();
		private TextField txtPrefixoNT = new TextField();
		private CheckBox chkColunaInfo = new CheckBox();
		private CheckBox chkAbrirAuto = new CheckBox();
		private TextField txtTabelas = new TextField();
		private CheckBox chkLinkAuto = new CheckBox();
		private TextField txtTabela = new TextField();
		private TextField txtChaves = new TextField();
		private TextField txtGrupo = new TextField();
		private TextField txtJoins = new TextField();
		private CheckBox chkCCSC = new CheckBox();
		private CheckBox chkBPNT = new CheckBox();

		private PanelBanco() {
			chkAjusteAutoForm.setSelected(objeto.isAjusteAutoForm());
			txtSelectAlter.setText(objeto.getSelectAlternativo());
			txtFinalConsulta.setText(objeto.getFinalConsulta());
			txtPrefixoNT.setText(objeto.getPrefixoNomeTabela());
			chkColunaInfo.setSelected(objeto.isColunaInfo());
			txtChaveamento.setText(objeto.getChaveamento());
			txtComplemento.setText(objeto.getComplemento());
			chkAbrirAuto.setSelected(objeto.isAbrirAuto());
			txtSequencias.setText(objeto.getSequencias());
			txtMapeamento.setText(objeto.getMapeamento());
			chkLinkAuto.setSelected(objeto.isLinkAuto());
			txtTabelas.setText(objeto.getTabelas());
			txtTabela.setText(objeto.getTabela2());
			txtChaves.setText(objeto.getChaves());
			chkCCSC.setSelected(objeto.isCcsc());
			chkBPNT.setSelected(objeto.isBpnt());
			txtGrupo.setText(objeto.getGrupo());
			txtJoins.setText(objeto.getJoins());
			txtFinalConsulta.addFocusListener(focusListenerInner);
			txtChaveamento.addFocusListener(focusListenerInner);
			txtComplemento.addFocusListener(focusListenerInner);
			txtSelectAlter.addFocusListener(focusListenerInner);
			txtMapeamento.addFocusListener(focusListenerInner);
			txtSequencias.addFocusListener(focusListenerInner);
			txtPrefixoNT.addFocusListener(focusListenerInner);
			txtTabelas.addFocusListener(focusListenerInner);
			txtTabela.addFocusListener(focusListenerInner);
			txtChaves.addFocusListener(focusListenerInner);
			txtJoins.addFocusListener(focusListenerInner);
			txtGrupo.addFocusListener(focusListenerInner);
			chkAjusteAutoForm.addActionListener(this);
			txtFinalConsulta.addActionListener(this);
			txtChaveamento.addActionListener(this);
			txtComplemento.addActionListener(this);
			txtSelectAlter.addActionListener(this);
			txtMapeamento.addActionListener(this);
			txtSequencias.addActionListener(this);
			chkColunaInfo.addActionListener(this);
			chkAbrirAuto.addActionListener(this);
			txtPrefixoNT.addActionListener(this);
			chkLinkAuto.addActionListener(this);
			txtTabelas.addActionListener(this);
			txtChaves.addActionListener(this);
			txtTabela.addActionListener(this);
			txtGrupo.addActionListener(this);
			txtJoins.addActionListener(this);
			chkCCSC.addActionListener(this);
			chkBPNT.addActionListener(this);
			Box container = Box.createVerticalBox();
			container.add(criarLinha("label.grupo", txtGrupo));
			container.add(criarLinha("label.tabela", txtTabela));
			container.add(criarLinha("label.chaves", txtChaves, Mensagens.getString("hint.chaves")));
			container.add(criarLinha("label.tabelas", txtTabelas));
			container.add(criarLinha("label.select_alter", txtSelectAlter));
			container.add(criarLinha("label.joins", txtJoins, Mensagens.getString("hint.joins")));
			container.add(criarLinha("label.prefixo_nt", txtPrefixoNT));
			container.add(criarLinha("label.sequencias", txtSequencias, Mensagens.getString("hint.sequencias")));
			container.add(criarLinha("label.chaveamento", txtChaveamento, Mensagens.getString("hint.chaveamento")));
			container.add(criarLinha("label.mapeamento", txtMapeamento, Mensagens.getString("hint.mapeamento")));
			container.add(criarLinha("label.complemento", txtComplemento));
			container.add(criarLinha("label.final_consulta", txtFinalConsulta));
			container.add(criarLinha("label.coluna_info", chkColunaInfo));
			container.add(criarLinha("label.abrir_auto", chkAbrirAuto));
			container.add(criarLinha("label.link_auto", chkLinkAuto));
			container.add(criarLinha("label.ccsc", chkCCSC, Mensagens.getString("hint.ccsc")));
			container.add(criarLinha("label.bpnt", chkBPNT, Mensagens.getString("hint.bpnt")));
			container.add(criarLinha("label.ajuste_auto_form", chkAjusteAutoForm,
					Mensagens.getString("hint.ajuste_auto_form")));
			txtChaveamento.addMouseListener(chaveamentoListener);
			txtMapeamento.addMouseListener(mapeamentoListener);
			add(BorderLayout.CENTER, new ScrollPane(container));
		}

		private transient FocusListener focusListenerInner = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				actionPerformed(new ActionEvent(e.getSource(), 0, null));
			}
		};

		private transient MouseListener chaveamentoListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					MiscelaniaDialogo form = MiscelaniaDialogo.criar((Dialog) null, objeto,
							MiscelaniaContainer.Tipo.CHAVEAMENTO);
					form.setLocationRelativeTo(ObjetoContainer.this);
					form.setVisible(true);

					txtChaveamento.setText(objeto.getChaveamento());
				}
			}
		};

		private transient MouseListener mapeamentoListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					MiscelaniaDialogo form = MiscelaniaDialogo.criar((Dialog) null, objeto,
							MiscelaniaContainer.Tipo.MAPEAMENTO);
					form.setLocationRelativeTo(ObjetoContainer.this);
					form.setVisible(true);

					txtMapeamento.setText(objeto.getMapeamento());
				}
			}
		};

		@Override
		public void actionPerformed(ActionEvent e) {
			if (txtChaveamento == e.getSource()) {
				objeto.setChaveamento(txtChaveamento.getText());
			} else if (txtMapeamento == e.getSource()) {
				objeto.setMapeamento(txtMapeamento.getText());
			} else if (txtFinalConsulta == e.getSource()) {
				objeto.setFinalConsulta(txtFinalConsulta.getText());
			} else if (txtComplemento == e.getSource()) {
				objeto.setComplemento(txtComplemento.getText());
			} else if (txtGrupo == e.getSource()) {
				objeto.setGrupo(txtGrupo.getText());
			} else if (txtTabela == e.getSource()) {
				objeto.setTabela(txtTabela.getText());
			} else if (txtPrefixoNT == e.getSource()) {
				objeto.setPrefixoNomeTabela(txtPrefixoNT.getText());
			} else if (txtChaves == e.getSource()) {
				objeto.setChaves(txtChaves.getText());
			} else if (txtSequencias == e.getSource()) {
				objeto.setSequencias(txtSequencias.getText());
			}
			actionPerformedCont(e);
			objetoSuperficie.repaint();
		}

		private void actionPerformedCont(ActionEvent e) {
			if (chkColunaInfo == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setColunaInfo(chk.isSelected());
				MacroProvedor.colunaInfo(chk.isSelected());
			} else if (chkAbrirAuto == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setAbrirAuto(chk.isSelected());
				MacroProvedor.abrirAuto(chk.isSelected());
			} else if (chkLinkAuto == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setLinkAuto(chk.isSelected());
				MacroProvedor.linkAuto(chk.isSelected());
			} else if (chkCCSC == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setCcsc(chk.isSelected());
				MacroProvedor.confirmarCsc(chk.isSelected());
			} else if (chkBPNT == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setBpnt(chk.isSelected());
				MacroProvedor.bloquearPnt(chk.isSelected());
			}
			actionPerformedCont2(e);
		}

		private void actionPerformedCont2(ActionEvent e) {
			if (chkAjusteAutoForm == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setAjusteAutoForm(chk.isSelected());
				MacroProvedor.ajusteAutoForm(chk.isSelected());
			} else if (txtTabelas == e.getSource()) {
				objeto.setTabelas(txtTabelas.getText());
			} else if (txtSelectAlter == e.getSource()) {
				objeto.setSelectAlternativo(txtSelectAlter.getText());
			} else if (txtJoins == e.getSource()) {
				objeto.setJoins(txtJoins.getText());
			}
		}
	}

	private Component criarLinha(String chaveRotulo, JComponent componente) {
		return criarLinha(chaveRotulo, componente, null);
	}

	private Component criarLinha(String chaveRotulo, JComponent componente, String hint) {
		return criarLinhaComLink(chaveRotulo, componente, hint, null);
	}

	private Component criarLinhaComLink(String chaveRotulo, JComponent componente, String hint,
			LabelLinkListener linkListener) {
		Dimension largura = new Dimension(120, 0);
		Panel linha = new Panel();
		Label label = new Label(chaveRotulo);
		label.setHorizontalAlignment(Label.RIGHT);
		label.setPreferredSize(largura);
		label.setMinimumSize(largura);
		label.setMaximumSize(largura);
		if (!Util.estaVazio(hint)) {
			label.setToolTipText(hint);
		}
		linha.add(BorderLayout.CENTER, componente);
		linha.add(BorderLayout.WEST, label);
		if (linkListener != null) {
			label.modoLink(linkListener);
		}
		return linha;
	}

	private class PanelDescricao extends Panel {
		private static final long serialVersionUID = 1L;
		private final TextArea textArea = new TextArea();

		private PanelDescricao() {
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

	private class PanelCorFonte extends Panel implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final Toolbar toolbar = new Toolbar();
		private final JColorChooser colorChooser;

		private PanelCorFonte() {
			colorChooser = new JColorChooser(objeto.getCorFonte());
			colorChooser.getSelectionModel().addChangeListener(this);
			add(BorderLayout.CENTER, colorChooser);
			add(BorderLayout.NORTH, toolbar);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			objeto.setCorFonte(colorChooser.getColor());
			MacroProvedor.corFonte(objeto.getCorFonte());
			objetoSuperficie.repaint();
		}

		private class Toolbar extends BarraButton {
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(null, COPIAR, COLAR);
			}

			@Override
			protected void copiar() {
				Preferencias.setCorFonteCopiado(colorChooser.getColor());
				copiarMensagem(".");
			}

			@Override
			protected void colar() {
				objeto.setCorFonte(Preferencias.getCorFonteCopiado());
				MacroProvedor.corFonte(objeto.getCorFonte());
				colorChooser.setColor(objeto.getCorFonte());
				objetoSuperficie.repaint();
			}
		}
	}

	private class PanelCor extends Panel implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final JColorChooser colorChooser;

		private PanelCor() {
			colorChooser = new JColorChooser(objeto.getCor());
			colorChooser.getSelectionModel().addChangeListener(this);
			add(BorderLayout.CENTER, colorChooser);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			objeto.setCor(colorChooser.getColor());
			MacroProvedor.corFundo(objeto.getCor());
			objetoSuperficie.repaint();
		}
	}

	private class IconeListener extends MouseAdapter {
		private final Objeto objeto;
		private final Label label;

		private IconeListener(Objeto objeto, Label label) {
			this.objeto = objeto;
			this.label = label;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			IconeDialogo form = IconeDialogo.criar((Dialog) null, objeto, label);
			form.setLocationRelativeTo(ObjetoContainer.this);
			form.setVisible(true);
			objetoSuperficie.repaint();
		}
	}

	private class Fichario extends TabbedPane {
		private static final long serialVersionUID = 1L;

		private Fichario() {
			addTab("label.geral", new PanelGeral());
			addTab("label.banco", new PanelBanco());
			addTab("label.descricao", new PanelDescricao());
			addTab("label.cor", new PanelCor());
			addTab("label.cor_fonte", new PanelCorFonte());
		}
	}
}