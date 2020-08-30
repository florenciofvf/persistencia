package br.com.persist.fichario;

import br.com.persist.conexao.Conexao;
import br.com.persist.fichario.Fichario.InfoConexao;

public interface FicharioConexao {
	void selecionarConexao(Conexao conexao);

	public InfoConexao getInfoConexao();
}