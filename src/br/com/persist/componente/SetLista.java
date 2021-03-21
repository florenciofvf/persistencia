package br.com.persist.componente;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetLista.Coletor;

public class SetLista {
	private SetLista() {
	}

	public static void view(String titulo, List<String> lista, Coletor coletor, Component c) {
		SetListaDialogo form = new SetListaDialogo(titulo, lista, coletor);
		form.setLocationRelativeTo(c);
		form.setVisible(true);
	}

	public static class Coletor {
		private List<String> lista;

		public Coletor(String... strings) {
			for (String string : strings) {
				if (!Util.estaVazio(string)) {
					getLista().add(string);
				}
			}
		}

		public List<String> getLista() {
			if (lista == null) {
				lista = new ArrayList<>();
			}
			return lista;
		}

		public void setLista(List<String> lista) {
			this.lista = lista;
		}

		public boolean estaVazio() {
			return getLista().isEmpty();
		}

		public int size() {
			return getLista().size();
		}

		public String get(int i) {
			return getLista().get(i);
		}

		public boolean contem(String string) {
			return !Util.estaVazio(string) && getLista().contains(string);
		}
	}
}

class Item {
	private final String rotulo;
	private boolean selecionado;

	public Item(String rotulo) {
		this.rotulo = rotulo;
		selecionado = true;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getRotulo() {
		return rotulo;
	}

	public String toString() {
		return rotulo;
	}
}

class ItemRenderer extends JCheckBox implements ListCellRenderer<Item> {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends Item> list, Item value, int index, boolean isSelected,
			boolean cellHasFocus) {
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		setSelected(value.isSelecionado());
		setText(value.toString());
		setFont(list.getFont());
		return this;
	}
}

class SetListaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private JList<Item> lista = new JList<>();
	private final transient Coletor coletor;

	SetListaDialogo(String titulo, List<String> listaString, Coletor coletor) {
		super((Frame) null, titulo + " [" + listaString.size() + "]");
		lista.setCellRenderer(new ItemRenderer());
		lista.setModel(criarModel(listaString));
		setSize(Constantes.SIZE3);
		this.coletor = coletor;
		toolbar.ini(this);
		montarLayout();
		eventos();
	}

	private ListModel<Item> criarModel(List<String> lista) {
		List<Item> listaItem = criarListaItem(lista);
		return criarModelo(listaItem);
	}

	private List<Item> criarListaItem(List<String> lista) {
		List<Item> listaItem = new ArrayList<>();
		for (String string : lista) {
			listaItem.add(new Item(string));
		}
		return listaItem;
	}

	private ListModel<Item> criarModelo(List<Item> listaItem) {
		return new AbstractListModel<Item>() {
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return listaItem.size();
			}

			public Item getElementAt(int i) {
				return listaItem.get(i);
			}
		};
	}

	private void eventos() {
		lista.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int index = lista.locationToIndex(event.getPoint());
				Item item = lista.getModel().getElementAt(index);
				item.setSelecionado(!item.isSelecionado());
				lista.repaint(lista.getCellBounds(index, index));
			}
		});
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(lista));
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private final CheckBox chkTodos = new CheckBox("label.todos");

		public void ini(Janela janela) {
			super.ini(janela, APLICAR);
			add(chkTodos);
			chkTodos.setSelected(true);
			chkTodos.addActionListener(e -> selecionar(chkTodos.isSelected()));
		}

		private void selecionar(boolean b) {
			ListModel<Item> model = lista.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				model.getElementAt(i).setSelecionado(b);
			}
			lista.repaint();
		}

		@Override
		protected void aplicar() {
			List<String> listar = new ArrayList<>();
			ListModel<Item> model = lista.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				if (model.getElementAt(i).isSelecionado()) {
					listar.add(model.getElementAt(i).getRotulo());
				}
			}
			coletor.setLista(listar);
			janela.fechar();
		}
	}
}