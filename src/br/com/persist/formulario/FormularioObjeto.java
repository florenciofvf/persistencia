package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import br.com.persist.Objeto;
import br.com.persist.comp.Button;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;

public class FormularioObjeto extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final Objeto objeto;

	public FormularioObjeto(Formulario formulario, Objeto objeto) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(objeto.getId());
		this.objeto = objeto;
		setSize(800, 600);
		setLocationRelativeTo(formulario);
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		public Toolbar() {
			add(new Button(new FecharAcao(false)));
			addSeparator();
			add(new Button(new ExcluirRegistrosAcao()));
		}
	}

	private class FecharAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public FecharAcao(boolean menu) {
			super(menu, "label.fechar", Icones.SAIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	private class ExcluirRegistrosAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ExcluirRegistrosAcao() {
			super(false, "label.excluir_registro", Icones.EXCLUIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
}