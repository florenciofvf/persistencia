package br.com.persist.componente;

public class TextEditorAdapter implements TextEditorListener {
	private final Runnable focusInputPesquisarRunnable;
	private final Runnable salvarConteudoRunnable;
	private final Runnable baixarConteudoRunnable;

	public TextEditorAdapter(Runnable focusInputPesquisarRunnable, Runnable salvarConteudoRunnable,
			Runnable baixarConteudoRunnable) {
		this.focusInputPesquisarRunnable = focusInputPesquisarRunnable;
		this.salvarConteudoRunnable = salvarConteudoRunnable;
		this.baixarConteudoRunnable = baixarConteudoRunnable;
	}

	public TextEditorAdapter(Runnable focusInputPesquisarRunnable) {
		this(focusInputPesquisarRunnable, null, null);
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

	@Override
	public void baixarConteudo(TextEditor textEditor) {
		if (baixarConteudoRunnable != null) {
			baixarConteudoRunnable.run();
		}
	}
}