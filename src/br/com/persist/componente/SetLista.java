package br.com.persist.componente;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.componente.SetLista.Config;

public class SetLista {
	private SetLista() {
	}

	public static void view(String titulo, List<String> lista, Coletor coletor, Component c, Config config) {
		if (config == null) {
			config = new Config();
		}
		Component comp = Util.getViewParent(c);
		SetListaDialogo form = null;
		if (comp instanceof Frame) {
			form = new SetListaDialogo((Frame) comp, titulo, lista, coletor, config);
		} else if (comp instanceof Dialog) {
			form = new SetListaDialogo((Dialog) comp, titulo, lista, coletor, config);
		} else {
			form = new SetListaDialogo((Frame) null, titulo, lista, coletor, config);
		}
		form.setLocationRelativeTo(comp != null ? comp : c);
		form.setVisible(true);
	}

	public static class Config {
		final boolean obrigatorio;
		final boolean somenteUm;
		boolean criar;

		public Config(boolean obrigatorio, boolean somenteUm) {
			this.obrigatorio = obrigatorio;
			this.somenteUm = somenteUm;
		}

		public Config(boolean somenteUm) {
			this(false, somenteUm);
		}

		public Config() {
			this(false, false);
		}

		public boolean isCriar() {
			return criar;
		}

		public void setCriar(boolean criar) {
			this.criar = criar;
		}
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

	public Item(String rotulo, boolean sel) {
		this.rotulo = rotulo;
		selecionado = sel;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rotulo == null) ? 0 : rotulo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (rotulo == null) {
			if (other.rotulo != null)
				return false;
		} else if (!rotulo.equalsIgnoreCase(other.rotulo)) {
			return false;
		}
		return true;
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

class SetListaModelo extends AbstractListModel<Item> {
	private static final long serialVersionUID = 1L;
	private final transient List<Item> listaItem;
	private final transient Config config;

	public SetListaModelo(List<Item> listaItem, Config config) {
		this.listaItem = listaItem;
		this.config = config;
	}

	public int getSize() {
		return listaItem.size();
	}

	public Item getElementAt(int i) {
		return listaItem.get(i);
	}

	protected void notificarMudancas() {
		fireContentsChanged(SetListaModelo.this, 0, getSize() - 1);
	}

	public void addItem(String string) {
		if (!Util.estaVazio(string)) {
			Item item = new Item(string, !config.somenteUm);
			if (!listaItem.contains(item)) {
				listaItem.add(item);
				if (config.somenteUm) {
					for (Item it : listaItem) {
						it.setSelecionado(false);
					}
					item.setSelecionado(true);
				}
				notificarMudancas();
			}
		}
	}
}

class SetListaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final JList<Item> lista = new JList<>();
	private final Toolbar toolbar = new Toolbar();
	private final transient Coletor coletor;
	private final transient Config config;

	SetListaDialogo(Frame frame, String titulo, List<String> listaString, Coletor coletor, Config config) {
		super(frame, titulo + " [" + listaString.size() + "]");
		this.coletor = coletor;
		this.config = config;
		init(listaString);
	}

	SetListaDialogo(Dialog dialog, String titulo, List<String> listaString, Coletor coletor, Config config) {
		super(dialog, titulo + " [" + listaString.size() + "]");
		this.coletor = coletor;
		this.config = config;
		init(listaString);
	}

	private void init(List<String> listaString) {
		Collections.sort(listaString);
		lista.setModel(criarModel(listaString, config));
		lista.setCellRenderer(new ItemRenderer());
		setSize(Constantes.SIZE3);
		toolbar.ini(this);
		montarLayout();
		eventos();
	}

	private ListModel<Item> criarModel(List<String> lista, Config config) {
		boolean sel = lista.size() == 1 || !config.somenteUm;
		List<Item> listaItem = criarListaItem(lista, sel);
		return new SetListaModelo(listaItem, config);
	}

	private List<Item> criarListaItem(List<String> lista, boolean sel) {
		List<Item> listaItem = new ArrayList<>();
		for (String string : lista) {
			listaItem.add(new Item(string, sel));
		}
		return listaItem;
	}

	private void eventos() {
		lista.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int index = lista.locationToIndex(event.getPoint());
				Item item = lista.getModel().getElementAt(index);
				checarSomenteUm(item);
				item.setSelecionado(!item.isSelecionado());
				lista.repaint();
			}

			private void checarSomenteUm(Item item) {
				if (config.somenteUm) {
					ListModel<Item> model = lista.getModel();
					for (int i = 0; i < model.getSize(); i++) {
						Item a = model.getElementAt(i);
						if (a != item) {
							a.setSelecionado(false);
						}
					}
				}
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
		private Action criarAcao = Action.actionIcon("label.criar", Icones.CRIAR);

		public void ini(Janela janela) {
			super.ini(janela, APLICAR);
			add(chkTodos);
			if (config.criar) {
				add(criarAcao);
				criarAcao.setActionListener(e -> criarCampo());
			}
			chkTodos.setSelected(!config.somenteUm);
			chkTodos.addActionListener(e -> selecionar(chkTodos.isSelected()));
		}

		private void criarCampo() {
			Object resp = Util.getValorInputDialog(SetListaDialogo.this, "label.atencao",
					Mensagens.getString("label.nome"), null);
			if (resp != null && !Util.estaVazio(resp.toString())) {
				crarCampo(resp.toString().trim());
			}
		}

		private void crarCampo(String nome) {
			((SetListaModelo) lista.getModel()).addItem(nome);
		}

		private void selecionar(boolean b) {
			ListModel<Item> model = lista.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Item item = model.getElementAt(i);
				if (config.somenteUm) {
					item.setSelecionado(false);
				} else {
					item.setSelecionado(b);
				}
			}
			lista.repaint();
		}

		@Override
		protected void aplicar() {
			List<String> listar = new ArrayList<>();
			ListModel<Item> model = lista.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Item item = model.getElementAt(i);
				if (item.isSelecionado()) {
					listar.add(item.getRotulo());
				}
			}
			if (config.obrigatorio && listar.isEmpty()) {
				if (config.somenteUm) {
					Util.mensagem(SetListaDialogo.this, Mensagens.getString("msg.selecione_um"));
				} else {
					Util.mensagem(SetListaDialogo.this, Mensagens.getString("msg.selecione_mais"));
				}
				return;
			}
			coletor.setLista(listar);
			janela.fechar();
		}
	}
}