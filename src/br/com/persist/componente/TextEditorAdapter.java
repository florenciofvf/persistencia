package br.com.persist.componente;

public class TextEditorAdapter implements TextEditorListener {
	private final Runnable focusInputPesquisarRunnable;
	private final Runnable salvarConteudoRunnable;

	public TextEditorAdapter(Runnable focusInputPesquisarRunnable, Runnable salvarConteudoRunnable) {
		this.focusInputPesquisarRunnable = focusInputPesquisarRunnable;
		this.salvarConteudoRunnable = salvarConteudoRunnable;
	}

	public TextEditorAdapter(Runnable focusInputPesquisarRunnable) {
		this(focusInputPesquisarRunnable, null);
	}

	@Override
	public void focusInputPesquisar(TextEditor textEditor) {
		if (focusInputPesquisarRunnable != null) {
			focusInputPesquisarRunnable.run();
		}
	}

	@Override
	public void salvarConteudo(TextEditor textEditor) {
		if (salvarConteudoRunnable != null) {
			salvarConteudoRunnable.run();
		}
	}
}