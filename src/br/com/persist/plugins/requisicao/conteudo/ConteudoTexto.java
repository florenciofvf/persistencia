package br.com.persist.plugins.requisicao.conteudo;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.TextArea;
import br.com.persist.parser.Tipo;

public class ConteudoTexto implements RequisicaoConteudo {

	@Override
	public Component exibir(InputStream is, Tipo parametros) throws IOException {
		TextArea area = new TextArea();
		String string = Util.getString(is);
		area.setText(string);
		return area;
	}

	@Override
	public String titulo() {
		return "Texto";
	}

	@Override
	public Icon icone() {
		return Icones.TEXTO;
	}
}