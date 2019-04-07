package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import br.com.persist.Objeto;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.principal.Formulario;
import br.com.persist.util.IJanela;
import br.com.persist.util.Imagens;

public class IconeContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final transient Objeto objeto;
	private final Label label;
	private int totalIcones;

	public IconeContainer(IJanela janela, Objeto objeto, Label label) {
		this.objeto = objeto;
		toolbar.ini(janela);
		this.label = label;
		montarLayout();
	}

	private void montarLayout() {
		List<Entry<String, Icon>> icones = Imagens.getIcones();
		Panel matriz = new Panel(new GridLayout(0, 25));

		for (Map.Entry<String, Icon> entry : icones) {
			matriz.add(new LabelIcone(entry));
		}

		add(BorderLayout.CENTER, new ScrollPane(matriz));
		add(BorderLayout.NORTH, toolbar);
		totalIcones = icones.size();
	}

	public int getTotalIcones() {
		return totalIcones;
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		@Override
		protected void ini(IJanela janela) {
			super.ini(janela);
		}
	}

	private class LabelIcone extends Label {
		private static final long serialVersionUID = 1L;
		private final String nome;

		LabelIcone(Map.Entry<String, Icon> entry) {
			addMouseListener(mouseListenerInner);
			setHorizontalAlignment(CENTER);
			setIcon(entry.getValue());
			nome = entry.getKey();
			setToolTipText(nome);
			if (nome.equals(objeto.getIcone())) {
				setBorder(BorderFactory.createLineBorder(Color.BLUE));
			}
		}

		private transient MouseListener mouseListenerInner = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				label.setIcon(getIcon());
				objeto.setIcone(nome);
				Formulario.macro.imagem(objeto.getIcone());
				toolbar.fechar();
			}
		};
	}
}