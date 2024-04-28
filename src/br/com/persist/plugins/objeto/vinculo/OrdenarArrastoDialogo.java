package br.com.persist.plugins.objeto.vinculo;

import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
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
		dialog.setSize(Constantes.SIZE.width / 2, Constantes.SIZE.height / 2);
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
	transient OrdenarArrastoPesquisaItem selecionado;
	private static final long serialVersionUID = 1L;
	private int ultY;

	public OrdenarArrastoPainelArea(List<Pesquisa> pesquisas) {
		Collections.sort(pesquisas, (Pesquisa o1, Pesquisa o2) -> o1.getOrdem() - o2.getOrdem());
		itens = new OrdenarArrastoPesquisaItem[pesquisas.size()];
		for (int i = 0; i < itens.length; i++) {
			itens[i] = new OrdenarArrastoPesquisaItem(pesquisas.get(i));
		}
		empilharItens();
		addMouseMotionListener(mouseAdapterArrasto);
		addMouseListener(mouseAdapterArrasto);
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

	private transient MouseAdapter mouseAdapterArrasto = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			selecionado = null;
			int x = e.getX();
			ultY = e.getY();
			for (OrdenarArrastoPesquisaItem item : itens) {
				if (item.contem(x, ultY)) {
					selecionado = item;
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int recY = e.getY();
			if (selecionado != null) {
				selecionado.y += recY - ultY;
			}
			ultY = recY;
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			ultY = e.getY();
			Arrays.sort(itens, (o1, o2) -> o1.y - o2.y);
			empilharItens();
			for (int i = 0; i < itens.length; i++) {
				OrdenarArrastoPesquisaItem item = itens[i];
				item.pesquisa.setOrdem(i);
			}
			repaint();
		}
	};
}

class OrdenarArrastoPesquisaItem {
	final Pesquisa pesquisa;
	final String nome;
	int alturaM = 19;
	int altura = 30;
	int largura;
	int x = 10;
	int y;

	OrdenarArrastoPesquisaItem(Pesquisa pesquisa) {
		nome = pesquisa.getNomeParaMenuItem();
		this.pesquisa = pesquisa;
	}

	void desenhar(Graphics2D g2) {
		g2.drawRoundRect(x, y, largura, altura, 5, 5);
		g2.drawString(pesquisa.getOrdem() + ": " + nome, x + 20, y + alturaM);
	}

	public boolean contem(int x, int y) {
		return (x >= this.x && x <= this.x + largura) && (y >= this.y && y <= this.y + altura);
	}
}