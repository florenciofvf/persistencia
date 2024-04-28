package br.com.persist.plugins.objeto.vinculo;

import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;

public class OrdenarArrastoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final OrdenarArrastoContainer container;

	private OrdenarArrastoDialogo(String titulo, OrdenarListener listener) {
		super((Dialog) null, titulo);
		container = new OrdenarArrastoContainer(this, listener);
		setTitle(listener.getPesquisas().size() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Component c, String titulo, OrdenarListener listener) {
		OrdenarArrastoDialogo dialog = new OrdenarArrastoDialogo(titulo, listener);
		dialog.setLocationRelativeTo(Util.getViewParent(c));
		dialog.setVisible(true);
	}
}

class OrdenarArrastoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient OrdenarListener listener;
	private final Toolbar toolbar = new Toolbar();

	public OrdenarArrastoContainer(Janela janela, OrdenarListener listener) {
		this.listener = Objects.requireNonNull(listener);
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new OrdenarArrastoPainelArea(listener.getPesquisas()));
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, SALVAR);
		}

		@Override
		protected void salvar() {
			listener.salvar();
			fechar();
		}
	}
}

class OrdenarArrastoPainelArea extends Panel {
	private final transient OrdenarArrastoPesquisaItem[] itens;
	private static final long serialVersionUID = 1L;
	private final List<Pesquisa> pesquisas;

	public OrdenarArrastoPainelArea(List<Pesquisa> pesquisas) {
		this.pesquisas = pesquisas;
		itens = new OrdenarArrastoPesquisaItem[pesquisas.size()];
		for (int i = 0; i < itens.length; i++) {
			itens[i] = new OrdenarArrastoPesquisaItem(pesquisas.get(i));
		}
		Arrays.sort(itens, (o1, o2) -> o1.ordemOriginal - o2.ordemOriginal);
		empilharItens();
	}

	void empilharItens() {
		int y = 30;
		for (OrdenarArrastoPesquisaItem item : itens) {
			item.y = y;
			y += 30;
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		int largura = getWidth() - 20;
		for (OrdenarArrastoPesquisaItem item : itens) {
			item.largura = largura;
			item.desenhar(g2);
		}
	}
}

class OrdenarArrastoPesquisaItem {
	final Pesquisa pesquisa;
	final String nome;
	int ordemOriginal;
	int alturaM = 19;
	int altura = 30;
	int largura;
	int x = 10;
	int y;

	OrdenarArrastoPesquisaItem(Pesquisa pesquisa) {
		nome = pesquisa.getNomeParaMenuItem();
		ordemOriginal = pesquisa.getOrdem();
		this.pesquisa = pesquisa;
	}

	void desenhar(Graphics2D g2) {
		g2.drawRoundRect(x, y, largura, altura, 5, 5);
		g2.drawString(nome, x + 20, y + alturaM);
	}
}