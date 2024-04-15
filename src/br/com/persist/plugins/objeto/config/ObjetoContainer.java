package br.com.persist.plugins.objeto.config;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR0;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Window;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.LabelLinkListener;
import br.com.persist.componente.LabelTextTemp;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelLeft;
import br.com.persist.componente.Popup;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TabbedPane;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoMensagens;
import br.com.persist.plugins.objeto.ObjetoPreferencia;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.ObjetoSuperficieUtil;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.macro.MacroProvedor;
import br.com.persist.plugins.objeto.vinculo.ParaTabela;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;

public class ObjetoContainer extends Panel {
	private transient List<CompChave> vinculados = new ArrayList<>();
	private String chaveMensagem = "msg.config_tabela_aba_banco";
	private String labelVinculo = "label.aplicar_arq_vinculo";
	private VinculadoPopup popupVinculo = new VinculadoPopup();
	private static final long serialVersionUID = 1L;
	private final ObjetoSuperficie objetoSuperficie;
	private final Toolbar toolbar = new Toolbar();
	private TextField txtTabela = new TextField();
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

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
	}

	private transient MouseListener listenerVinculado = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			JComponent comp = (JComponent) e.getSource();
			if (e.isPopupTrigger() && !Util.isEmpty(txtTabela.getText())) {
				popupVinculo.compChave = getCompChave(comp);
				if (popupVinculo.showValido()) {
					popupVinculo.show(comp, e.getX(), e.getY());
				}
			}
		}

		private CompChave getCompChave(JComponent comp) {
			for (CompChave cc : vinculados) {
				if (cc.comp == comp) {
					return cc;
				}
			}
			return null;
		}
	};

	private class VinculadoPopup extends Popup {
		private Action action = acaoMenu(labelVinculo);
		private static final long serialVersionUID = 1L;
		private transient CompChave compChave;

		private VinculadoPopup() {
			add(action);
			action.setActionListener(e -> processar());
		}

		void processar() {
			if (compChave == null) {
				return;
			}
			Vinculacao vinculacao = null;
			try {
				vinculacao = ObjetoSuperficieUtil.getVinculacao(objetoSuperficie);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("VINCULAR EM BANCO", ex, ObjetoContainer.this);
				return;
			}
			if (vinculacao == null) {
				return;
			}
			String tabela = txtTabela.getText().trim();
			ParaTabela para = vinculacao.getParaTabela(tabela);
			if (para == null) {
				para = new ParaTabela(tabela);
				vinculacao.putParaTabela(para);
			}
			if ("APELIDO".equals(compChave.chave)) {
				para.setApelido(compChave.getText());
			} else if ("GRUPO".equals(compChave.chave)) {
				para.setGrupo(compChave.getText());
			} else if ("CHAVES".equals(compChave.chave)) {
				para.setChaves(compChave.getText());
			} else if ("JOINS".equals(compChave.chave)) {
				para.setJoins(compChave.getText());
			} else if ("TABELAS".equals(compChave.chave)) {
				para.setTabelas(compChave.getText());
			} else if ("ESQUEMA_ALTER".equals(compChave.chave)) {
				para.setEsquemaAlternativo(compChave.getText());
			} else if ("TABELA_ALTER".equals(compChave.chave)) {
				para.setTabelaAlternativo(compChave.getText());
			} else if ("SELECT_ALTER".equals(compChave.chave)) {
				para.setSelectAlternativo(compChave.getText());
			} else if ("PREFIXO_NT".equals(compChave.chave)) {
				para.setPrefixoNomeTabela(compChave.getText());
			}
			processar1(para);
			processar2(para);
			processar3(para);
			salvarVinculacao(vinculacao);
		}

		void processar1(ParaTabela para) {
			if ("SEQUENCIA".equals(compChave.chave)) {
				para.setSequencias(compChave.getText());
			} else if ("CHAVEAMENTO".equals(compChave.chave)) {
				para.setCampoNomes(compChave.getText());
			} else if ("MAPEAMENTO".equals(compChave.chave)) {
				para.setMapeamento(compChave.getText());
			} else if ("COMPLEMENTO".equals(compChave.chave)) {
				para.setComplemento(compChave.getText());
			} else if ("DESTACAVEIS".equals(compChave.chave)) {
				para.setDestacaveis(compChave.getText());
			} else if ("CHECAR_REGISTRO".equals(compChave.chave)) {
				para.setBiblioChecagem(compChave.getText());
			}
		}

		void processar2(ParaTabela para) {
			if ("ORDER_BY".equals(compChave.chave)) {
				para.setOrderBy(compChave.getText());
			} else if ("CLASSBIBLIO".equals(compChave.chave)) {
				para.setClassBiblio(compChave.getText());
			} else if ("FINAL_CONSULTA".equals(compChave.chave)) {
				para.setFinalConsulta(compChave.getText());
			} else if ("COLUNA_INFO".equals(compChave.chave)) {
				para.setColunaInfo(compChave.getBool());
			} else if ("ABRIR_AUTO".equals(compChave.chave)) {
				para.setDestacavel(compChave.getBool());
			} else if ("LINK_AUTO".equals(compChave.chave)) {
				para.setLinkAuto(compChave.getBool());
			} else if ("LARGURA_ROTULOS".equals(compChave.chave)) {
				para.setLarguraRotulos(compChave.getBool());
			} else if ("TRANSPARENTE".equals(compChave.chave)) {
				para.setTransparente(compChave.getBool());
			} else if ("CLONAR_DESTA".equals(compChave.chave)) {
				para.setClonarAoDestacar(compChave.getBool());
			} else if ("IGNORAR".equals(compChave.chave)) {
				para.setIgnorar(compChave.getBool());
			} else if ("SANE".equals(compChave.chave)) {
				para.setSane(compChave.getBool());
			} else if ("CCSC".equals(compChave.chave)) {
				para.setCcsc(compChave.getBool());
			} else if ("BPNT".equals(compChave.chave)) {
				para.setBpnt(compChave.getBool());
			}
		}

		void processar3(ParaTabela para) {
			if ("AJUSTE_AUTO".equals(compChave.chave)) {
				para.setAjustarAltura(compChave.getBool());
			} else if ("AJUSTE_LARG".equals(compChave.chave)) {
				para.setAjustarLargura(compChave.getBool());
			} else if ("INSTRUCAO".equals(compChave.chave)) {
				para.addInstrucao(compChave.getText());
			} else if ("FILTRO".equals(compChave.chave)) {
				para.addFiltro(compChave.getText());
			}
		}

		boolean showValido() {
			if (compChave == null) {
				return false;
			}
			return compChave.comp instanceof TextField || compChave.comp instanceof CheckBox;
		}
	}

	class CompChave {
		final JComponent comp;
		final String chave;

		CompChave(JComponent comp, String chave) {
			this.chave = chave;
			this.comp = comp;
		}

		String getText() {
			return ((TextField) comp).getText();
		}

		String getBool() {
			return "" + ((CheckBox) comp).isSelected();
		}
	}

	private class PanelGeral extends Panel implements ActionListener {
		private TextField txtBiblioChecagem = new TextField();
		private CheckBox chkTransparente = new CheckBox();
		private CheckBox chkCopiarDestac = new CheckBox();
		private TextField txtDeslocXId = new TextField();
		private TextField txtDeslocYId = new TextField();
		private TextField txtIntervalo = new TextField();
		private TextField txtInstrucao = new TextField();
		private static final long serialVersionUID = 1L;
		private CheckBox chkDesenharId = new CheckBox();
		private TextField txtArquivo = new TextField();
		private TextField txtFiltro = new TextField();
		private TextField txtId = new TextField();
		private TextField txtX = new TextField();
		private TextField txtY = new TextField();
		private Label labelIcone = new Label();

		private PanelGeral() {
			final String VAZIO = Constantes.VAZIO;
			txtDeslocXId.setText(VAZIO + objeto.getDeslocamentoXId());
			txtDeslocYId.setText(VAZIO + objeto.getDeslocamentoYId());
			chkCopiarDestac.setSelected(objeto.isClonarAoDestacar());
			txtBiblioChecagem.setText(objeto.getBiblioChecagem());
			chkTransparente.setSelected(objeto.isTransparente());
			txtIntervalo.setText(VAZIO + objeto.getIntervalo());
			chkDesenharId.setSelected(objeto.isDesenharId());
			txtArquivo.setText(objeto.getArquivo());
			txtX.setText(VAZIO + objeto.getX());
			txtY.setText(VAZIO + objeto.getY());
			txtId.setText(objeto.getId());
			txtBiblioChecagem.addFocusListener(focusListenerInner);
			txtDeslocXId.addFocusListener(focusListenerInner);
			txtDeslocYId.addFocusListener(focusListenerInner);
			txtIntervalo.addFocusListener(focusListenerInner);
			txtArquivo.addFocusListener(focusListenerInner);
			txtId.addFocusListener(focusListenerInner);
			txtX.addFocusListener(focusListenerInner);
			txtY.addFocusListener(focusListenerInner);
			txtBiblioChecagem.addActionListener(this);
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
				labelIcone.setToolTipText(objeto.getIcone());
				labelIcone.setIcon(objeto.getIcon());
			}
			PanelLeft panelIcone = new PanelLeft(labelIcone);
			panelIcone.addMouseListener(new IconeListener(objeto, labelIcone));
			Box container = Box.createVerticalBox();
			container.add(criarLinha("label.icone", panelIcone));
			Panel panel = criarLinhaCopiarRotulo("label.id", txtId);
			configHora(panel);
			container.add(panel);
			container.add(criarLinha("label.x", txtX));
			container.add(criarLinha("label.y", txtY));
			container.add(criarLinhaRotulo("label.desloc_x_id", txtDeslocXId));
			container.add(criarLinhaRotulo("label.desloc_y_id", txtDeslocYId));
			container.add(criarLinhaCopiar("label.intervalo", txtIntervalo));
			container.add(criarLinhaComLinkCopiar("label.arquivo", txtArquivo,
					ObjetoMensagens.getString("hint.arquivo_absoluto_relativo"),
					PanelGeral.this::mensagemPropriedadeArquivo));
			container.add(criarLinha("label.checar_reg", txtBiblioChecagem));
			container.add(criarLinha("label.desenhar_id", chkDesenharId));
			container.add(criarLinha("label.transparente", chkTransparente));
			container.add(criarLinhaRotulo("label.copiar_destacado", chkCopiarDestac));
			container.add(criarLinhaComLinkCopiar("label.add_instrucao", txtInstrucao,
					ObjetoMensagens.getString("hint.add_instrucao"), PanelGeral.this::mensagemAddInstrucao));
			container.add(criarLinhaComLinkCopiar("label.add_filtro", txtFiltro,
					ObjetoMensagens.getString("hint.add_filtro"), PanelGeral.this::mensagemAddFiltro));
			add(BorderLayout.CENTER, new ScrollPane(container));
			vincular();
		}

		private void configHora(Panel panel) {
			for (Component c : panel.getComponents()) {
				if (c instanceof PanelCopiarColar) {
					PanelCopiarColar pnl = (PanelCopiarColar) c;
					pnl.buttonColar.configHora();
				}
			}
		}

		private void vincular() {
			vinculados.add(new CompChave(txtBiblioChecagem, "CHECAR_REGISTRO"));
			vinculados.add(new CompChave(chkTransparente, "TRANSPARENTE"));
			vinculados.add(new CompChave(chkCopiarDestac, "CLONAR_DESTA"));
			vinculados.add(new CompChave(txtInstrucao, "INSTRUCAO"));
			vinculados.add(new CompChave(txtFiltro, "FILTRO"));

			txtBiblioChecagem.addMouseListener(listenerVinculado);
			chkTransparente.addMouseListener(listenerVinculado);
			chkCopiarDestac.addMouseListener(listenerVinculado);
			txtInstrucao.addMouseListener(listenerVinculado);
			txtFiltro.addMouseListener(listenerVinculado);
		}

		private void mensagemPropriedadeArquivo(Label label) {
			Util.mensagem(ObjetoContainer.this, ObjetoMensagens.getString("msg.propriedade_arquivo"));
		}

		private void mensagemAddInstrucao(Label label) {
			Util.mensagem(ObjetoContainer.this, ObjetoMensagens.getString("msg.add_instrucao"));
		}

		private void mensagemAddFiltro(Label label) {
			Util.mensagem(ObjetoContainer.this, ObjetoMensagens.getString("msg.add_filtro"));
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

		private void configLarguraFonte() {
			Font font = getFont();
			if (font != null) {
				FontMetrics fm = getFontMetrics(font);
				if (fm != null) {
					objeto.setLarguraId(fm.stringWidth(objeto.getId()));
				}
			}
		}

		private void actionPerformedCont(ActionEvent e) {
			if (txtId == e.getSource()) {
				String id = txtId.getText();
				if (!Util.isEmpty(id)) {
					Objeto obj = new Objeto();
					obj.setId(id);

					if (!ObjetoSuperficieUtil.contem(objetoSuperficie, obj)) {
						objeto.setId(id);
						configLarguraFonte();
					}
				}
			} else if (txtArquivo == e.getSource()) {
				objeto.setArquivo(txtArquivo.getText());
			} else if (chkDesenharId == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setDesenharId(chk.isSelected());
				MacroProvedor.desenharIdDescricao(chk.isSelected());
			} else if (txtBiblioChecagem == e.getSource()) {
				objeto.setBiblioChecagem(txtBiblioChecagem.getText());
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

		private Panel criarLinhaComLinkCopiar(String chaveRotulo, TextField textField, String hint,
				LabelLinkListener linkListener) {
			Panel panel = criarLinhaComLink(chaveRotulo, true, textField, hint, linkListener);
			panel.add(BorderLayout.EAST, new PanelCopiarColar(textField));
			return panel;
		}
	}

	private class PanelBanco extends Panel implements ActionListener {
		private TextField txtFinalConsulta = new TextField();
		private CheckBox chkAjusteAutoForm = new CheckBox();
		private CheckBox chkAjusteLargForm = new CheckBox();
		private CheckBox chkLarguraRotulos = new CheckBox();
		private TextField txtEsquemaAlter = new TextField();
		private TextField txtChaveamento = new TextField();
		private TextField txtSelectAlter = new TextField();
		private TextField txtComplemento = new TextField();
		private TextField txtClassBiblio = new TextField();
		private TextField txtDestacaveis = new TextField();
		private TextField txtTabelaAlter = new TextField();
		private TextField txtSequencias = new TextField();
		private TextField txtMapeamento = new TextField();
		private TextField txtPrefixoNT = new TextField();
		private static final long serialVersionUID = 1L;
		private CheckBox chkColunaInfo = new CheckBox();
		private TextField txtOrderBy = new TextField();
		private CheckBox chkAbrirAuto = new CheckBox();
		private TextField txtTabelas = new TextField();
		private TextField txtApelido = new TextField();
		private CheckBox chkLinkAuto = new CheckBox();
		private TextField txtChaves = new TextField();
		private TextField txtGrupo = new TextField();
		private TextField txtJoins = new TextField();
		private CheckBox chkIgnorar = new CheckBox();
		private CheckBox chkCCSC = new CheckBox();
		private CheckBox chkSANE = new CheckBox();
		private CheckBox chkBPNT = new CheckBox();

		private PanelBanco() {
			chkAjusteAutoForm.setSelected(objeto.isAjusteAutoForm());
			chkAjusteLargForm.setSelected(objeto.isAjustarLargura());
			chkLarguraRotulos.setSelected(objeto.isLarguraRotulos());
			txtEsquemaAlter.setText(objeto.getEsquemaAlternativo());
			txtTabelaAlter.setText(objeto.getTabelaAlternativo());
			txtSelectAlter.setText(objeto.getSelectAlternativo());
			txtFinalConsulta.setText(objeto.getFinalConsulta());
			txtPrefixoNT.setText(objeto.getPrefixoNomeTabela());
			txtApelido.setText(objeto.getApelidoParaJoins());
			chkColunaInfo.setSelected(objeto.isColunaInfo());
			txtChaveamento.setText(objeto.getChaveamento());
			txtComplemento.setText(objeto.getComplemento());
			txtClassBiblio.setText(objeto.getClassBiblio());
			txtDestacaveis.setText(objeto.getDestacaveis());
			chkAbrirAuto.setSelected(objeto.isAbrirAuto());
			txtSequencias.setText(objeto.getSequencias());
			txtMapeamento.setText(objeto.getMapeamento());
			chkLinkAuto.setSelected(objeto.isLinkAuto());
			chkIgnorar.setSelected(objeto.isIgnorar());
			txtOrderBy.setText(objeto.getOrderBy());
			txtTabelas.setText(objeto.getTabelas());
			txtTabela.setText(objeto.getTabela());
			txtChaves.setText(objeto.getChaves());
			chkCCSC.setSelected(objeto.isCcsc());
			chkSANE.setSelected(objeto.isSane());
			chkBPNT.setSelected(objeto.isBpnt());
			txtGrupo.setText(objeto.getGrupo());
			txtJoins.setText(objeto.getJoins());
			txtFinalConsulta.addFocusListener(focusListenerInner);
			txtEsquemaAlter.addFocusListener(focusListenerInner);
			txtTabelaAlter.addFocusListener(focusListenerInner);
			txtChaveamento.addFocusListener(focusListenerInner);
			txtComplemento.addFocusListener(focusListenerInner);
			txtClassBiblio.addFocusListener(focusListenerInner);
			txtDestacaveis.addFocusListener(focusListenerInner);
			txtSelectAlter.addFocusListener(focusListenerInner);
			txtMapeamento.addFocusListener(focusListenerInner);
			txtSequencias.addFocusListener(focusListenerInner);
			txtPrefixoNT.addFocusListener(focusListenerInner);
			txtTabelas.addFocusListener(focusListenerInner);
			txtApelido.addFocusListener(focusListenerInner);
			txtOrderBy.addFocusListener(focusListenerInner);
			txtTabela.addFocusListener(focusListenerInner);
			txtChaves.addFocusListener(focusListenerInner);
			txtJoins.addFocusListener(focusListenerInner);
			txtGrupo.addFocusListener(focusListenerInner);
			chkAjusteAutoForm.addActionListener(this);
			chkAjusteLargForm.addActionListener(this);
			chkLarguraRotulos.addActionListener(this);
			txtFinalConsulta.addActionListener(this);
			txtEsquemaAlter.addActionListener(this);
			txtTabelaAlter.addActionListener(this);
			txtChaveamento.addActionListener(this);
			txtComplemento.addActionListener(this);
			txtClassBiblio.addActionListener(this);
			txtDestacaveis.addActionListener(this);
			txtSelectAlter.addActionListener(this);
			txtMapeamento.addActionListener(this);
			txtSequencias.addActionListener(this);
			chkColunaInfo.addActionListener(this);
			chkAbrirAuto.addActionListener(this);
			txtPrefixoNT.addActionListener(this);
			chkLinkAuto.addActionListener(this);
			txtOrderBy.addActionListener(this);
			txtTabelas.addActionListener(this);
			txtApelido.addActionListener(this);
			chkIgnorar.addActionListener(this);
			txtChaves.addActionListener(this);
			txtTabela.addActionListener(this);
			txtGrupo.addActionListener(this);
			txtJoins.addActionListener(this);
			chkCCSC.addActionListener(this);
			chkSANE.addActionListener(this);
			chkBPNT.addActionListener(this);
			Box container = Box.createVerticalBox();
			container.add(criarLinhaCopiarRotulo("label.apelido_para_joins", txtApelido));
			container.add(criarLinhaCopiar("label.grupo", txtGrupo));
			container.add(criarLinhaCopiar("label.tabela", txtTabela));
			container.add(criarLinhaCopiar("label.chaves", txtChaves, ObjetoMensagens.getString("hint.chaves")));
			container.add(criarLinhaCopiarRotulo("label.select_alter", txtSelectAlter));
			if (ObjetoPreferencia.isHabilitadoEsquemaTabelaAlter()) {
				container.add(criarLinhaCopiar("label.esquema_alter", txtEsquemaAlter));
				container.add(criarLinhaCopiar("label.tabela_alter", txtTabelaAlter,
						ObjetoMensagens.getString("hint.tabela_alter")));
			}
			if (ObjetoPreferencia.isHabilitadoInnerJoinsObjeto()) {
				container.add(criarLinhaCopiar("label.tabelas", txtTabelas));
				container.add(criarLinhaCopiar("label.joins", txtJoins, ObjetoMensagens.getString("hint.joins")));
			}
			container.add(criarLinhaCopiarRotulo("label.prefixo_nt", txtPrefixoNT));
			container.add(
					criarLinhaCopiar("label.sequencias", txtSequencias, ObjetoMensagens.getString("hint.sequencias")));
			container.add(criarLinhaCopiar("label.chaveamento", txtChaveamento,
					ObjetoMensagens.getString("hint.chaveamento")));
			container.add(
					criarLinhaCopiar("label.mapeamento", txtMapeamento, ObjetoMensagens.getString("hint.mapeamento")));
			container.add(criarLinhaCopiar("label.complemento", txtComplemento));
			container.add(criarLinhaCopiar("label.class_biblio", txtClassBiblio));
			container.add(criarLinhaCopiar("label.campos_destac", txtDestacaveis));
			container.add(criarLinhaCopiar("label.order_by", txtOrderBy));
			container.add(criarLinhaCopiarRotulo("label.final_consulta", txtFinalConsulta));
			container.add(criarLinhaRotulo("label.coluna_info", chkColunaInfo));
			container.add(criarLinha("label.abrir_auto", chkAbrirAuto));
			container.add(criarLinhaRotulo("label.link_auto", chkLinkAuto));
			container.add(criarLinhaRotulo("label.largura_rotulos", chkLarguraRotulos));
			container.add(criarLinha("label.sane", chkSANE, ObjetoMensagens.getString("hint.sane")));
			container.add(criarLinha("label.ccsc", chkCCSC, ObjetoMensagens.getString("hint.ccsc")));
			container.add(criarLinha("label.bpnt", chkBPNT, ObjetoMensagens.getString("hint.bpnt")));
			container.add(criarLinhaComLink(ObjetoMensagens.getString("label.ajuste_auto_form"), false,
					chkAjusteAutoForm, ObjetoMensagens.getString("hint.ajuste_auto_form"), null));
			container.add(criarLinhaComLink(ObjetoMensagens.getString("label.ajuste_larg_form"), false,
					chkAjusteLargForm, ObjetoMensagens.getString("hint.ajuste_larg_form"), null));
			container.add(criarLinha("label.ignorar", chkIgnorar, ObjetoMensagens.getString("hint.ignorar")));
			txtChaveamento.addMouseListener(chaveamentoListener);
			txtComplemento.addMouseListener(complementoListener);
			txtClassBiblio.addMouseListener(classBiblioListener);
			txtDestacaveis.addMouseListener(destacaveisListener);
			txtMapeamento.addMouseListener(mapeamentoListener);
			txtSequencias.addMouseListener(sequenciaListener);
			add(BorderLayout.CENTER, new ScrollPane(container));
			vincular();
		}

		private void vincular() {
			vinculados.add(new CompChave(chkLarguraRotulos, "LARGURA_ROTULOS"));
			vinculados.add(new CompChave(txtFinalConsulta, "FINAL_CONSULTA"));
			vinculados.add(new CompChave(chkAjusteAutoForm, "AJUSTE_AUTO"));
			vinculados.add(new CompChave(chkAjusteLargForm, "AJUSTE_LARG"));
			vinculados.add(new CompChave(txtEsquemaAlter, "ESQUEMA_ALTER"));
			vinculados.add(new CompChave(txtTabelaAlter, "TABELA_ALTER"));
			vinculados.add(new CompChave(txtSelectAlter, "SELECT_ALTER"));
			vinculados.add(new CompChave(txtChaveamento, "CHAVEAMENTO"));
			vinculados.add(new CompChave(txtComplemento, "COMPLEMENTO"));
			vinculados.add(new CompChave(txtClassBiblio, "CLASSBIBLIO"));
			vinculados.add(new CompChave(txtDestacaveis, "DESTACAVEIS"));
			vinculados.add(new CompChave(chkColunaInfo, "COLUNA_INFO"));
			vinculados.add(new CompChave(txtMapeamento, "MAPEAMENTO"));
			vinculados.add(new CompChave(txtPrefixoNT, "PREFIXO_NT"));
			vinculados.add(new CompChave(chkAbrirAuto, "ABRIR_AUTO"));
			vinculados.add(new CompChave(txtSequencias, "SEQUENCIA"));
			vinculados.add(new CompChave(chkLinkAuto, "LINK_AUTO"));
			vinculados.add(new CompChave(txtOrderBy, "ORDER_BY"));
			vinculados.add(new CompChave(txtApelido, "APELIDO"));
			vinculados.add(new CompChave(txtTabelas, "TABELAS"));
			vinculados.add(new CompChave(chkIgnorar, "IGNORAR"));
			vinculados.add(new CompChave(txtChaves, "CHAVES"));
			vinculados.add(new CompChave(txtJoins, "JOINS"));
			vinculados.add(new CompChave(txtGrupo, "GRUPO"));
			vinculados.add(new CompChave(chkSANE, "SANE"));
			vinculados.add(new CompChave(chkCCSC, "CCSC"));
			vinculados.add(new CompChave(chkBPNT, "BPNT"));

			chkAjusteAutoForm.addMouseListener(listenerVinculado);
			chkAjusteLargForm.addMouseListener(listenerVinculado);
			chkLarguraRotulos.addMouseListener(listenerVinculado);
			txtFinalConsulta.addMouseListener(listenerVinculado);
			txtEsquemaAlter.addMouseListener(listenerVinculado);
			txtTabelaAlter.addMouseListener(listenerVinculado);
			txtSelectAlter.addMouseListener(listenerVinculado);
			txtChaveamento.addMouseListener(listenerVinculado);
			txtComplemento.addMouseListener(listenerVinculado);
			txtClassBiblio.addMouseListener(listenerVinculado);
			txtDestacaveis.addMouseListener(listenerVinculado);
			txtSequencias.addMouseListener(listenerVinculado);
			txtMapeamento.addMouseListener(listenerVinculado);
			chkColunaInfo.addMouseListener(listenerVinculado);
			txtPrefixoNT.addMouseListener(listenerVinculado);
			chkAbrirAuto.addMouseListener(listenerVinculado);
			chkLinkAuto.addMouseListener(listenerVinculado);
			txtOrderBy.addMouseListener(listenerVinculado);
			txtTabelas.addMouseListener(listenerVinculado);
			txtApelido.addMouseListener(listenerVinculado);
			chkIgnorar.addMouseListener(listenerVinculado);
			txtChaves.addMouseListener(listenerVinculado);
			txtJoins.addMouseListener(listenerVinculado);
			txtGrupo.addMouseListener(listenerVinculado);
			chkSANE.addMouseListener(listenerVinculado);
			chkCCSC.addMouseListener(listenerVinculado);
			chkBPNT.addMouseListener(listenerVinculado);
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
					Dialog dialog = Util.getViewParentDialog(ObjetoContainer.this);
					MiscelaniaDialogo form = MiscelaniaDialogo.criar(dialog, objeto,
							MiscelaniaContainer.Tipo.CHAVEAMENTO);
					config(dialog, form);
					form.setVisible(true);
					txtChaveamento.setText(objeto.getChaveamento());
				}
			}
		};

		private transient MouseListener complementoListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					Dialog dialog = Util.getViewParentDialog(ObjetoContainer.this);
					MiscelaniaDialogo form = MiscelaniaDialogo.criar(dialog, objeto,
							MiscelaniaContainer.Tipo.COMPLEMENTO);
					config(dialog, form);
					form.setVisible(true);
					txtComplemento.setText(objeto.getComplemento());
				}
			}
		};

		private transient MouseListener classBiblioListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					Dialog dialog = Util.getViewParentDialog(ObjetoContainer.this);
					MiscelaniaDialogo form = MiscelaniaDialogo.criar(dialog, objeto,
							MiscelaniaContainer.Tipo.CLASSBIBLIO);
					config(dialog, form);
					form.setVisible(true);
					txtClassBiblio.setText(objeto.getClassBiblio());
				}
			}
		};

		private transient MouseListener destacaveisListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					Dialog dialog = Util.getViewParentDialog(ObjetoContainer.this);
					MiscelaniaDialogo form = MiscelaniaDialogo.criar(dialog, objeto,
							MiscelaniaContainer.Tipo.DESTACAVEIS);
					config(dialog, form);
					form.setVisible(true);
					txtDestacaveis.setText(objeto.getDestacaveis());
				}
			}
		};

		private void config(Window parent, Window child) {
			Util.configSizeLocation(parent, child, ObjetoContainer.this);
		}

		private transient MouseListener mapeamentoListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					Dialog dialog = Util.getViewParentDialog(ObjetoContainer.this);
					MiscelaniaDialogo form = MiscelaniaDialogo.criar(dialog, objeto,
							MiscelaniaContainer.Tipo.MAPEAMENTO);
					config(dialog, form);
					form.setVisible(true);
					txtMapeamento.setText(objeto.getMapeamento());
				}
			}
		};

		private transient MouseListener sequenciaListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					Dialog dialog = Util.getViewParentDialog(ObjetoContainer.this);
					MiscelaniaDialogo form = MiscelaniaDialogo.criar(dialog, objeto,
							MiscelaniaContainer.Tipo.SEQUENCIA);
					config(dialog, form);
					form.setVisible(true);
					txtSequencias.setText(objeto.getSequencias());
				}
			}
		};

		@Override
		public void actionPerformed(ActionEvent e) {
			AtomicBoolean atom = new AtomicBoolean(false);
			if (txtChaveamento == e.getSource()) {
				objeto.setChaveamento(txtChaveamento.getText());
				atom.set(true);
			} else if (txtMapeamento == e.getSource()) {
				objeto.setMapeamento(txtMapeamento.getText());
				atom.set(true);
			} else if (txtFinalConsulta == e.getSource()) {
				objeto.setFinalConsulta(txtFinalConsulta.getText());
			} else if (txtComplemento == e.getSource()) {
				objeto.setComplemento(txtComplemento.getText());
			} else if (txtClassBiblio == e.getSource()) {
				objeto.setClassBiblio(txtClassBiblio.getText());
				atom.set(true);
			} else if (txtDestacaveis == e.getSource()) {
				objeto.setDestacaveis(txtDestacaveis.getText());
			} else if (txtOrderBy == e.getSource()) {
				objeto.setOrderBy(txtOrderBy.getText());
			} else if (txtGrupo == e.getSource()) {
				objeto.setGrupo(txtGrupo.getText());
			} else if (txtApelido == e.getSource()) {
				objeto.setApelidoParaJoins(txtApelido.getText());
			} else if (txtTabela == e.getSource()) {
				objeto.setTabela(txtTabela.getText());
			} else if (txtPrefixoNT == e.getSource()) {
				objeto.setPrefixoNomeTabela(txtPrefixoNT.getText());
			} else if (txtChaves == e.getSource()) {
				objeto.setChaves(txtChaves.getText());
			} else if (txtSequencias == e.getSource()) {
				objeto.setSequencias(txtSequencias.getText());
				atom.set(true);
			}
			actionPerformedCont(e);
			objetoSuperficie.repaint();
			if (atom.get()) {
				ObjetoSuperficieUtil.configuracaoDinamica(objetoSuperficie, ObjetoContainer.this, objeto);
			}
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
			} else if (chkLarguraRotulos == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setLarguraRotulos(chk.isSelected());
				MacroProvedor.larguraRotulos(chk.isSelected());
			} else if (chkCCSC == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setCcsc(chk.isSelected());
				MacroProvedor.confirmarCsc(chk.isSelected());
			} else if (chkIgnorar == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setIgnorar(chk.isSelected());
				MacroProvedor.ignorar(chk.isSelected());
			} else if (chkSANE == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setSane(chk.isSelected());
				MacroProvedor.semArgNaoExec(chk.isSelected());
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
			} else if (chkAjusteLargForm == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setAjustarLargura(chk.isSelected());
				MacroProvedor.ajusteLargForm(chk.isSelected());
			} else if (txtTabelas == e.getSource()) {
				objeto.setTabelas(txtTabelas.getText());
			} else if (txtEsquemaAlter == e.getSource()) {
				objeto.setEsquemaAlternativo(txtEsquemaAlter.getText());
			} else if (txtTabelaAlter == e.getSource()) {
				objeto.setTabelaAlternativo(txtTabelaAlter.getText());
			} else if (txtSelectAlter == e.getSource()) {
				objeto.setSelectAlternativo(txtSelectAlter.getText());
			} else if (txtJoins == e.getSource()) {
				objeto.setJoins(txtJoins.getText());
			}
		}
	}

	private Panel criarLinhaCopiar(String chaveRotulo, TextField textField) {
		return criarLinhaCopiar(chaveRotulo, textField, null);
	}

	private Panel criarLinhaCopiarRotulo(String chaveRotulo, TextField textField) {
		return criarLinhaCopiarRotulo(chaveRotulo, textField, null);
	}

	private Panel criarLinhaCopiar(String chaveRotulo, TextField textField, String hint) {
		Panel panel = criarLinha(chaveRotulo, textField, hint);
		panel.add(BorderLayout.EAST, new PanelCopiarColar(textField));
		return panel;
	}

	private Panel criarLinhaCopiarRotulo(String chaveRotulo, TextField textField, String hint) {
		Panel panel = criarLinhaComLink(ObjetoMensagens.getString(chaveRotulo), false, textField, hint, null);
		panel.add(BorderLayout.EAST, new PanelCopiarColar(textField));
		return panel;
	}

	private Panel criarLinha(String chaveRotulo, JComponent componente) {
		return criarLinha(chaveRotulo, componente, null);
	}

	private Panel criarLinhaRotulo(String chaveRotulo, JComponent componente) {
		return criarLinhaComLink(ObjetoMensagens.getString(chaveRotulo), false, componente, null, null);
	}

	private Panel criarLinha(String chaveRotulo, JComponent componente, String hint) {
		return criarLinhaComLink(chaveRotulo, true, componente, hint, null);
	}

	private Panel criarLinhaComLink(String rotulo, boolean chaveRotulo, JComponent componente, String hint,
			LabelLinkListener linkListener) {
		Dimension largura = new Dimension(150, 0);
		Panel linha = new Panel();
		Label label = new Label(rotulo, chaveRotulo);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setPreferredSize(largura);
		label.setMinimumSize(largura);
		label.setMaximumSize(largura);
		if (!Util.isEmpty(hint)) {
			label.setToolTipText(hint);
		}
		linha.add(BorderLayout.CENTER, componente);
		linha.add(BorderLayout.WEST, label);
		if (linkListener != null) {
			label.modoLink(linkListener);
		}
		return linha;
	}

	private class PanelCopiarColar extends Panel {
		private final Action copiar = Action.actionIcon("label.copiar", Icones.COPIA);
		private final ButtonColar buttonColar = new ButtonColar();
		private LabelTextTemp lblMsg = new LabelTextTemp();
		private static final long serialVersionUID = 1L;
		private final TextField textField;

		private PanelCopiarColar(TextField textField) {
			this.textField = textField;
			add(BorderLayout.WEST, new Button(copiar));
			add(BorderLayout.CENTER, lblMsg);
			add(BorderLayout.EAST, buttonColar);
			copiar.setActionListener(e -> copiar());
		}

		private void copiar() {
			String string = Util.getString(textField);
			if (!Util.isEmpty(string)) {
				Util.setContentTransfered(string);
				lblMsg.mensagemChave("msg.copiado");
			}
		}

		private class ButtonColar extends ButtonPopup {
			private Action desativarHoraAcao = acaoMenu("label.desativar_diff_em_horas");
			private Action ativarHoraAcao = acaoMenu("label.ativar_diff_em_horas");
			private Action diffHoraAcao = acaoMenu("label.diferenca_em_horas");
			private Action numeroAcao = actionMenu("label.numeros");
			private Action letraAcao = actionMenu("label.letras");
			private Action todosAcao = actionMenu("label.todos");
			private Action horaAcao = actionMenu("label.hora");
			private static final long serialVersionUID = 1L;

			private ButtonColar() {
				super("label.colar", Icones.COLAR);
				addMenuItem(numeroAcao);
				addMenuItem(letraAcao);
				addMenuItem(todosAcao);
				numeroAcao.setActionListener(e -> colar(true, false));
				letraAcao.setActionListener(e -> colar(false, true));
				todosAcao.setActionListener(e -> colar(false, false));
			}

			private void colar(boolean numeros, boolean letras) {
				Util.getContentTransfered(textField, numeros, letras);
				textField.postActionEvent();
			}

			private void configHora() {
				addMenuItem(true, horaAcao);
				addMenuItem(diffHoraAcao);
				addMenuItem(ativarHoraAcao);
				addMenuItem(desativarHoraAcao);
				horaAcao.setActionListener(e -> {
					textField.setText(HoraUtil.getHoraAtual());
					textField.postActionEvent();
					List<Relacao> lista = ObjetoSuperficieUtil.getRelacoes(objetoSuperficie, objeto);
					if (lista.size() == 1 && Util.confirmar(ObjetoContainer.this,
							ObjetoMensagens.getString("msg.calcular_diferenca_em_horas"), false)) {
						Relacao relacao = lista.get(0);
						relacao.processarHoraDiff(false);
						objetoSuperficie.repaint();
					} else if (lista.size() == 1 && Util.confirmar(ObjetoContainer.this,
							ObjetoMensagens.getString("msg.ativar_diff_em_horas"), false)) {
						ativarHoraAcao.actionPerformed(null);
					}
				});
				diffHoraAcao.setActionListener(e -> {
					List<Relacao> lista = ObjetoSuperficieUtil.getRelacoes(objetoSuperficie, objeto);
					if (lista.size() == 1) {
						Relacao relacao = lista.get(0);
						relacao.processarHoraDiff(false);
						objetoSuperficie.repaint();
					}
				});
				ativarHoraAcao.setActionListener(e -> {
					List<Relacao> lista = ObjetoSuperficieUtil.getRelacoes(objetoSuperficie, objeto);
					if (lista.size() == 1) {
						Relacao relacao = lista.get(0);
						relacao.setProcessar(true);
						relacao.ativar();
						objetoSuperficie.repaint();
					}
				});
				desativarHoraAcao.setActionListener(e -> {
					List<Relacao> lista = ObjetoSuperficieUtil.getRelacoes(objetoSuperficie, objeto);
					if (lista.size() == 1) {
						Relacao relacao = lista.get(0);
						relacao.desativar();
						objetoSuperficie.repaint();
					}
				});
			}
		}
	}

	private class PanelDescricao extends Panel {
		private final TextArea textArea = new TextArea();
		private static final long serialVersionUID = 1L;
		private final Toolbar toolbar = new Toolbar();

		private PanelDescricao() {
			textArea.setText(objeto.getDescricao());
			textArea.addKeyListener(keyListenerInner);
			add(BorderLayout.CENTER, textArea);
			add(BorderLayout.NORTH, toolbar);
		}

		private transient KeyListener keyListenerInner = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				objeto.setDescricao(textArea.getText());
			}
		};

		private class Toolbar extends BarraButton {
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(new Nil(), COPIAR, COLAR);
			}

			@Override
			protected void copiar() {
				String string = Util.getString(textArea.getTextAreaInner());
				Util.setContentTransfered(string);
				copiarMensagem(string);
				textArea.requestFocus();
			}

			@Override
			protected void colar(boolean numeros, boolean letras) {
				Util.getContentTransfered(textArea.getTextAreaInner(), numeros, letras);
			}
		}
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
			private Action actionCorFonteVinculo = acaoIcon(labelVinculo, Icones.SUCESSO);
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(new Nil(), COPIAR, COLAR0, APLICAR);
				aplicarAcao.text(ObjetoMensagens.getString("label.reaplicar_macro"));
				addButton(actionCorFonteVinculo);
				actionCorFonteVinculo.setActionListener(e -> corFonteVinculo());
			}

			private void corFonteVinculo() {
				if (Util.isEmpty(txtTabela.getText())) {
					Util.mensagem(ObjetoContainer.this, ObjetoMensagens.getString(chaveMensagem));
					return;
				}
				Vinculacao vinculacao = null;
				try {
					vinculacao = ObjetoSuperficieUtil.getVinculacao(objetoSuperficie);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("VINCULAR EM COR FONTE", ex, ObjetoContainer.this);
					return;
				}
				String tabela = txtTabela.getText().trim();
				ParaTabela para = vinculacao.getParaTabela(tabela);
				if (para == null) {
					para = new ParaTabela(tabela);
					vinculacao.putParaTabela(para);
				}
				para.setCorFonte(colorChooser.getColor());
				salvarVinculacao(vinculacao);
			}

			@Override
			protected void copiar() {
				Preferencias.setCorFonteCopiado(colorChooser.getColor());
				copiarMensagem(".");
			}

			@Override
			protected void colar0() {
				objeto.setCorFonte(Preferencias.getCorFonteCopiado());
				MacroProvedor.corFonte(objeto.getCorFonte());
				colorChooser.setColor(objeto.getCorFonte());
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
			colorChooser = new JColorChooser(objeto.getCor());
			colorChooser.getSelectionModel().addChangeListener(this);
			add(BorderLayout.CENTER, colorChooser);
			add(BorderLayout.NORTH, toolbar);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			objeto.setCor(colorChooser.getColor());
			MacroProvedor.corFundo(objeto.getCor());
			objetoSuperficie.repaint();
		}

		private class Toolbar extends BarraButton {
			private Action actionCorVinculo = acaoIcon(labelVinculo, Icones.SUCESSO);
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(new Nil(), COPIAR, COLAR0, APLICAR);
				aplicarAcao.text(ObjetoMensagens.getString("label.reaplicar_macro"));
				addButton(actionCorVinculo);
				actionCorVinculo.setActionListener(e -> corVinculo());
			}

			private void corVinculo() {
				if (Util.isEmpty(txtTabela.getText())) {
					Util.mensagem(ObjetoContainer.this, ObjetoMensagens.getString(chaveMensagem));
					return;
				}
				Vinculacao vinculacao = null;
				try {
					vinculacao = ObjetoSuperficieUtil.getVinculacao(objetoSuperficie);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("VINCULAR EM COR FUNDO", ex, ObjetoContainer.this);
					return;
				}
				String tabela = txtTabela.getText().trim();
				ParaTabela para = vinculacao.getParaTabela(tabela);
				if (para == null) {
					para = new ParaTabela(tabela);
					vinculacao.putParaTabela(para);
				}
				para.setCorFundo(colorChooser.getColor());
				salvarVinculacao(vinculacao);
			}

			@Override
			protected void copiar() {
				Preferencias.setCorCopiado(colorChooser.getColor());
				copiarMensagem(".");
			}

			@Override
			protected void colar0() {
				objeto.setCor(Preferencias.getCorCopiado());
				MacroProvedor.corFundo(objeto.getCor());
				colorChooser.setColor(objeto.getCor());
				objetoSuperficie.repaint();
			}

			@Override
			protected void aplicar() {
				stateChanged(null);
			}
		}
	}

	private class IconeListener extends MouseAdapter {
		private Action action = acaoMenu(labelVinculo);
		private Popup popup = new Popup();
		private final Objeto objeto;
		private final Label label;

		private IconeListener(Objeto objeto, Label label) {
			this.objeto = objeto;
			this.label = label;
			popup.add(action);
			action.setActionListener(e -> configIconeVinculo());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() < Constantes.DOIS) {
				return;
			}
			Dialog dialog = Util.getViewParentDialog(ObjetoContainer.this);
			IconeDialogo form = IconeDialogo.criar(dialog, objeto, label);
			Util.configSizeLocation(dialog, form, ObjetoContainer.this);
			form.setVisible(true);
			objetoSuperficie.repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			if (e.isPopupTrigger() && !Util.isEmpty(objeto.getIcone())) {
				popup.show(label, e.getX(), e.getY());
			}
		}

		private void configIconeVinculo() {
			if (Util.isEmpty(txtTabela.getText())) {
				Util.mensagem(ObjetoContainer.this, ObjetoMensagens.getString(chaveMensagem));
				return;
			}
			Vinculacao vinculacao = null;
			try {
				vinculacao = ObjetoSuperficieUtil.getVinculacao(objetoSuperficie);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("VINCULAR EM ICONE", ex, ObjetoContainer.this);
				return;
			}
			String tabela = txtTabela.getText().trim();
			ParaTabela para = vinculacao.getParaTabela(tabela);
			if (para == null) {
				para = new ParaTabela(tabela);
				vinculacao.putParaTabela(para);
			}
			para.setIcone(objeto.getIcone());
			salvarVinculacao(vinculacao);
		}
	}

	private void salvarVinculacao(Vinculacao vinculacao) {
		ObjetoSuperficieUtil.salvarVinculacao(objetoSuperficie, vinculacao);
	}

	Action acaoMenu(String chave, Icon icon) {
		return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
	}

	Action acaoMenu(String chave) {
		return acaoMenu(chave, null);
	}

	Action acaoIcon(String chave, Icon icon) {
		return Action.acaoIcon(ObjetoMensagens.getString(chave), icon);
	}

	Action acaoIcon(String chave) {
		return acaoIcon(chave, null);
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