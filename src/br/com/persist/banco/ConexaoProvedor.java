package br.com.persist.banco;

import java.util.List;

@FunctionalInterface
public interface ConexaoProvedor {
	public List<Conexao> getConexoes();
}