package br.com.persist.listener;

import br.com.persist.anexo.Anexo;

public interface AnexoListener {
	public void excluirArquivo(Anexo anexo);

	public void editarArquivo(Anexo anexo);

	public void abrirArquivo(Anexo anexo);
}