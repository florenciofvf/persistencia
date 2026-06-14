package br.com.persist.assistencia;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class StringComboModelo extends AbstractListModel<String> implements ComboBoxModel<String> {
	private static final long serialVersionUID = 1L;
	private final transient List<String> lista;
	private transient Object selecionado;

	public StringComboModelo(List<String> lista) {
		this.lista = lista;
		if (getSize() > 0) {
			selecionado = getElementAt(0);
		}
	}

	public void notificarMudancas() {
		fireContentsChanged(StringComboModelo.this, 0, getSize() - 1);
	}

	@Override
	public int getSize() {
		return lista.size();
	}

	@Override
	public String getElementAt(int index) {
		if (index >= 0 && index < lista.size()) {
			return lista.get(index);
		}
		return null;
	}

	@Override
	public void setSelectedItem(Object object) {
		if ((selecionado != null && !selecionado.equals(object)) || (selecionado == null && object != null)) {
			selecionado = object;
			fireContentsChanged(this, -1, -1);
		}
	}

	@Override
	public Object getSelectedItem() {
		return selecionado;
	}
}