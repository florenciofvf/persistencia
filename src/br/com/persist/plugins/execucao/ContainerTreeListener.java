package br.com.persist.plugins.execucao;

public interface ContainerTreeListener {
	public void executar(ContainerTree tree, boolean confirmar);

	public void executarVar(ContainerTree tree);
}