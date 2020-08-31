package br.com.persist.conexao;

import java.util.ArrayList;
import java.util.List;

public class ConexaoColetor {
	private final List<Conexao> conexoes;

	public ConexaoColetor() {
		conexoes = new ArrayList<>();
	}

	public void init() {
		conexoes.clear();
	}

	public List<Conexao> getConexoes() {
		return conexoes;
	}
}