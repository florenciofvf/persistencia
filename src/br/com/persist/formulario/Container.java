package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.comp.Button;
import br.com.persist.comp.PanelBorder;
import br.com.persist.dialogo.DialogoConexao;
import br.com.persist.dialogo.RelacaoDialogo;
import br.com.persist.util.Acao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;

public class Container extends PanelBorder {
	private static final long serialVersionUID = 1L;
	private final JToggleButton buttonMovimentar = new JToggleButton(new MovimentarAcao());
	private final Toolbar toolbar = new Toolbar();
	private final Formulario formulario;
	private final Superficie superficie;
	private File arquivo;

	public Container(Formulario formulario) {
		superficie = new Superficie(formulario);
		this.formulario = formulario;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, superficie);
		add(BorderLayout.NORTH, toolbar);
	}

	public void abrir(File file, List<Objeto> objetos, List<Relacao> relacoes) {
		superficie.abrir(objetos, relacoes);
		arquivo = file;
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		public Toolbar() {
			add(new Button(new ConexaoAcao(false)));
			addSeparator();
			add(new Button(new SalvarAcao(false)));
			add(new Button(new SalvarComoAcao(false)));
			addSeparator();
			add(new Button(new ExcluirObjetoAcao()));
			add(new Button(new ExcluirRelacaoAcao()));
			add(new Button(new CriarObjetoAcao()));
			add(new Button(new CriarRelacaoAcao()));
			addSeparator();
			add(new JToggleButton(new DesenhoIdAcao()));
			add(buttonMovimentar);
		}
	}

	private class ConexaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ConexaoAcao(boolean menu) {
			super(menu, "label.conexao", Icones.BANCO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new DialogoConexao(formulario);
		}
	}

	private class SalvarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public SalvarAcao(boolean menu) {
			super(menu, "label.salvar", Icones.SALVAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (arquivo != null) {
				superficie.salvar(arquivo);
			} else {
				new SalvarComoAcao(false).actionPerformed(null);
			}
		}
	}

	private class SalvarComoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public SalvarComoAcao(boolean menu) {
			super(menu, "label.salvar_como", Icones.SALVARC);
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

	private class ExcluirObjetoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ExcluirObjetoAcao() {
			super(false, "label.excluir_objeto", Icones.EXCLUIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			superficie.excluirSelecionados();
		}
	}

	private class ExcluirRelacaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ExcluirRelacaoAcao() {
			super(false, "label.excluir_relacao", Icones.EXCLUIR2);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Objeto[] selecionados = superficie.getSelecionados();

			if (selecionados.length == Constantes.DOIS) {
				Relacao relacao = superficie.getRelacao(selecionados[0], selecionados[1]);
				superficie.excluir(relacao);
				superficie.repaint();
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
			superficie.addObjeto(new Objeto(40, 40));
			superficie.limparSelecao();
			superficie.repaint();
		}
	}

	private class CriarRelacaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public CriarRelacaoAcao() {
			super(false, "label.criar_relacao", Icones.CRIAR2);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Objeto[] selecionados = superficie.getSelecionados();

			if (selecionados.length == Constantes.DOIS) {
				new RelacaoDialogo(formulario, superficie, selecionados[0], selecionados[1]);
			}
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

	private class MovimentarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public MovimentarAcao() {
			super(false, "label.movimentar", Icones.MAO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JToggleButton button = (JToggleButton) e.getSource();
			superficie.movimentarStatus(button.isSelected());
		}
	}
}