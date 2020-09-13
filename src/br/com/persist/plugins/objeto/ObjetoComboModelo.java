package br.com.persist.plugins.objeto;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class ObjetoComboModelo extends AbstractListModel<Objeto> implements ComboBoxModel<Objeto> {
	private static final long serialVersionUID = 1L;
	private final transient List<Objeto> objetos;
	private transient Object selecionado;

	public ObjetoComboModelo(List<Objeto> objetos) {
		this.objetos = objetos;

		if (getSize() > 0) {
			selecionado = getElementAt(0);
		}
	}

	@Override
	public int getSize() {
		return objetos.size();
	}

	@Override
	public Objeto getElementAt(int index) {
		if (index >= 0 && index < objetos.size()) {
			return objetos.get(index);
		}

		return null;
	}

	public List<Objeto> getObjetos() {
		return objetos;
	}

	@Override
	public void setSelectedItem(Object object) {
		if ((selecionado != null && !selecionado.equals(object)) || selecionado == null && object != null) {
			selecionado = object;
			fireContentsChanged(this, -1, -1);
		}
	}

	@Override
	public Object getSelectedItem() {
		return selecionado;
	}
}