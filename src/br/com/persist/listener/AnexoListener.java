package br.com.persist.listener;

import br.com.persist.anexo.Anexo;

public interface AnexoListener {
	public void selecionarArquivo(Anexo anexo);

	public void abrirFormArquivo(Anexo anexo);

	public void abrirFichArquivo(Anexo anexo);

	public void atualizarArvore(Anexo anexo);

	public void fecharArquivo(Anexo anexo);

	public void clickArquivo(Anexo anexo);
}