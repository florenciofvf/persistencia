package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.Button;
import br.com.persist.comp.PanelBorder;
import br.com.persist.util.Acao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Container extends PanelBorder {
	private static final long serialVersionUID = 1L;
	private final JToggleButton btnArrasto = new JToggleButton(new ArrastoAcao());
	private final JToggleButton btnRelacao = new JToggleButton(new RelacaoAcao());
	private final JToggleButton btnSelecao = new JToggleButton(new SelecaoAcao());
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
		add(BorderLayout.CENTER, superficie);
		add(BorderLayout.NORTH, toolbar);
		grupo.add(btnArrasto);
		grupo.add(btnRelacao);
		grupo.add(btnSelecao);
	}

	public Conexao getConexaoPadrao() {
		return (Conexao) cmbConexao.getSelectedItem();
	}

	public void abrir(File file, List<Objeto> objetos, List<Relacao> relacoes) {
		superficie.abrir(objetos, relacoes);
		arquivo = file;
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		public Toolbar() {
			add(new Button(new BaixarAcao()));
			add(new Button(new SalvarAcao()));
			add(new Button(new SalvarComoAcao()));
			addSeparator();
			add(new Button(new ExcluirAcao()));
			add(new Button(new CriarObjetoAcao()));
			add(btnRelacao);
			addSeparator();
			add(btnSelecao);
			add(btnArrasto);
			addSeparator();
			add(new JToggleButton(new DesenhoIdAcao()));
			addSeparator();
		}
	}

	private class SalvarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public SalvarAcao() {
			super(false, "label.salvar", Icones.SALVAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (arquivo != null) {
				superficie.salvar(arquivo);
			} else {
				new SalvarComoAcao().actionPerformed(null);
			}
		}
	}

	private class SalvarComoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public SalvarComoAcao() {
			super(false, "label.salvar_como", Icones.SALVARC);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(".");
			int opcao = fileChooser.showSaveDialog(formulario);

			if (opcao == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();

				if (file != null) {
					superficie.salvar(file);
					arquivo = file;
					titulo();
				}
			}
		}
	}

	private void titulo() {
		Fichario fichario = formulario.getFichario();
		int indice = fichario.getSelectedIndex();

		if (indice != -1) {
			fichario.setTitleAt(indice, arquivo.getName());
		}
	}

	private class ExcluirAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ExcluirAcao() {
			super(false, "label.excluir", Icones.EXCLUIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			superficie.excluirSelecionados();
		}
	}

	private class BaixarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public BaixarAcao() {
			super(false, "label.baixar", Icones.BAIXAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (arquivo == null) {
				return;
			}

			try {
				List<Relacao> relacoes = new ArrayList<>();
				List<Objeto> objetos = new ArrayList<>();
				XML.processar(arquivo, objetos, relacoes);

				abrir(arquivo, objetos, relacoes);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("BAIXAR: " + arquivo.getAbsolutePath(), ex, formulario);
			}
		}
	}

	private class CriarObjetoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public CriarObjetoAcao() {
			super(false, "label.criar_objeto", Icones.CRIAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			superficie.configEstado(Constantes.SELECAO);
			superficie.addObjeto(new Objeto(40, 40));
			btnSelecao.setSelected(true);
			superficie.limparSelecao();
			superficie.repaint();
		}
	}

	private class DesenhoIdAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public DesenhoIdAcao() {
			super(false, "label.desenhar_id", Icones.LABEL);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JToggleButton button = (JToggleButton) e.getSource();
			superficie.desenharIds(button.isSelected());
		}
	}

	private class ArrastoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ArrastoAcao() {
			super(false, "label.arrastar", Icones.MAO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnArrasto.isSelected()) {
				superficie.configEstado(Constantes.ARRASTO);
			}
		}
	}

	private class RelacaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public RelacaoAcao() {
			super(false, "label.criar_relacao", Icones.SETA);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnRelacao.isSelected()) {
				superficie.configEstado(Constantes.RELACAO);
			}
		}
	}

	private class SelecaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public SelecaoAcao() {
			super(false, "label.selecao", Icones.CURSOR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnSelecao.isSelected()) {
				superficie.configEstado(Constantes.SELECAO);
			}
		}
	}
}