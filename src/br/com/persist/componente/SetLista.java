package br.com.persist.componente;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		if (lista == null) {
			lista = new ArrayList<>();
		}
		Collections.sort(lista);
		Component comp = Util.getViewParent(c);
		SetListaDialogo form = null;
		if (comp instanceof Frame) {
			form = new SetListaDialogo((Frame) comp, titulo, lista, coletor, config);
		} else if (comp instanceof Dialog) {
			form = new SetListaDialogo((Dialog) comp, titulo, lista, coletor, config);
		} else {
			form = new SetListaDialogo((Frame) null, titulo, lista, coletor, config);
		}
		form.pack();
		form.setSize(form.getWidth(), Constantes.SIZE3.height);
		form.setLocationRelativeTo(comp != null ? comp : c);
		form.setVisible(true);
	}

	public static class Config {
		final String selecionarItemIgual;
		private boolean clickAplicar;
		final boolean obrigatorio;
		final boolean somenteUm;
		String mensagem;
		boolean criar;

		public Config(boolean obrigatorio, boolean somenteUm, String selecionarItemIgual) {
			this.selecionarItemIgual = selecionarItemIgual;
			this.obrigatorio = obrigatorio;
			this.somenteUm = somenteUm;
		}

		public Config(boolean obrigatorio, boolean somenteUm) {
			this(obrigatorio, somenteUm, null);
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

		public String getMensagem() {
			return mensagem;
		}

		public void setMensagem(String mensagem) {
			this.mensagem = mensagem;
		}

		public boolean isClickAplicar() {
			return clickAplicar;
		}

		public void setClickAplicar(boolean clickAplicar) {
			this.clickAplicar = clickAplicar;
		}
	}

	public static class Coletor {
		private List<String> lista;

		public Coletor(String... strings) {
			for (String string : strings) {
				if (!Util.isEmpty(string)) {
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
			return !Util.isEmpty(string) && getLista().contains(string);
		}
	}
}

class Item implements Comparable<Item> {
	private final String rotulo;
	private boolean selecionado;
	private int tag;

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

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public void selecionarSe(Config config) {
		if (rotulo != null && rotulo.equalsIgnoreCase(config.selecionarItemIgual)) {
			selecionado = true;
		}
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

	@Override
	public int compareTo(Item o) {
		return rotulo.compareTo(o.rotulo);
	}
}

class ItemRenderer extends JCheckBox implements ListCellRenderer<Item> {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends Item> list, Item value, int index, boolean isSelected,
			boolean cellHasFocus) {
		if (value.isSelecionado()) {
			setBackground(Color.BLUE);
			setForeground(Color.WHITE);
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setSelected(value.isSelecionado());
		setOpaque(value.isSelecionado());
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
		if (!Util.isEmpty(string)) {
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

	public List<Item> getListaItem() {
		return listaItem;
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
		lista.setModel(criarModel(listaString));
		lista.setCellRenderer(new ItemRenderer());
		setSize(Constantes.SIZE3);
		toolbar.ini(this);
		montarLayout();
		eventos();
	}

	private ListModel<Item> criarModel(List<String> lista) {
		boolean sel = lista.size() == 1 || !config.somenteUm;
		List<Item> listaItem = criarListaItem(lista, sel);
		return new SetListaModelo(listaItem, config);
	}

	private List<Item> criarListaItem(List<String> lista, boolean sel) {
		List<Item> listaItem = new ArrayList<>();
		for (String string : lista) {
			Item item = new Item(string, sel);
			item.selecionarSe(config);
			listaItem.add(item);
		}
		return listaItem;
	}

	private void eventos() {
		lista.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int index = lista.locationToIndex(event.getPoint());
				if (index != -1) {
					Item item = lista.getModel().getElementAt(index);
					checarSomenteUm(item);
					item.setSelecionado(!item.isSelecionado());
					lista.repaint();
				}
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

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.focusInputPesquisar();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(lista));
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action ordenarAcao = actionIcon("label.ordenar", Icones.ASC_TEXTO);
		private Action criarAcao = actionIcon("label.criar", Icones.CRIAR);
		private final CheckBox chkTodos = new CheckBox("label.todos");
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, APLICAR);
			add(ordenarAcao);
			if (!config.somenteUm) {
				add(chkTodos);
			}
			if (config.criar) {
				add(criarAcao);
				criarAcao.setActionListener(e -> criarCampo());
			}
			if (config.mensagem != null) {
				add(label);
				label.setText(" " + config.mensagem);
			}
			add(txtPesquisa);
			add(chkPorParte);
			chkTodos.addActionListener(e -> selecionar(chkTodos.isSelected()));
			txtPesquisa.setText(config.selecionarItemIgual);
			ordenarAcao.setActionListener(e -> ordenar());
			chkTodos.setSelected(!config.somenteUm);
			txtPesquisa.addActionListener(this);
		}

		private void ordenar() {
			List<Item> listaItem = ((SetListaModelo) lista.getModel()).getListaItem();
			Collections.sort(listaItem);
			lista.setModel(new SetListaModelo(listaItem, config));
		}

		private void criarCampo() {
			Object resp = Util.getValorInputDialog(SetListaDialogo.this, "label.atencao",
					Mensagens.getString("label.nome"), null);
			if (resp != null && !Util.isEmpty(resp.toString())) {
				criarCampo(resp.toString().trim());
			}
		}

		private void criarCampo(String nome) {
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
			config.setClickAplicar(true);
			coletor.setLista(listar);
			janela.fechar();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			selecionar(false);
			if (!Util.isEmpty(txtPesquisa.getText())) {
				Item pesquisado = pesquisar(txtPesquisa.getText().toUpperCase(), chkPorParte.isSelected());
				if (pesquisado != null) {
					pesquisado.setSelecionado(true);
					int index = pesquisado.getTag();
					Rectangle rect = lista.getCellBounds(index, index);
					if (rect != null) {
						lista.scrollRectToVisible(rect);
					}
					lista.repaint();
				}
			}
		}

		private Item pesquisar(String string, boolean porParte) {
			ListModel<Item> model = lista.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Item item = model.getElementAt(i);
				if (item.getRotulo() == null) {
					continue;
				}
				if (Util.existeEm(item.getRotulo(), string, porParte)) {
					item.setTag(i);
					return item;
				}
			}
			return null;
		}
	}
}