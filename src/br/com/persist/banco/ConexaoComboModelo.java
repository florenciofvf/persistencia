package br.com.persist.banco;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import br.com.persist.listener.ListaListener;
import br.com.persist.util.ListaArray;

public class ConexaoComboModelo extends AbstractListModel<Conexao> implements ComboBoxModel<Conexao> {
	private static final long serialVersionUID = 1L;
	private final transient List<Conexao> conexoes;
	private transient Object selecionado;

	public ConexaoComboModelo(List<Conexao> conexoes) {
		this.conexoes = conexoes;

		if (getSize() > 0) {
			selecionado = getElementAt(0);
		}

		if (conexoes instanceof ListaArray) {
			((ListaArray<?>) conexoes).setListener(listener);
		}
	}

	transient ListaListener listener = new ListaListener() {
		@Override
		public void adicionado(int indice) {
			fireIntervalAdded(ConexaoComboModelo.this, indice, indice);
		}

		@Override
		public void excluido(int indice) {
			fireIntervalRemoved(ConexaoComboModelo.this, indice, indice);
		}
	};

	public void notificarMudancas() {
		fireContentsChanged(ConexaoComboModelo.this, 0, conexoes.size() - 1);
	}

	@Override
	public int getSize() {
		return conexoes.size();
	}

	@Override
	public Conexao getElementAt(int index) {
		if (index >= 0 && index < conexoes.size()) {
			return conexoes.get(index);
		}

		return null;
	}

	public List<Conexao> getConexoes() {
		return conexoes;
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