package br.com.persist.desktop;

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
import br.com.persist.comp.PanelBorder;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.ToggleButton;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.FormularioSelect;
import br.com.persist.formulario.FormularioSuperficie;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Container extends PanelBorder {
	private static final long serialVersionUID = 1L;
	private final ToggleButton btnArrasto = new ToggleButton(new ArrastoAcao());
	private final ToggleButton btnRotulos = new ToggleButton(new RotulosAcao());
	private final ToggleButton btnRelacao = new ToggleButton(new RelacaoAcao());
	private final ToggleButton btnSelecao = new ToggleButton(new SelecaoAcao());
	private FormularioSuperficie formularioSuperficie;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;
	private final Formulario formulario;
	private final Superficie superficie;
	private File arquivo;

	public Container(Formulario formulario) {
		cmbConexao = new JComboBox<>(formulario.getConexoes());
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
		private Action destacarAcao = Action.actionIcon("label.desktop", Icones.PANEL2);
		private Action excluirAcao = Action.actionIcon("label.excluir", Icones.EXCLUIR);
		private Action formAcao = Action.actionIcon("label.formulario", Icones.PANEL);
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
			add(new Button(destacarAcao));
			add(new Button(formAcao));
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

		private void eventos() {
			destacarAcao.setActionListener(e -> formulario.destacar(getConexaoPadrao(), superficie, false));
			formAcao.setActionListener(e -> formulario.destacar(getConexaoPadrao(), superficie, true));
			copiarAcao.setActionListener(e -> Formulario.copiar(superficie));

			baixarAcao.setActionListener(e -> {
				if (arquivo == null) {
					btnSelecao.click();
					return;
				}

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
			});

			salvarAcao.setActionListener(e -> {
				if (arquivo != null) {
					superficie.salvar(arquivo, getConexaoPadrao());
				} else {
					salvarComoAcao.actionPerformed(null);
				}
			});

			salvarComoAcao.setActionListener(e -> {
				JFileChooser fileChooser = new JFileChooser(".");
				fileChooser.setPreferredSize(Constantes.DIMENSION_FILE_CHOOSER);

				if (arquivo != null) {
					fileChooser.setCurrentDirectory(arquivo);
				}

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
				FormularioSelect form = new FormularioSelect(Mensagens.getString("label.pesquisa"), formulario,
						getConexaoPadrao(), null, null);
				form.setLocationRelativeTo(formularioSuperficie != null ? formularioSuperficie : formulario);
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
			if (formularioSuperficie == null) {
				Fichario fichario = formulario.getFichario();
				int indice = fichario.getSelectedIndex();

				if (indice != -1) {
					fichario.setTitleAt(indice, arquivo.getName());
				}
			} else {
				formularioSuperficie.setTitle(arquivo.getName());
			}
		}

		private void configAtalho(Acao acao, int tecla) {
			Container.this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(Superficie.getKeyStroke(tecla), acao.getChave());
			Container.this.getActionMap().put(acao.getChave(), acao);
		}
	}

	public void excluir() {
		if (formularioSuperficie == null) {
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

	public FormularioSuperficie getFormularioSuperficie() {
		return formularioSuperficie;
	}

	public void setFormularioSuperficie(FormularioSuperficie formularioSuperficie) {
		this.formularioSuperficie = formularioSuperficie;
	}

	public void excluido() {
		superficie.excluido();
	}
}