package br.com.persist.arvore;

import java.awt.BorderLayout;

import javax.swing.JToolBar;

import br.com.persist.comp.Button;
import br.com.persist.comp.PanelBorder;
import br.com.persist.comp.ScrollPane;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Icones;

public class ArvoreContainer extends PanelBorder implements ArvoreListener {
	private static final long serialVersionUID = 1L;
	private Arvore arvore = new Arvore(new ModeloArvore());
	private final Toolbar toolbar = new Toolbar();
	private final Formulario formulario;

	public ArvoreContainer(Formulario formulario) {
		this.formulario = formulario;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(arvore));
		add(BorderLayout.NORTH, toolbar);
		arvore.adicionarOuvinte(this);
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIcon("label.atualizar", Icones.ATUALIZAR);
		// private Action baixarAcao = Action.actionIcon("label.baixar",
		// Icones.BAIXAR);

		Toolbar() {
			add(new Button(atualizarAcao));
			// add(new Button(baixarAcao));

			eventos();
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizarArvore(arvore));

			// baixarAcao.setActionListener(e -> {
			// if (arquivo == null) {
			// btnSelecao.click();
			// return;
			// }
			//
			// try {
			// excluido();
			// StringBuilder sbConexao = new StringBuilder();
			// List<Relacao> relacoes = new ArrayList<>();
			// List<Objeto> objetos = new ArrayList<>();
			// List<Form> forms = new ArrayList<>();
			// Dimension d = XML.processar(arquivo, objetos, relacoes, forms,
			// sbConexao);
			// abrir(arquivo, objetos, relacoes, forms, sbConexao, null, d);
			// } catch (Exception ex) {
			// Util.stackTraceAndMessage("BAIXAR: " + arquivo.getAbsolutePath(),
			// ex, formulario);
			// }
			// });
		}
	}

	@Override
	public void abrirFormArquivo(Arvore arvore) {
	}

	@Override
	public void abrirFichArquivo(Arvore arvore) {
	}

	@Override
	public void atualizarArvore(Arvore arvore) {
		arvore.setModel(new ModeloArvore());
	}

	@Override
	public void excluirArquivo(Arvore arvore) {
	}
}