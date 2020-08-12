package br.com.persist.anexo;

public interface AnexoTreeListener {
	public void copiarAtributosArquivo(AnexoTree anexoTree);

	public void colarAtributosArquivo(AnexoTree anexoTree);

	public void renomearArquivo(AnexoTree anexoTree);

	public void imprimirArquivo(AnexoTree anexoTree);

	public void corFonteArquivo(AnexoTree anexoTree);

	public void excluirArquivo(AnexoTree anexoTree);

	public void editarArquivo(AnexoTree anexoTree);

	public void abrirArquivo(AnexoTree anexoTree);

	public void pastaArquivo(AnexoTree anexoTree);

	public void iconeArquivo(AnexoTree anexoTree);
}