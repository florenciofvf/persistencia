package br.com.persist.plugins.requisicao.conteudo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.parser.Tipo;

public class ConteudoImagem extends AbstratoRequisicaoConteudo {

	@Override
	public Component exibidor(Component parent, byte[] bytes, Tipo parametros) {
		try {
			Label label = new Label();
			label.setIcon(new ImageIcon(bytes));

			Panel panel = new Panel();
			panel.add(BorderLayout.CENTER, new ScrollPane(label));
			SwingUtilities.invokeLater(() -> label.scrollRectToVisible(new Rectangle()));

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
	public Icon getIcone() {
		return Icones.ICON;
	}
}