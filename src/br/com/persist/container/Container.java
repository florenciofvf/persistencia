package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.componente.ToggleButton;
import br.com.persist.conexao.Conexao;
import br.com.persist.consulta.ConsultaDialogo;
import br.com.persist.consulta.ConsultaFormulario;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Fichario.InfoConexao;
import br.com.persist.icone.Icones;
import br.com.persist.metadado.Metadado;
import br.com.persist.objeto.Objeto;
import br.com.persist.principal.Formulario;
import br.com.persist.superficie.Superficie;
import br.com.persist.update.UpdateDialogo;
import br.com.persist.update.UpdateFormulario;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.ButtonPadrao1;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;
import br.com.persist.xml.XMLColetor;

public class Container extends Panel implements Fichario.IFicharioSalvar, Fichario.IFicharioConexao {
	private static final long serialVersionUID = 1L;
	private final ToggleButton btnArrasto = new ToggleButton(new ArrastoAcao());
	private final ToggleButton btnRotulos = new ToggleButton(new RotulosAcao());
	private final ToggleButton btnRelacao = new ToggleButton(new RelacaoAcao());
	private final ToggleButton btnSelecao = new ToggleButton(new SelecaoAcao());
	private ContainerFormulario containerFormulario;
	private boolean abortarFecharComESCSuperficie;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;
	private final Formulario formulario;
	private final Superficie superficie;
	private String conexaoFile;
	private File arquivo;

	public Container(Formulario formulario, IJanela janela) {
		cmbConexao = Util.criarComboConexao(formulario, null);
		superficie = new Superficie(formulario, this);
		this.formulario = formulario;
		toolbar.ini(janela);
		montarLayout();
		eventos();
	}

	private void eventos() {
		cmbConexao.addItemListener(e -> {
			if (ItemEvent.SELECTED == e.getStateChange()) {
				superficie.selecionarConexao(getConexaoPadrao());
			}
		});
	}

	@Override
	public void selecionarConexao(Conexao conexao) {
		if (conexao != null) {
			cmbConexao.setSelectedItem(conexao);
			superficie.selecionarConexao(conexao);
		}
	}

	@Override
	public InfoConexao getInfoConexao() {
		Conexao conexao = getConexaoPadrao();
		String conexaoAtual = conexao == null ? "null" : conexao.getNome();
		String nomeAba = arquivo == null ? "null" : arquivo.getAbsolutePath();
		return new InfoConexao(conexaoAtual, conexaoFile == null ? "null" : conexaoFile, nomeAba);
	}

	@Override
	public File getFileSalvarAberto() {
		return getArquivo();
	}

	public Superficie getSuperficie() {
		return superficie;
	}

	public Formulario getFormulario() {
		return formulario;
	}

	private void montarLayout() {
		ButtonGroup grupo = new ButtonGroup();
		add(BorderLayout.CENTER, new ScrollPane(superficie));
		add(BorderLayout.NORTH, toolbar);
		grupo.add(btnRotulos);
		grupo.add(btnArrasto);
		grupo.add(btnRelacao);
		grupo.add(btnSelecao);
	}

	public Conexao getConexaoPadrao() {
		return (Conexao) cmbConexao.getSelectedItem();
	}

	public void estadoSelecao() {
		btnSelecao.click();
	}

	public void abrirExportacaoImportacaoMetadado(Metadado metadado, boolean exportacao, boolean circular) {
		if (abortarFecharComESCSuperficie) {
			superficie.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
		}

		superficie.abrirExportacaoImportacaoMetadado(metadado, exportacao, circular);
		btnSelecao.click();
	}

	public void exportarMetadadoRaiz(Metadado metadado) {
		if (abortarFecharComESCSuperficie) {
			superficie.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
		}

		superficie.exportarMetadadoRaiz(metadado);
		btnSelecao.click();
	}

