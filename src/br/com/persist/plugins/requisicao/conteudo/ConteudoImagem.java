package br.com.persist.plugins.requisicao.conteudo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoException;

public class ConteudoImagem extends AbstratoRequisicaoConteudo {

	@Override
	public Component exibir(InputStream is, Tipo parametros, String uri) throws RequisicaoException, IOException {
		Label label = new Label();
		byte[] bytes = Util.getArrayBytes(is);
		label.setIcon(new ImageIcon(bytes));

		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, criarToolbarPesquisa(uri));
		panel.add(BorderLayout.CENTER, new ScrollPane(label));
		SwingUtilities.invokeLater(() -> label.scrollRectToVisible(new Rectangle()));

		return panel;
	}

	@Override
	public String titulo() {
		return "Imagem";
	}

	@Override
	public Icon icone() {
		return Icones.ICON;
	}
}