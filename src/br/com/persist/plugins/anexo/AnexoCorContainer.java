package br.com.persist.plugins.anexo;

import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;

public class AnexoCorContainer extends Panel implements ChangeListener {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final JColorChooser colorChooser;
	private final transient Anexo anexo;

	public AnexoCorContainer(Janela janela, Anexo anexo) {
		colorChooser = new JColorChooser();
		colorChooser.getSelectionModel().addChangeListener(this);
		toolbar.ini(janela);
		this.anexo = anexo;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, colorChooser);
		add(BorderLayout.NORTH, toolbar);
		if (anexo.getCorFonte() != null) {
			colorChooser.setColor(anexo.getCorFonte());
		} else {
			colorChooser.setColor(Color.BLACK);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		anexo.setCorFonte(colorChooser.getColor());
		AnexoModelo.putAnexo(anexo);
		toolbar.fechar();
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR);
		}

		@Override
		protected void limpar() {
			anexo.setCorFonte(null);
			fechar();
		}
	}
}