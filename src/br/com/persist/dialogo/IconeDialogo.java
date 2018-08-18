package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import br.com.persist.Objeto;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.util.Imagens;

public class IconeDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final Objeto objeto;
	private final Label label;

	public IconeDialogo(Dialog dialog, Objeto objeto, Label label) {
		super(dialog, objeto.getId(), 600, 600, false);
		this.objeto = objeto;
		this.label = label;
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		Panel matriz = new Panel(new GridLayout(0, 25));

		for (Map.Entry<String, Icon> entry : Imagens.getIcones()) {
			matriz.add(new LabelIcone(entry));
		}

		add(BorderLayout.CENTER, new ScrollPane(matriz));
	}

	protected void processar() {
	}

	private class LabelIcone extends Label {
		private static final long serialVersionUID = 1L;
		private final String nome;

		LabelIcone(Map.Entry<String, Icon> entry) {
			addMouseListener(mouseListener);
			setHorizontalAlignment(CENTER);
			setIcon(entry.getValue());
			nome = entry.getKey();
			setToolTipText(nome);
			if (nome.equals(objeto.getIcone())) {
				setBorder(BorderFactory.createLineBorder(Color.BLUE));
			}
		}

		private MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				label.setIcon(getIcon());
				objeto.setIcone(nome);
				dispose();
			}
		};
	}
}