	public void abrir(File file, XMLColetor coletor, Graphics g, ConfigArquivo config) {
		if (abortarFecharComESCSuperficie) {
			superficie.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
		}

		superficie.setAjusteAutomaticoForm(coletor.getAjusteAutoForm().get());
		toolbar.chkAjusteAutom.setSelected(coletor.getAjusteAutoForm().get());
		superficie.abrir(coletor);
		Conexao conexaoSel = null;
		arquivo = file;
		btnSelecao.click();

		if (!Util.estaVazio(coletor.getSbConexao().toString())) {
			conexaoFile = coletor.getSbConexao().toString();

			for (int i = 0; i < cmbConexao.getItemCount(); i++) {
				Conexao c = cmbConexao.getItemAt(i);

				if (conexaoFile.equalsIgnoreCase(c.getNome())) {
					conexaoSel = c;
					break;
				}
			}

			if (conexaoSel != null) {
				cmbConexao.setSelectedItem(conexaoSel);
			}
		}

		Conexao conexao = getConexaoPadrao();

		if (conexao == null) {
			return;
		}

		if (conexaoSel != null && conexaoSel.equals(conexao)) {
			adicionarForm(conexao, coletor, g, config);
		}
	}

	private void adicionarForm(Conexao conexao, XMLColetor coletor, Graphics g, ConfigArquivo config) {
		for (Form form : coletor.getForms()) {
			Objeto instancia = null;

			for (Objeto objeto : coletor.getObjetos()) {
				if (form.getObjeto().equals(objeto.getId())) {
					instancia = objeto;
				}
			}

			if (instancia != null) {
				Object[] array = Util.criarArray(conexao, instancia, new Dimension(form.getLargura(), form.getAltura()),
						form.getApelido());
				superficie.addForm(array, new Point(form.getX(), form.getY()), g, (String) array[Util.ARRAY_INDICE_APE],
						true, config);
			}
		}
	}

	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action salvarComoAcao = Action.actionIcon("label.salvar_como", Icones.SALVARC);
		private Action desenharDescAcao = Action.actionIcon("label.desenhar_desc", Icones.TAG);
		private Action desenharIdAcao = Action.actionIcon("label.desenhar_id", Icones.LABEL);
		private Action criarObjAcao = Action.actionIcon("label.criar_objeto", Icones.CRIAR);
		private Action transpAcao = Action.actionIcon("label.transparente", Icones.RECT);
		private Action excluirAcao = Action.actionIcon("label.excluir", Icones.EXCLUIR);
		private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);
		private Action colarAcao = Action.actionIcon("label.colar", Icones.COLAR);
		private TextField txtPrefixoNomeTabela = new TextField(10);
		private CheckBox chkAjusteAutom = new CheckBox();
		private Label labelStatus = new Label();

		public void ini(IJanela janela) {
			super.ini(janela, false, true);
			configBaixarAcao(null);

			addButton(salvarComoAcao);
			addButton(true, copiarAcao);
			addButton(colarAcao);
			add(true, new ButtonDestacar());
			add(true, new ButtonConsulta());
			add(true, new ButtonUpdate());
			add(true, new Button(excluirAcao));
			add(new Button(criarObjAcao));
			add(btnRelacao);
			add(true, btnRotulos);
			add(btnArrasto);
			add(btnSelecao);
			add(true, new ToggleButton(desenharIdAcao));
			add(new ToggleButton(desenharDescAcao));
			add(true, new ToggleButton(transpAcao));
			add(chkAjusteAutom);
			add(true, cmbConexao);
			add(true, new ButtonInfo());
			add(true, labelStatus);
			add(true, txtPrefixoNomeTabela);

			chkAjusteAutom.setToolTipText(Mensagens.getString("label.ajuste_automatico"));
			txtPrefixoNomeTabela.setToolTipText(Mensagens.getString("label.prefixo_nt"));

			eventos();
		}

		@Override
		protected void salvar() {
			if (!Util.confirmaSalvar(Container.this, Constantes.UM)) {
				return;
			}

			if (arquivo != null) {
				superficie.salvar(arquivo, getConexaoPadrao());
			} else {
				salvarComoAcao.actionPerformed(null);
			}
		}

		private class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action excluirSemTabelaAcao = Action.actionMenu("label.excluir_sem_tabela", null);
			private Action totalAtualAcao = Action.actionMenu("label.total_atual", null);
			private Action comparaRecAcao = Action.actionMenu("label.compararRec", null);
			private MenuItem itemTotalAtual = new MenuItem(totalAtualAcao);
			private MenuItem itemComparaRec = new MenuItem(comparaRecAcao);

			private ButtonInfo() {
				super("label.comparar", Icones.INFO);

				addMenuItem(itemTotalAtual);
				addMenuItem(true, itemComparaRec);
				addMenuItem(true, excluirSemTabelaAcao);

				totalAtualAcao.setActionListener(
						e -> superficie.atualizarTotal(getConexaoPadrao(), itemTotalAtual, labelStatus));
				comparaRecAcao.setActionListener(
						e -> superficie.compararRecent(getConexaoPadrao(), itemComparaRec, labelStatus));
				excluirSemTabelaAcao.setActionListener(e -> {
					superficie.excluirSemTabela();
					labelStatus.limpar();
				});
			}
		}

		private class ButtonConsulta extends ButtonPadrao1 {
			private static final long serialVersionUID = 1L;

			private ButtonConsulta() {
				super(Constantes.LABEL_CONSULTA, Icones.TABELA);

				formularioAcao.setActionListener(e -> {
					Frame frame = containerFormulario != null ? containerFormulario : formulario;
					ConsultaFormulario form = new ConsultaFormulario(formulario, formulario, getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					Frame frame = containerFormulario != null ? containerFormulario : formulario;
					ConsultaDialogo form = new ConsultaDialogo(frame, formulario, formulario, getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(
						e -> formulario.getFichario().getConsulta().nova(formulario, getConexaoPadrao()));
			}
		}

		private class ButtonUpdate extends ButtonPadrao1 {
			private static final long serialVersionUID = 1L;

			private ButtonUpdate() {
				super(Constantes.LABEL_ATUALIZAR, Icones.UPDATE);

				formularioAcao.setActionListener(e -> {
					Frame frame = containerFormulario != null ? containerFormulario : formulario;
					UpdateFormulario form = new UpdateFormulario(formulario, formulario, getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					Frame frame = containerFormulario != null ? containerFormulario : formulario;
					UpdateDialogo form = new UpdateDialogo(frame, formulario, formulario, getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(
						e -> formulario.getFichario().getUpdate().novo(formulario, getConexaoPadrao()));
			}
		}

		private class ButtonDestacar extends ButtonPadrao1 {
			private static final long serialVersionUID = 1L;
			private Action desktopAcao = Action.actionMenuDesktop();
			private Action abrirEmForm = Action.actionMenu("label.abrir_em_formulario", null);
			private Action destacarFrm = Action.actionMenu("label.destac_formulario", null);
			private Action destacarCnt = Action.actionMenu("label.destac_container", null);

			private ButtonDestacar() {
				super(Constantes.LABEL_DESTACAR, Icones.ARRASTAR, false);
				addMenuItem(desktopAcao);
				addMenuItem(true, destacarFrm);
				addMenuItem(destacarCnt);
				addMenuItem(abrirEmForm);

				formularioAcao.setActionListener(e -> formulario.destacar(getConexaoPadrao(), superficie,
						Constantes.TIPO_CONTAINER_FORMULARIO, null));
				ficharioAcao.setActionListener(e -> formulario.destacar(getConexaoPadrao(), superficie,
						Constantes.TIPO_CONTAINER_FICHARIO, null));
				desktopAcao.setActionListener(e -> formulario.destacar(getConexaoPadrao(), superficie,
						Constantes.TIPO_CONTAINER_DESKTOP, null));
				destacarFrm.setActionListener(
						e -> formulario.getFichario().getConteiner().destacarEmFormulario(formulario, Container.this));
				abrirEmForm.setActionListener(e -> formulario.getArquivos().abrir(getArquivo(), false, null));
				destacarCnt.setActionListener(e -> {
					if (containerFormulario != null) {
						containerFormulario.retornoAoFichario();
					}
				});
			}
		}

		private void abrirArquivo() {
			try {
				excluido();
				XMLColetor coletor = new XMLColetor();
				XML.processar(arquivo, coletor);
				abrir(arquivo, coletor, null, null);
				txtPrefixoNomeTabela.limpar();
				labelStatus.limpar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("BAIXAR: " + arquivo.getAbsolutePath(), ex, formulario);
			}
		}

		private void eventos() {
			chkAjusteAutom.addActionListener(e -> superficie.setAjusteAutomaticoForm(chkAjusteAutom.isSelected()));
			copiarAcao.setActionListener(e -> Formulario.CopiarColar.copiar(superficie));

			getBaixarAcao().setActionListener(e -> {
				if (arquivo == null) {
					btnSelecao.click();
					return;
				}

				abrirArquivo();
			});

			salvarComoAcao.setActionListener(e -> {
				JFileChooser fileChooser = Util.criarFileChooser(arquivo, false);
				int opcao = fileChooser.showSaveDialog(formulario);

				if (opcao == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					if (file != null) {
						superficie.salvar(file, getConexaoPadrao());
						arquivo = file;
						titulo();
					}
				}
			});

			configAtalho(colarAcao, KeyEvent.VK_V);
			colarAcao.setActionListener(e -> Formulario.CopiarColar.colar(superficie, false, 0, 0));

			configAtalho(excluirAcao, KeyEvent.VK_D);
			excluirAcao.setActionListener(e -> superficie.excluirSelecionados());

			criarObjAcao.setActionListener(e -> {
				superficie.criarNovoObjeto(40, 40);
				btnSelecao.setSelected(true);
				btnSelecao.click();
			});

			desenharIdAcao.setActionListener(e -> {
				ToggleButton button = (ToggleButton) e.getSource();
				superficie.desenharIds(button.isSelected());
			});

			desenharDescAcao.setActionListener(e -> {
				ToggleButton button = (ToggleButton) e.getSource();
				superficie.desenharDesc(button.isSelected());
			});

			transpAcao.setActionListener(e -> {
				ToggleButton button = (ToggleButton) e.getSource();
				superficie.transparente(button.isSelected());
			});

			txtPrefixoNomeTabela.addActionListener(e -> superficie.prefixoNomeTabela(txtPrefixoNomeTabela.getText()));
		}

		private void titulo() {
			if (containerFormulario == null) {
				Fichario fichario = formulario.getFichario();
				int indice = fichario.getSelectedIndex();

				if (indice != -1) {
					fichario.setToolTipTextAt(indice, arquivo.getAbsolutePath());
					fichario.setTitleAt(indice, arquivo.getName());
				}
			} else {
				containerFormulario.setTitle(arquivo.getName());
			}

			labelStatus.limpar();
		}

		private void configAtalho(Acao acao, int tecla) {
			Container.this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(Superficie.getKeyStroke(tecla), acao.getChave());
			Container.this.getActionMap().put(acao.getChave(), acao);
		}
	}

	public void excluir() {
		if (containerFormulario == null) {
			Fichario fichario = formulario.getFichario();
			int indice = fichario.getSelectedIndex();

			if (indice != -1) {
				fichario.remove(indice);
			}
		}
	}

	private class ArrastoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private ArrastoAcao() {
			super(false, "label.arrastar", Icones.MAO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnArrasto.isSelected()) {
				superficie.configEstado(Constantes.ARRASTO);
				superficie.repaint();
			}
		}
	}

	private class RotulosAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private RotulosAcao() {
			super(false, "label.rotulos", Icones.TEXTO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnRotulos.isSelected()) {
				superficie.configEstado(Constantes.ROTULOS);
				superficie.repaint();
			}
		}
	}

	private class RelacaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private RelacaoAcao() {
			super(false, "label.criar_relacao", Icones.SETA);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnRelacao.isSelected()) {
				superficie.configEstado(Constantes.RELACAO);
				superficie.repaint();
			}
		}
	}

	private class SelecaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private SelecaoAcao() {
			super(false, "label.selecao", Icones.CURSOR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnSelecao.isSelected()) {
				superficie.configEstado(Constantes.SELECAO);
				superficie.repaint();
			}
		}
	}

	public ContainerFormulario getContainerFormulario() {
		return containerFormulario;
	}

	public void setContainerFormulario(ContainerFormulario containerFormulario) {
		this.containerFormulario = containerFormulario;
	}

	public void excluido() {
		superficie.excluido();
	}

	public File getArquivo() {
		return arquivo;
	}

	public void setAbortarFecharComESCSuperficie(boolean b) {
		this.abortarFecharComESCSuperficie = b;
	}
}