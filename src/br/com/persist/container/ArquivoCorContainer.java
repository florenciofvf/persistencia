package br.com.persist.container;

import java.awt.BorderLayout;

import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.Arquivo;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.modelo.AnexoModelo;
import br.com.persist.util.Action;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;

public class ArquivoCorContainer extends Panel implements ChangeListener {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final JColorChooser colorChooser;
	private final transient Arquivo arquivo;

	public ArquivoCorContainer(IJanela janela, Arquivo arquivo) {
		colorChooser = new JColorChooser();
		colorChooser.getSelectionModel().addChangeListener(this);
		this.arquivo = arquivo;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, colorChooser);
		add(BorderLayout.NORTH, toolbar);

		if (arquivo.getCorFonte() != null) {
			colorChooser.setColor(arquivo.getCorFonte());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		arquivo.setCorFonte(colorChooser.getColor());
		AnexoModelo.putArquivo(arquivo);
		toolbar.fechar();
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action cancelaAcao = Action.actionIcon("label.limpar", Icones.NOVO);

		@Override
		public void ini(IJanela janela) {
			super.ini(janela);

			addButton(cancelaAcao);

			cancelaAcao.setActionListener(e -> {
				arquivo.setCorFonte(null);
				fechar();
			});
		}
	}
}