package br.com.persist.plugins.objeto.config;

import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.macro.MacroProvedor;
import br.com.persist.util.Imagens;

public class IconeContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final transient Objeto objeto;
	private final Label label;
	private int totalIcones;

	public IconeContainer(Janela janela, Objeto objeto, Label label) {
		this.objeto = objeto;
		toolbar.ini(janela);
		this.label = label;
		montarLayout();
	}

	private void montarLayout() {
		List<Entry<String, Icon>> icones = Imagens.getIcones();
		Panel matriz = new Panel(new GridLayout(0, 25));

		for (Map.Entry<String, Icon> entry : icones) {
			matriz.add(new LabelIcone(entry));
		}

		add(BorderLayout.CENTER, new ScrollPane(matriz));
		add(BorderLayout.NORTH, toolbar);
		totalIcones = icones.size();
	}

	public int getTotalIcones() {
		return totalIcones;
	}

	private class LabelIcone extends Label {
		private static final long serialVersionUID = 1L;
		private final String nome;

		private LabelIcone(Map.Entry<String, Icon> entry) {
			addMouseListener(mouseListenerInner);
			setHorizontalAlignment(CENTER);
			setIcon(entry.getValue());
			nome = entry.getKey();
			setToolTipText(nome);
			if (nome.equals(objeto.getIcone())) {
				setBorder(BorderFactory.createLineBorder(Color.BLUE));
			}
		}

		private transient MouseListener mouseListenerInner = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				label.setIcon(getIcon());
				objeto.setIcone(nome);
				MacroProvedor.imagem(objeto.getIcone());
				toolbar.fechar();
			}
		};
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR);
		}

		@Override
		protected void limpar() {
			MacroProvedor.imagem(null);
			objeto.limparIcone();
			label.setIcon(null);
			fechar();
		}
	}
}