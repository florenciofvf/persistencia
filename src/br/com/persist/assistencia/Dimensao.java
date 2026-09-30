package br.com.persist.assistencia;

public interface Dimensao {
	public boolean contemScrollHorizontal();

	public boolean contemScrollVertical();

	public void somarALargura(int valor);

	public void somarAAltura(int valor);

	public int getLargura();

	public int getAltura();
}