package br.com.persist.conexao;

import java.util.List;

@FunctionalInterface
public interface ConexaoProvedor {
	public List<Conexao> getConexoes();
}