package br.com.persist.plugins.requisicao.conteudo;

import java.awt.Component;
import java.io.InputStream;

import javax.swing.Icon;

public interface RequisicaoConteudo {
	public Component exibir(InputStream is) throws Exception;

	public String titulo();

	public Icon icone();
}