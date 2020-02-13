package br.com.persist.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import br.com.persist.Metadado;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextField;
import br.com.persist.comp.ToggleButton;
import br.com.persist.dialogo.ConsultaDialogo;
import br.com.persist.dialogo.UpdateDialogo;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.ConsultaFormulario;
import br.com.persist.formulario.ContainerFormulario;
import br.com.persist.formulario.UpdateFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.ButtonPadrao1;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Container extends Panel {
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

	public void selecionarConexao(Conexao conexao) {
		if (conexao != null) {
			cmbConexao.setSelectedItem(conexao);
		}
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

	public void abrir(File file, List<Objeto> objetos, List<Relacao> relacoes, List<Form> forms,
			StringBuilder sbConexao, Graphics g, Dimension d) {
		if (abortarFecharComESCSuperficie) {
			superficie.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
		}

		superficie.abrir(objetos, relacoes, d);
		arquivo = file;
		btnSelecao.click();

		if (!Util.estaVazio(sbConexao.toString())) {
			String nomeConexao = sbConexao.toString();
			Conexao conexao = null;

			for (int i = 0; i < cmbConexao.getItemCount(); i++) {
				Conexao c = cmbConexao.getItemAt(i);

				if (nomeConexao.equalsIgnoreCase(c.getNome())) {
					conexao = c;
					break;
				}
			}

			if (conexao != null) {
				cmbConexao.setSelectedItem(conexao);
			}
		}

		Conexao conexao = getConexaoPadrao();

		if (conexao == null) {
			return;
		}

		adicionarForm(conexao, forms, objetos, g);
	}

	private void adicionarForm(Conexao conexao, List<Form> forms, List<Objeto> objetos, Graphics g) {
		for (Form form : forms) {
			Objeto instancia = null;

			for (Objeto objeto : objetos) {
				if (form.getObjeto().equals(objeto.getId())) {
					instancia = objeto;
				}
			}

			if (instancia != null) {
				Object[] array = Util.criarArray(conexao, instancia, new Dimension(form.getLargura(), form.getAltura()),
						form.getApelido());
				superficie.addForm(array, new Point(form.getX(), form.getY()), g, (String) array[Util.ARRAY_INDICE_APE],
						true);
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
		private Action salvarAcao = Action.actionIconSalvar();
		private Action baixarAcao = Action.actionIconBaixar();
		private Label labelStatus = new Label();

		@Override
		public void ini(IJanela janela) {
			super.ini(janela);

			addButton(baixarAcao);
			addButton(true, salvarAcao);
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
			add(true, cmbConexao);
			add(true, new ButtonInfo());
			add(true, labelStatus);
			add(true, txtPrefixoNomeTabela);

			txtPrefixoNomeTabela.setToolTipText(Mensagens.getString("label.prefixo_nt"));

			eventos();
		}

		class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action excluirSemTabelaAcao = Action.actionMenu("label.excluir_sem_tabela", null);
			private Action totalAtualAcao = Action.actionMenu("label.total_atual", null);
			private Action comparaRecAcao = Action.actionMenu("label.compararRec", null);
			private MenuItem itemTotalAtual = new MenuItem(totalAtualAcao);
			private MenuItem itemComparaRec = new MenuItem(comparaRecAcao);

			ButtonInfo() {
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

		class ButtonConsulta extends ButtonPadrao1 {
			private static final long serialVersionUID = 1L;

			ButtonConsulta() {
				super(Constantes.LABEL_CONSULTA, Icones.PANEL3);

				formularioAcao.setActionListener(e -> {
					ConsultaFormulario form = new ConsultaFormulario(formulario, getConexaoPadrao());
					form.setLocationRelativeTo(containerFormulario != null ? containerFormulario : formulario);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					Frame frame = containerFormulario != null ? containerFormulario : formulario;
					ConsultaDialogo form = new ConsultaDialogo(frame, formulario, getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				ficharioAcao
						.setActionListener(e -> formulario.getFichario().novaConsulta(formulario, getConexaoPadrao()));
			}
		}

		class ButtonUpdate extends ButtonPadrao1 {
			private static final long serialVersionUID = 1L;

			ButtonUpdate() {
				super(Constantes.LABEL_ATUALIZAR, Icones.UPDATE);

				formularioAcao.setActionListener(e -> {
					UpdateFormulario form = new UpdateFormulario(formulario, getConexaoPadrao());
					form.setLocationRelativeTo(containerFormulario != null ? containerFormulario : formulario);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					Frame frame = containerFormulario != null ? containerFormulario : formulario;
					UpdateDialogo form = new UpdateDialogo(frame, formulario, getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				ficharioAcao
						.setActionListener(e -> formulario.getFichario().novoUpdate(formulario, getConexaoPadrao()));
			}
		}

		class ButtonDestacar extends ButtonPadrao1 {
			private static final long serialVersionUID = 1L;
			Action desktopAcao = Action.actionMenuDesktop();
			Action destacarFrm = Action.actionMenu("label.destac_formulario", null);
			Action destacarCnt = Action.actionMenu("label.destac_container", null);

			ButtonDestacar() {
				super(Constantes.LABEL_DESTACAR, Icones.ARRASTAR, false);
				addMenuItem(desktopAcao);
				addMenuItem(destacarFrm);
				addMenuItem(destacarCnt);

				formularioAcao.setActionListener(
						e -> formulario.destacar(getConexaoPadrao(), superficie, Constantes.TIPO_CONTAINER_FORMULARIO));
				ficharioAcao.setActionListener(
						e -> formulario.destacar(getConexaoPadrao(), superficie, Constantes.TIPO_CONTAINER_FICHARIO));
				desktopAcao.setActionListener(
						e -> formulario.destacar(getConexaoPadrao(), superficie, Constantes.TIPO_CONTAINER_DESKTOP));
				destacarFrm.setActionListener(
						e -> formulario.getFichario().destacarEmFormulario(formulario, Container.this));
				destacarCnt.setActionListener(e -> {
					if (containerFormulario != null) {
						containerFormulario.retornoDestacarEmFormulario();
					}
				});
			}
		}

		private void abrirArquivo() {
			try {
				excluido();
				StringBuilder sbConexao = new StringBuilder();
				List<Relacao> relacoes = new ArrayList<>();
				List<Objeto> objetos = new ArrayList<>();
				List<Form> forms = new ArrayList<>();
				Dimension d = XML.processar(arquivo, objetos, relacoes, forms, sbConexao);
				abrir(arquivo, objetos, relacoes, forms, sbConexao, null, d);
				labelStatus.limpar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("BAIXAR: " + arquivo.getAbsolutePath(), ex, formulario);
			}
		}

		private void eventos() {
			copiarAcao.setActionListener(e -> Formulario.copiar(superficie));

			baixarAcao.setActionListener(e -> {
				if (arquivo == null) {
					btnSelecao.click();
					return;
				}

				abrirArquivo();
			});

			salvarAcao.setActionListener(e -> {
				if (arquivo != null) {
					superficie.salvar(arquivo, getConexaoPadrao());
				} else {
					salvarComoAcao.actionPerformed(null);
				}
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
			colarAcao.setActionListener(e -> {
				Formulario.colar(superficie, false, 0, 0);
				superficie.repaint();
			});

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

		ArrastoAcao() {
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

		RotulosAcao() {
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

		RelacaoAcao() {
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

		SelecaoAcao() {
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

	public void setSuperficieFormulario(ContainerFormulario containerFormulario) {
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