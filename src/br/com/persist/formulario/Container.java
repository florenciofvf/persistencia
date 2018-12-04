package br.com.persist.formulario;

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
import javax.swing.InputMap;
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
import br.com.persist.util.Acao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
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

				if (nomeConexao.equals(c.getNome())) {
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
				Object[] array = Fichario.criarArray(conexao, instancia,
						new Dimension(form.getLargura(), form.getAltura()));
				superficie.addForm(array, new Point(form.getX(), form.getY()), g);
			}
		}
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		Toolbar() {
			add(new Button(new BaixarAcao()));
			addSeparator();
			add(new Button(new SalvarAcao()));
			add(new Button(new SalvarComoAcao()));
			addSeparator();
			add(new Button(new CopiarAcao()));
			add(new Button(new ColarAcao()));
			addSeparator();
			add(new Button(new DestacarAcao()));
			add(new Button(new FormularioAcao()));
			addSeparator();
			add(new Button(new ExcluirAcao()));
			add(new Button(new CriarObjetoAcao()));
			add(btnRelacao);
			addSeparator();
			add(btnRotulos);
			add(btnArrasto);
			add(btnSelecao);
			addSeparator();
			add(new ToggleButton(new DesenhoIdAcao()));
			add(new ToggleButton(new TransparenteAcao()));
			addSeparator();
		}

		class BaixarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			BaixarAcao() {
				super(false, "label.baixar", Icones.BAIXAR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (arquivo == null) {
					btnSelecao.click();
					return;
				}

				try {
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
		}

		class SalvarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			SalvarAcao() {
				super(false, "label.salvar", Icones.SALVAR);

				inputMap().put(Superficie.getKeyStroke(KeyEvent.VK_S), chave);
				Container.this.getActionMap().put(chave, this);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (arquivo != null) {
					superficie.salvar(arquivo, getConexaoPadrao());
				} else {
					new SalvarComoAcao().actionPerformed(null);
				}
			}
		}

		class SalvarComoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			SalvarComoAcao() {
				super(false, "label.salvar_como", Icones.SALVARC);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(".");
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
			}
		}

		class CopiarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			CopiarAcao() {
				super(false, "label.copiar", Icones.COPIA);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				formulario.copiar(superficie);
			}
		}

		class ColarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ColarAcao() {
				super(false, "label.colar", Icones.COLAR);

				inputMap().put(Superficie.getKeyStroke(KeyEvent.VK_V), chave);
				Container.this.getActionMap().put(chave, this);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				formulario.colar(superficie, false, 0, 0);
				superficie.repaint();
			}
		}

		class DestacarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			DestacarAcao() {
				super(false, "label.desktop", Icones.CUBO2);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				formulario.destacar(getConexaoPadrao(), superficie, false);
			}
		}

		class FormularioAcao extends Acao {
			private static final long serialVersionUID = 1L;

			FormularioAcao() {
				super(false, "label.formulario", Icones.PANEL);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				formulario.destacar(getConexaoPadrao(), superficie, true);
			}
		}

		class ExcluirAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ExcluirAcao() {
				super(false, "label.excluir", Icones.EXCLUIR);

				inputMap().put(Superficie.getKeyStroke(KeyEvent.VK_D), chave);
				Container.this.getActionMap().put(chave, this);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				superficie.excluirSelecionados();
			}
		}

		class CriarObjetoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			CriarObjetoAcao() {
				super(false, "label.criar_objeto", Icones.CRIAR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				superficie.addObjeto(new Objeto(40, 40));
				btnSelecao.setSelected(true);
				superficie.limparSelecao();
				superficie.repaint();
				btnSelecao.click();
			}
		}

		class DesenhoIdAcao extends Acao {
			private static final long serialVersionUID = 1L;

			DesenhoIdAcao() {
				super(false, "label.desenhar_id", Icones.LABEL);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				ToggleButton button = (ToggleButton) e.getSource();
				superficie.desenharIds(button.isSelected());
			}
		}

		class TransparenteAcao extends Acao {
			private static final long serialVersionUID = 1L;

			TransparenteAcao() {
				super(false, "label.transparente", Icones.RECT);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				ToggleButton button = (ToggleButton) e.getSource();
				superficie.transparente(button.isSelected());
			}
		}
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

	private InputMap inputMap() {
		return getInputMap(WHEN_IN_FOCUSED_WINDOW);
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
}