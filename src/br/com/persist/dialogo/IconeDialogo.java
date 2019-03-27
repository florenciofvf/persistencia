package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
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
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Imagens;

public class IconeDialogo extends DialogoAbstrato {
	private static final long serialVersionUID = 1L;
	private final Objeto objeto;
	private final Label label;

	public IconeDialogo(Dialog dialog, Objeto objeto, Label label) {
		super(dialog, objeto.getId(), false);
		this.objeto = objeto;
		this.label = label;
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		Panel matriz = new Panel(new GridLayout(0, 25));

		List<Entry<String, Icon>> icones = Imagens.getIcones();

		for (Map.Entry<String, Icon> entry : icones) {
			matriz.add(new LabelIcone(entry));
		}

		add(BorderLayout.CENTER, new ScrollPane(matriz));

		setTitle(icones.size() + " - " + getTitle());
	}

	protected void processar() {
	}

	private class LabelIcone extends Label {
		private static final long serialVersionUID = 1L;
		private final String nome;

		LabelIcone(Map.Entry<String, Icon> entry) {
			addMouseListener(mouseListener_);
			setHorizontalAlignment(CENTER);
			setIcon(entry.getValue());
			nome = entry.getKey();
			setToolTipText(nome);
			if (nome.equals(objeto.getIcone())) {
				setBorder(BorderFactory.createLineBorder(Color.BLUE));
			}
		}

		private MouseListener mouseListener_ = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				label.setIcon(getIcon());
				objeto.setIcone(nome);
				Formulario.macro.imagem(objeto.getIcone());
				dispose();
			}
		};
	}
}