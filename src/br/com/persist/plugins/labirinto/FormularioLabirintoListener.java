package br.com.persist.plugins.labirinto;

public interface FormularioLabirintoListener {
	public void fechado(FormularioLabirinto formulario);

	public void alterado(Matriz matriz, Pivo pivo);
}