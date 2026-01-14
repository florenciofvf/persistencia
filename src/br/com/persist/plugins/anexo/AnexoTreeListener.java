package br.com.persist.plugins.anexo;

import br.com.persist.assistencia.AssistenciaException;

public interface AnexoTreeListener {
	public void copiarAtributosAnexo(AnexoTree anexoTree);

	public void focusInputPesquisar(AnexoTree anexoTree);

	public void colarAtributosAnexo(AnexoTree anexoTree) throws AssistenciaException;

	public void copiarSeLinkAnexo(AnexoTree anexoTree);

	public void diretorioAnexo(AnexoTree anexoTree);

	public void conteudoAnexo(AnexoTree anexoTree);

	public void renomearAnexo(AnexoTree anexoTree);

	public void imprimirAnexo(AnexoTree anexoTree);

	public void corFonteAnexo(AnexoTree anexoTree);

	public void excluirAnexo(AnexoTree anexoTree);

	public void editarAnexo(AnexoTree anexoTree);

	public void clonarAnexo(AnexoTree anexoTree);

	public void abrirAnexo(AnexoTree anexoTree);

	public void iconeAnexo(AnexoTree anexoTree);
}