package br.com.persist.plugins.objeto.config;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR0;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
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

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
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
import br.com.persist.plugins.objeto.macro.MacroProvedor;
import br.com.persist.plugins.objeto.vinculo.ParaTabela;

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
			container.add(criarLinhaCopiarRotulo("label.id", txtId));
			container.add(criarLinha("label.x", txtX));
			container.add(criarLinha("label.y", txtY));
			container.add(criarLinhaRotulo("label.desloc_x_id", txtDeslocXId));
			container.add(criarLinhaRotulo("label.desloc_y_id", txtDeslocYId));
			container.add(criarLinhaCopiar("label.intervalo", txtIntervalo));
			container.add(criarLinhaComLinkCopiar("label.arquivo", txtArquivo,
					ObjetoMensagens.getString("hint.arquivo_absoluto_relativo"),
					PanelGeral.this::mensagemPropriedadeArquivo));
			container.add(criarLinha("label.desenhar_id", chkDesenharId));
			container.add(criarLinha("label.transparente", chkTransparente));
			container.add(criarLinhaRotulo("label.copiar_destacado", chkCopiarDestac));
			add(BorderLayout.CENTER, new ScrollPane(container));
		}

		private void mensagemPropriedadeArquivo(Label label) {
			Util.mensagem(ObjetoContainer.this, ObjetoMensagens.getString("msg.propriedade_arquivo"));
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

		private Panel criarLinhaComLinkCopiar(String chaveRotulo, TextField textField, String hint,
				LabelLinkListener linkListener) {
			Panel panel = criarLinhaComLink(chaveRotulo, true, textField, hint, linkListener);
			panel.add(BorderLayout.EAST, new PanelCopiarColar(textField));
			return panel;
		}
	}

	private class PanelBanco extends Panel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private transient List<CompChave> vinculados = new ArrayList<>();
		private VinculadoPopup popupVinculo = new VinculadoPopup();
		private TextField txtFinalConsulta = new TextField();
		private CheckBox chkAjusteAutoForm = new CheckBox();
		private TextField txtChaveamento = new TextField();
		private TextField txtSelectAlter = new TextField();
		private TextField txtComplemento = new TextField();
		private TextField txtSequencias = new TextField();
		private TextField txtMapeamento = new TextField();
		private TextField txtPrefixoNT = new TextField();
		private CheckBox chkColunaInfo = new CheckBox();
		private TextField txtOrderBy = new TextField();
		private CheckBox chkAbrirAuto = new CheckBox();
		private TextField txtTabelas = new TextField();
		private TextField txtApelido = new TextField();
		private CheckBox chkLinkAuto = new CheckBox();
		private TextField txtTabela = new TextField();
		private TextField txtChaves = new TextField();
		private TextField txtGrupo = new TextField();
		private TextField txtJoins = new TextField();
		private CheckBox chkCCSC = new CheckBox();
		private CheckBox chkSANE = new CheckBox();
		private CheckBox chkBPNT = new CheckBox();

		private PanelBanco() {
			chkAjusteAutoForm.setSelected(objeto.isAjusteAutoForm());
			txtSelectAlter.setText(objeto.getSelectAlternativo());
			txtFinalConsulta.setText(objeto.getFinalConsulta());
			txtPrefixoNT.setText(objeto.getPrefixoNomeTabela());
			txtApelido.setText(objeto.getApelidoParaJoins());
			chkColunaInfo.setSelected(objeto.isColunaInfo());
			txtChaveamento.setText(objeto.getChaveamento());
			txtComplemento.setText(objeto.getComplemento());
			chkAbrirAuto.setSelected(objeto.isAbrirAuto());
			txtSequencias.setText(objeto.getSequencias());
			txtMapeamento.setText(objeto.getMapeamento());
			chkLinkAuto.setSelected(objeto.isLinkAuto());
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
			txtChaveamento.addFocusListener(focusListenerInner);
			txtComplemento.addFocusListener(focusListenerInner);
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
			txtOrderBy.addActionListener(this);
			txtTabelas.addActionListener(this);
			txtApelido.addActionListener(this);
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
			container.add(criarLinhaCopiar("label.order_by", txtOrderBy));
			container.add(criarLinhaCopiarRotulo("label.final_consulta", txtFinalConsulta));
			container.add(criarLinhaRotulo("label.coluna_info", chkColunaInfo));
			container.add(criarLinha("label.abrir_auto", chkAbrirAuto));
			container.add(criarLinhaRotulo("label.link_auto", chkLinkAuto));
			container.add(criarLinha("label.sane", chkSANE, ObjetoMensagens.getString("hint.sane")));
			container.add(criarLinha("label.ccsc", chkCCSC, ObjetoMensagens.getString("hint.ccsc")));
			container.add(criarLinha("label.bpnt", chkBPNT, ObjetoMensagens.getString("hint.bpnt")));
			container.add(criarLinhaComLink(ObjetoMensagens.getString("label.ajuste_auto_form"), false,
					chkAjusteAutoForm, ObjetoMensagens.getString("hint.ajuste_auto_form"), null));
			txtChaveamento.addMouseListener(chaveamentoListener);
			txtMapeamento.addMouseListener(mapeamentoListener);
			txtSequencias.addMouseListener(sequenciaListener);
			add(BorderLayout.CENTER, new ScrollPane(container));
			vincular();
		}

		private void vincular() {
			vinculados.add(new CompChave(txtApelido, "APELIDO"));
			vinculados.add(new CompChave(txtGrupo, "GRUPO"));
			vinculados.add(new CompChave(txtChaves, "CHAVES"));
			vinculados.add(new CompChave(txtSelectAlter, "SELECT_ALTER"));
			vinculados.add(new CompChave(txtPrefixoNT, "PREFIXO_NT"));
			vinculados.add(new CompChave(txtSequencias, "SEQUENCIA"));
			vinculados.add(new CompChave(txtChaveamento, "CHAVEAMENTO"));
			vinculados.add(new CompChave(txtMapeamento, "MAPEAMENTO"));
			vinculados.add(new CompChave(txtComplemento, "COMPLEMENTO"));
			vinculados.add(new CompChave(txtOrderBy, "ORDER_BY"));
			vinculados.add(new CompChave(txtFinalConsulta, "FINAL_CONSULTA"));
			vinculados.add(new CompChave(chkColunaInfo, "COLUNA_INFO"));
			vinculados.add(new CompChave(chkAbrirAuto, "ABRIR_AUTO"));
			vinculados.add(new CompChave(chkLinkAuto, "LINK_AUTO"));
			vinculados.add(new CompChave(chkSANE, "SANE"));
			vinculados.add(new CompChave(chkCCSC, "CCSC"));
			vinculados.add(new CompChave(chkBPNT, "BPNT"));
			vinculados.add(new CompChave(chkAjusteAutoForm, "AJUSTE_AUTO"));

			txtApelido.addMouseListener(listenerVinculado);
			txtGrupo.addMouseListener(listenerVinculado);
			txtChaves.addMouseListener(listenerVinculado);
			txtSelectAlter.addMouseListener(listenerVinculado);
			txtPrefixoNT.addMouseListener(listenerVinculado);
			txtSequencias.addMouseListener(listenerVinculado);
			txtChaveamento.addMouseListener(listenerVinculado);
			txtMapeamento.addMouseListener(listenerVinculado);
			txtComplemento.addMouseListener(listenerVinculado);
			txtOrderBy.addMouseListener(listenerVinculado);
			txtFinalConsulta.addMouseListener(listenerVinculado);
			chkColunaInfo.addMouseListener(listenerVinculado);
			chkAbrirAuto.addMouseListener(listenerVinculado);
			chkLinkAuto.addMouseListener(listenerVinculado);
			chkSANE.addMouseListener(listenerVinculado);
			chkCCSC.addMouseListener(listenerVinculado);
			chkBPNT.addMouseListener(listenerVinculado);
			chkAjusteAutoForm.addMouseListener(listenerVinculado);
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
				if (e.isPopupTrigger() && !Util.estaVazio(txtTabela.getText())) {
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

		Action actionMenu(String chave, Icon icon) {
			return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
		}

		Action actionMenu(String chave) {
			return actionMenu(chave, null);
		}

		private class VinculadoPopup extends Popup {
			private static final long serialVersionUID = 1L;
			private Action action = actionMenu("label.aplicar_arq_vinculo");
			private transient CompChave compChave;

			private VinculadoPopup() {
				add(action);
				action.setActionListener(e -> processar());
			}

			void processar() {
				if (compChave == null) {
					return;
				}
				ParaTabela para = null;
				if (para == null) {
					return;
				}
				if ("APELIDO".equals(compChave.chave)) {
					para.setApelido(compChave.getText());
				} else if ("GRUPO".equals(compChave.chave)) {
					para.setGrupo(compChave.getText());
				} else if ("CHAVES".equals(compChave.chave)) {
					para.setChaves(compChave.getText());
				} else if ("SELECT_ALTER".equals(compChave.chave)) {
					para.setSelectAlternativo(compChave.getText());
				} else if ("PREFIXO_NT".equals(compChave.chave)) {
					para.setPrefixoNomeTabela(compChave.getText());
				} else if ("SEQUENCIA".equals(compChave.chave)) {
					para.setSequencias(compChave.getText());
				} else if ("CHAVEAMENTO".equals(compChave.chave)) {
					para.setCampoNomes(compChave.getText());
				} else if ("MAPEAMENTO".equals(compChave.chave)) {
					para.setMapeamento(compChave.getText());
				} else if ("COMPLEMENTO".equals(compChave.chave)) {
					para.setComplemento(compChave.getText());
				}
				processar(para);
			}

			void processar(ParaTabela para) {
				if ("ORDER_BY".equals(compChave.chave)) {
					para.setOrderBy(compChave.getText());
				} else if ("FINAL_CONSULTA".equals(compChave.chave)) {
					para.setFinalConsulta(compChave.getText());
				} else if ("COLUNA_INFO".equals(compChave.chave)) {
					para.setColunaInfo(compChave.getBool());
				} else if ("ABRIR_AUTO".equals(compChave.chave)) {
					para.setDestacavel(compChave.getBool());
				} else if ("LINK_AUTO".equals(compChave.chave)) {
					para.setLinkAuto(compChave.getBool());
				} else if ("SANE".equals(compChave.chave)) {
					para.setSane(compChave.getBool());
				} else if ("CCSC".equals(compChave.chave)) {
					para.setCcsc(compChave.getBool());
				} else if ("BPNT".equals(compChave.chave)) {
					para.setBpnt(compChave.getBool());
				} else if ("AJUSTE_AUTO".equals(compChave.chave)) {
					para.setAjustarAltura(compChave.getBool());
				}
			}

			boolean showValido() {
				if (compChave == null) {
					return false;
				}
				if (compChave.comp instanceof TextField) {
					return !Util.estaVazio(((TextField) compChave.comp).getText());
				} else if (compChave.comp instanceof CheckBox) {
					return true;
				}
				return false;
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
			if (txtChaveamento == e.getSource()) {
				objeto.setChaveamento(txtChaveamento.getText());
			} else if (txtMapeamento == e.getSource()) {
				objeto.setMapeamento(txtMapeamento.getText());
			} else if (txtFinalConsulta == e.getSource()) {
				objeto.setFinalConsulta(txtFinalConsulta.getText());
			} else if (txtComplemento == e.getSource()) {
				objeto.setComplemento(txtComplemento.getText());
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
			} else if (txtTabelas == e.getSource()) {
				objeto.setTabelas(txtTabelas.getText());
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
		Dimension largura = new Dimension(120, 0);
		Panel linha = new Panel();
		Label label = new Label(rotulo, chaveRotulo);
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

	private class PanelCopiarColar extends Panel {
		private static final long serialVersionUID = 1L;
		private final Action copiar = Action.actionIcon("label.copiar", Icones.COPIA);
		private LabelTextTemp lblMsg = new LabelTextTemp();
		private final TextField textField;

		private PanelCopiarColar(TextField textField) {
			this.textField = textField;
			add(BorderLayout.WEST, new Button(copiar));
			add(BorderLayout.CENTER, lblMsg);
			add(BorderLayout.EAST, new ButtonColar());
			copiar.setActionListener(e -> copiar());
		}

		private void copiar() {
			String string = Util.getString(textField);
			if (!Util.estaVazio(string)) {
				Util.setContentTransfered(string);
				lblMsg.mensagemChave("msg.copiado");
			}
		}

		private class ButtonColar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action numeroAcao = Action.actionMenu("label.numeros", null);
			private Action letraAcao = Action.actionMenu("label.letras", null);
			private Action todosAcao = Action.actionMenu("label.todos", null);

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
		}
	}

	private class PanelDescricao extends Panel {
		private static final long serialVersionUID = 1L;
		private final TextArea textArea = new TextArea();
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
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(null, COPIAR, COLAR0, APLICAR);
				aplicarAcao.text(ObjetoMensagens.getString("label.reaplicar_macro"));
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
			private static final long serialVersionUID = 1L;

			private Toolbar() {
				super.ini(null, COPIAR, COLAR0, APLICAR);
				aplicarAcao.text(ObjetoMensagens.getString("label.reaplicar_macro"));
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
		private final Objeto objeto;
		private final Label label;

		private IconeListener(Objeto objeto, Label label) {
			this.objeto = objeto;
			this.label = label;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			Dialog dialog = Util.getViewParentDialog(ObjetoContainer.this);
			IconeDialogo form = IconeDialogo.criar(dialog, objeto, label);
			Util.configSizeLocation(dialog, form, ObjetoContainer.this);
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