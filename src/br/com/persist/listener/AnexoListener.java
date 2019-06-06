package br.com.persist.listener;

import br.com.persist.anexo.Anexo;

public interface AnexoListener {
	public void renomearArquivo(Anexo anexo);

	public void imprimirArquivo(Anexo anexo);

	public void excluirArquivo(Anexo anexo);

	public void editarArquivo(Anexo anexo);

	public void abrirArquivo(Anexo anexo);

	public void iconeArquivo(Anexo anexo);
}