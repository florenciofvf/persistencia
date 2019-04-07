package br.com.persist.fichario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.Button;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.PanelBorder;
import br.com.persist.comp.Popup;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.ToggleButton;
import br.com.persist.desktop.Superficie;
import br.com.persist.formulario.ConsultaFormulario;
import br.com.persist.formulario.SuperficieFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class FicharioAbaContainer extends PanelBorder {
	private static final long serialVersionUID = 1L;
	private final ToggleButton btnArrasto = new ToggleButton(new ArrastoAcao());
	private final ToggleButton btnRotulos = new ToggleButton(new RotulosAcao());
	private final ToggleButton btnRelacao = new ToggleButton(new RelacaoAcao());
	private final ToggleButton btnSelecao = new ToggleButton(new SelecaoAcao());
	private SuperficieFormulario superficieFormulario;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;
	private final Formulario formulario;
	private final Superficie superficie;
	private File arquivo;

	public FicharioAbaContainer(Formulario formulario) {
		cmbConexao = Util.criarComboConexao(formulario, null);
		superficie = new Superficie(formulario, this);
		this.formulario = formulario;
		toolbar.add(cmbConexao);
		montarLayout();
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

	public void abrir(File file, List<Objeto> objetos, List<Relacao> relacoes, List<Form> forms,
			StringBuilder sbConexao, Graphics g, Dimension d) {
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

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action salvarComoAcao = Action.actionIcon("label.salvar_como", Icones.SALVARC);
		private Action desenharDescAcao = Action.actionIcon("label.desenhar_desc", Icones.TAG);
		private Action desenharIdAcao = Action.actionIcon("label.desenhar_id", Icones.LABEL);
		private Action criarObjAcao = Action.actionIcon("label.criar_objeto", Icones.CRIAR);
		private Action transpAcao = Action.actionIcon("label.transparente", Icones.RECT);
		private Action excluirAcao = Action.actionIcon("label.excluir", Icones.EXCLUIR);
		private Action baixarAcao = Action.actionIcon("label.baixar", Icones.BAIXAR);
		private Action salvarAcao = Action.actionIcon("label.salvar", Icones.SALVAR);
		private Action consAcao = Action.actionIcon("label.consulta", Icones.PANEL3);
		private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);
		private Action colarAcao = Action.actionIcon("label.colar", Icones.COLAR);

		Toolbar() {
			add(new Button(baixarAcao));
			addSeparator();
			add(new Button(salvarAcao));
			add(new Button(salvarComoAcao));
			addSeparator();
			add(new Button(copiarAcao));
			add(new Button(colarAcao));
			addSeparator();
			add(new ButtonDestacar());
			add(new Button(consAcao));
			addSeparator();
			add(new Button(excluirAcao));
			add(new Button(criarObjAcao));
			add(btnRelacao);
			addSeparator();
			add(btnRotulos);
			add(btnArrasto);
			add(btnSelecao);
			addSeparator();
			add(new ToggleButton(desenharIdAcao));
			add(new ToggleButton(desenharDescAcao));
			addSeparator();
			add(new ToggleButton(transpAcao));
			addSeparator();

			eventos();
		}

		private class ButtonDestacar extends Button {
			private static final long serialVersionUID = 1L;
			Action objetoAcao = Action.actionMenu("label.objeto", null);
			Action formularioAcao = Action.actionMenuFormulario();
			Action desktopAcao = Action.actionMenuDesktop();
			private Popup popup = new Popup();

			ButtonDestacar() {
				setToolTipText(Mensagens.getString(Constantes.LABEL_DESTACAR));
				popup.add(new MenuItem(formularioAcao));
				popup.add(new MenuItem(desktopAcao));
				popup.add(new MenuItem(objetoAcao));
				setComponentPopupMenu(popup);
				setIcon(Icones.ARRASTAR);
				addActionListener(e -> popup.show(this, 5, 5));

				formularioAcao.setActionListener(
						e -> formulario.destacar(getConexaoPadrao(), superficie, Constantes.TIPO_CONTAINER_FORMULARIO));
				desktopAcao.setActionListener(
						e -> formulario.destacar(getConexaoPadrao(), superficie, Constantes.TIPO_CONTAINER_DESKTOP));
				objetoAcao.setActionListener(
						e -> formulario.destacar(getConexaoPadrao(), superficie, Constantes.TIPO_CONTAINER_OBJETO));
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

			consAcao.setActionListener(e -> {
				ConsultaFormulario form = new ConsultaFormulario(formulario, getConexaoPadrao());
				form.setLocationRelativeTo(superficieFormulario != null ? superficieFormulario : formulario);
				form.setVisible(true);
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
		}

		private void titulo() {
			if (superficieFormulario == null) {
				Fichario fichario = formulario.getFichario();
				int indice = fichario.getSelectedIndex();

				if (indice != -1) {
					fichario.setTitleAt(indice, arquivo.getName());
				}
			} else {
				superficieFormulario.setTitle(arquivo.getName());
			}
		}

		private void configAtalho(Acao acao, int tecla) {
			FicharioAbaContainer.this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(Superficie.getKeyStroke(tecla),
					acao.getChave());
			FicharioAbaContainer.this.getActionMap().put(acao.getChave(), acao);
		}
	}

	public void excluir() {
		if (superficieFormulario == null) {
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

	public SuperficieFormulario getSuperficieFormulario() {
		return superficieFormulario;
	}

	public void setSuperficieFormulario(SuperficieFormulario superficieFormulario) {
		this.superficieFormulario = superficieFormulario;
	}

	public void excluido() {
		superficie.excluido();
	}

	public File getArquivo() {
		return arquivo;
	}
}