package br.com.persist.plugins.requisicao.conteudo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.parser.Parser;
import br.com.persist.parser.Tipo;

public class ConteudoJSON extends RequisicaoHeader {
	private final Parser parser = new Parser();

	@Override
	public Component exibir(InputStream is) throws Exception {
		JTextPane area = new JTextPane();
		area.setText(Constantes.VAZIO);
		String string = Util.getString(is);
		StyledDocument styledDoc = area.getStyledDocument();
		Tipo json = parser.parse(string);
		if (styledDoc instanceof AbstractDocument) {
			AbstractDocument doc = (AbstractDocument) styledDoc;
			json.toString(doc, false, 0);
		}
		String accessToken = getAccessToken(json);
		setAccesToken(accessToken);
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, area);
		return new ScrollPane(panel);
	}

	@Override
	public String titulo() {
		return "Json";
	}

	@Override
	public Icon icone() {
		return Icones.CONFIG;
	}
}