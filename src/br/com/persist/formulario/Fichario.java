package br.com.persist.formulario;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.util.Mensagens;

public class Fichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private Ponto ponto;

	public Fichario() {
		addMouseListener(new Listener());
	}

	public void novo(Formulario formulario) {
		Container container = new Container(formulario);
		addTab(Mensagens.getString("label.novo"), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	public void abrir(Formulario formulario, File file, List<Objeto> objetos, List<Relacao> relacoes) {
		Container container = new Container(formulario);
		container.abrir(file, objetos, relacoes);
		addTab(Mensagens.getString("label.novo"), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this);
		setTabComponentAt(ultimoIndice, tituloAba);
		setTitleAt(ultimoIndice, file.getName());
		setSelectedIndex(ultimoIndice);
	}

	private void inverter(int origem, int destino) {
		Component aba = getTabComponentAt(origem);
		Component cmp = getComponentAt(origem);
		String hint = getToolTipTextAt(origem);
		String titulo = getTitleAt(origem);
		Icon icon = getIconAt(origem);
		remove(origem);

		insertTab(titulo, icon, cmp, hint, destino);
		setTabComponentAt(destino, aba);
		setSelectedIndex(destino);
	}

	private class Listener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			if (ponto == null) {
				return;
			}

			int destino = indexAtLocation(e.getX(), e.getY());
			int origem = indexAtLocation(ponto.x, ponto.y);

			if (origem != -1 && destino != -1 && origem != destino) {
				inverter(origem, destino);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			ponto = new Ponto(e.getX(), e.getY());
		}
	}

	private class Ponto {
		final int x;
		final int y;

		Ponto(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}