package br.com.persist.plugins.objeto;

public interface ObjetoListener {
	public void repaint(int x, int y, int largura, int altura);

	public void labelTotalRegistros(Objeto objeto);

	public boolean contemId(String id);
}