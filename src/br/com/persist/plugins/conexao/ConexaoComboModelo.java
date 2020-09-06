package br.com.persist.plugins.conexao;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class ConexaoComboModelo extends AbstractListModel<Conexao> implements ComboBoxModel<Conexao> {
	private static final long serialVersionUID = 1L;
	private final transient List<Conexao> lista;
	private transient Object selecionado;

	public ConexaoComboModelo(List<Conexao> lista) {
		this.lista = lista;

		if (getSize() > 0) {
			selecionado = getElementAt(0);
		}
	}

	public void notificarMudancas() {
		fireContentsChanged(ConexaoComboModelo.this, 0, getSize() - 1);
	}

	@Override
	public int getSize() {
		return lista.size();
	}

	@Override
	public Conexao getElementAt(int index) {
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