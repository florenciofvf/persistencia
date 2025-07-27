package br.com.persist.plugins.conexao;

import br.com.persist.assistencia.Evento;

public class ConexaoEvento implements Evento {
	public static final String COLETAR_INFO_CONEXAO = "COLETAR_INFO_CONEXAO";
	public static final String SELECIONAR_CONEXAO = "SELECIONAR_CONEXAO";

	private ConexaoEvento() {
	}
}