package br.com.persist.plugins.requisicao.conteudo;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;

import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoException;

public interface RequisicaoConteudo {
	public Component exibir(InputStream is, Tipo parametros)
			throws RequisicaoException, IOException, BadLocationException;

	public String titulo();

	public Icon icone();
}