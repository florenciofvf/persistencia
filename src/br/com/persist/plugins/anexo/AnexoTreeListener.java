package br.com.persist.plugins.anexo;

public interface AnexoTreeListener {
	public void copiarAtributosAnexo(AnexoTree anexoTree);

	public void colarAtributosAnexo(AnexoTree anexoTree);

	public void diretorioAnexo(AnexoTree anexoTree);

	public void renomearAnexo(AnexoTree anexoTree);

	public void imprimirAnexo(AnexoTree anexoTree);

	public void corFonteAnexo(AnexoTree anexoTree);

	public void excluirAnexo(AnexoTree anexoTree);

	public void editarAnexo(AnexoTree anexoTree);

	public void abrirAnexo(AnexoTree anexoTree);

	public void iconeAnexo(AnexoTree anexoTree);
}