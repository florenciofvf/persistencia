package br.com.persist.assistencia;

public interface Dimensao {
	public boolean contemScrollHorizontal();

	public boolean contemScrollVertical();

	public void somarALargura(int valor);

	public void checarMaximoHorizontal();

	public void somarAAltura(int valor);

	public void setLargura(int largura);

	public void checarMaximoVertical();

	public void setAltura(int altura);

	public int getLargura();

	public int getAltura();
}