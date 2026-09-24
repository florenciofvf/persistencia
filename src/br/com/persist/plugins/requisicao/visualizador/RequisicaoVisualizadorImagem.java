package br.com.persist.plugins.requisicao.visualizador;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import br.com.persist.assistencia.Icone;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.SwingUtilitario;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.data.Tipo;

public class RequisicaoVisualizadorImagem extends AbstratoRequisicaoVisualizador {
	@Override
	public Component exibidor(Component parent, byte[] bytes, Tipo parametros) {
		try {
			Label label = new Label();
			label.setIcon(new ImageIcon(bytes));

			Panel panel = new Panel();
			panel.add(BorderLayout.CENTER, new ScrollPane(label));
			SwingUtilitario.invokeLater(() -> label.scrollRectToVisible(new Rectangle()));

			return panel;
		} catch (Exception e) {
			Util.mensagem(parent, e.getMessage());
			return null;
		}
	}

	@Override
	public String toString() {
		return "Imagem";
	}

	@Override
	public Icone getIcone() {
		return Icones.ICON;
	}
}