package br.com.persist.arvore;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JToolBar;

import br.com.persist.comp.Button;
import br.com.persist.comp.PanelBorder;
import br.com.persist.comp.ScrollPane;
import br.com.persist.listener.ArvoreListener;
import br.com.persist.modelo.ArvoreModelo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Icones;

public class ContainerArvore extends PanelBorder implements ArvoreListener {
	private static final long serialVersionUID = 1L;
	private Arvore arvore = new Arvore(new ArvoreModelo());
	private final Toolbar toolbar = new Toolbar();
	private final Formulario formulario;

	public ContainerArvore(Formulario formulario) {
		this.formulario = formulario;
		montarLayout();
		baixarArquivo();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(arvore));
		add(BorderLayout.NORTH, toolbar);
		arvore.adicionarOuvinte(this);
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIcon("label.baixar", Icones.BAIXAR);

		Toolbar() {
			add(new Button(atualizarAcao));

			atualizarAcao.setActionListener(e -> baixarArquivo());
		}
	}

	@Override
	public void abrirFormArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.abrirArquivo(arquivo.getFile(), false);
		}
	}

	@Override
	public void abrirFichArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.abrirArquivo(arquivo.getFile(), true);
			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
		}
	}

	@Override
	public void atualizarArvore(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
			ArvoreUtil.statusEstrutura(arvore, arquivo);
		}
	}

	private void baixarArquivo() {
		ArvoreModelo modelo = new ArvoreModelo();
		arvore.setModel(modelo);

		List<Arquivo> lista = new ArrayList<>();
		modelo.listar(lista);

		for (Arquivo arquivo : lista) {
			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
		}
	}

	@Override
	public void fecharArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.getFichario().fecharArquivo(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
		}
	}
}