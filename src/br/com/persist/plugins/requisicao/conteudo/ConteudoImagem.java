package br.com.persist.plugins.requisicao.conteudo;

import java.awt.Component;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;
import br.com.persist.componente.ScrollPane;

public class ConteudoImagem implements RequisicaoConteudo {

	@Override
	public Component exibir(InputStream is) throws Exception {
		Label label = new Label();
		byte[] bytes = Util.getArrayBytes(is);
		label.setIcon(new ImageIcon(bytes));
		return new ScrollPane(label);
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