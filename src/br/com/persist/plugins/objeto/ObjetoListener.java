package br.com.persist.plugins.objeto;

public interface ObjetoListener {
	public void repaint(int x, int y, int largura, int altura);

	public boolean contemId(Objeto objeto, String id);

	public void labelTotalRegistros(Objeto objeto);
}