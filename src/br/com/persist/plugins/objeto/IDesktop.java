package br.com.persist.plugins.objeto;

import java.awt.Point;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.objeto.internal.Argumento;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;

public interface IDesktop {
	public void pesquisar(Conexao conexao, Pesquisa pesquisa, Argumento argumento, boolean soTotal, boolean emForms)
			throws ObjetoException, AssistenciaException;

	public boolean processadoMetadado(Metadado metadado, Point point, boolean labelDireito, boolean checarNomear)
			throws AssistenciaException;

	public void pesquisarDestacar(Pesquisa pesquisa, boolean b);
}