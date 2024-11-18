package br.com.persist.componente;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
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

public class SetListaCheck {
	private SetListaCheck() {
	}

	public static void view(String titulo, List<CheckBox> listaCheck, Component c) {
		Component comp = Util.getViewParent(c);
		SetListaCheckDialogo form = null;
		if (comp instanceof Frame) {
			form = new SetListaCheckDialogo((Frame) comp, titulo, listaCheck);
		} else if (comp instanceof Dialog) {
			form = new SetListaCheckDialogo((Dialog) comp, titulo, listaCheck);
		} else {
			form = new SetListaCheckDialogo((Frame) null, titulo, listaCheck);
		}
		form.setLocationRelativeTo(comp != null ? comp : c);
		form.setVisible(true);
	}
}

class ItemCheck {
	private final CheckBox check;

	public ItemCheck(CheckBox check) {
		this.check = check;
	}

	public boolean isSelecionado() {
		return check.isSelected();
	}

	public void setSelecionado(boolean selecionado) {
		check.setSelected(selecionado);
	}

	@Override
	public String toString() {
		return check.getText();
	}
}

class ItemCheckRenderer extends JCheckBox implements ListCellRenderer<ItemCheck> {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends ItemCheck> listaItem, ItemCheck itemCheck, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (itemCheck.isSelecionado()) {
			setBackground(Color.BLUE);
			setForeground(Color.WHITE);
		} else {
			setBackground(listaItem.getBackground());
			setForeground(listaItem.getForeground());
		}
		setSelected(itemCheck.isSelecionado());
		setText(itemCheck.toString());
		setFont(listaItem.getFont());
		setOpaque(true);
		return this;
	}
}

class SetListaCheckModelo extends AbstractListModel<ItemCheck> {
	private final transient List<ItemCheck> listaItem;
	private static final long serialVersionUID = 1L;

	public SetListaCheckModelo(List<ItemCheck> listaCheck) {
		this.listaItem = listaCheck;
	}

	public int getSize() {
		return listaItem.size();
	}

	public ItemCheck getElementAt(int i) {
		return listaItem.get(i);
	}

	public List<ItemCheck> getListaItem() {
		return listaItem;
	}
}

class SetListaCheckDialogo extends AbstratoDialogo {
	private final JList<ItemCheck> listaItem = new JList<>();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();

	SetListaCheckDialogo(Frame frame, String titulo, List<CheckBox> listaCheck) {
		super(frame, titulo + " [" + listaCheck.size() + "]");
		init(listaCheck);
	}

	SetListaCheckDialogo(Dialog dialog, String titulo, List<CheckBox> listaCheck) {
		super(dialog, titulo + " [" + listaCheck.size() + "]");
		init(listaCheck);
	}

	private void init(List<CheckBox> listaCheck) {
		listaItem.setCellRenderer(new ItemCheckRenderer());
		listaItem.setModel(criarModel(listaCheck));
		setSize(Constantes.SIZE3);
		toolbar.ini(this);
		montarLayout();
		eventos();
	}

	private ListModel<ItemCheck> criarModel(List<CheckBox> listaCheck) {
		List<ItemCheck> lista = criarListaItem(listaCheck);
		return new SetListaCheckModelo(lista);
	}

	private List<ItemCheck> criarListaItem(List<CheckBox> listaCheck) {
		List<ItemCheck> lista = new ArrayList<>();
		for (CheckBox item : listaCheck) {
			lista.add(new ItemCheck(item));
		}
		return lista;
	}

	private void eventos() {
		listaItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int index = listaItem.locationToIndex(event.getPoint());
				if (index != -1) {
					ItemCheck item = listaItem.getModel().getElementAt(index);
					item.setSelecionado(!item.isSelecionado());
					listaItem.repaint();
				}
			}
		});
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(listaItem));
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, APLICAR);
		}

		@Override
		protected void aplicar() {
			janela.fechar();
		}
	}
}