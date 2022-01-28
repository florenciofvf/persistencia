package br.com.persist.plugins.requisicao.conteudo;

import java.awt.Component;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JTextPane;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.ScrollPane;

public class ConteudoHTML implements RequisicaoConteudo {

	@Override
	public Component exibir(InputStream is) throws Exception {
		JTextPane area = new JTextPane();
		area.setContentType("text/html");
		String string = Util.getString(is);
		area.setText(string);
		return new ScrollPane(area);
	}

	@Override
	public String titulo() {
		return "Html";
	}

	@Override
	public Icon icone() {
		return Icones.URL;
	}
}