package br.com.persist.icone;

import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import br.com.persist.abstrato.PluginBasico;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Busca;
import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;

public class IconeContainer extends Panel implements PluginBasico {
	private static final long serialVersionUID = 1L;
	private final List<LabelIcone> listaLabelIcone;
	private final transient IconeListener listener;
	private final Toolbar toolbar = new Toolbar();
	private static String nomeIconeCopiado;
	private final transient Object objeto;
	private final Label labelIcone;
	private final String iconeSel;
	private int totalIcones;

	public IconeContainer(Janela janela, IconeListener listener, String iconeSel) {
		this.listener = Objects.requireNonNull(listener);
		this.labelIcone = listener.getOptLabel();
		this.objeto = listener.getOptObjeto();
		listaLabelIcone = new ArrayList<>();
		this.iconeSel = iconeSel;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		List<Entry<String, Icon>> icones = Imagens.getIcones();
		Panel matriz = new Panel(new GridLayout(0, 25));
		for (Map.Entry<String, Icon> entry : icones) {
			LabelIcone icone = new LabelIcone(entry);
			listaLabelIcone.add(icone);
			matriz.add(icone);
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
			selecionar(iconeSel);
		}

		private void selecionar(String nomeIcone) {
			if (nome.equalsIgnoreCase(nomeIcone)) {
				setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
			}
		}

		private transient MouseListener mouseListenerInner = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if (labelIcone != null) {
						labelIcone.setToolTipText(nome);
						labelIcone.setIcon(getIcon());
					}
					listener.setIcone(objeto, nome, getIcon());
					toolbar.fechar();
				} catch (AssistenciaException ex) {
					Util.mensagem(IconeContainer.this, ex.getMessage());
				}
			}
		};
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private transient IconePesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR, COPIAR);
			add(true, txtPesquisa);
			add(chkPorParte);
			add(label);
			txtPesquisa.addActionListener(e -> selecionar());
		}

		private void selecionar() {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				pesquisa = getPesquisa(listaLabelIcone, pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
				pesquisa.selecionar(label);
			} else {
				label.limpar();
			}
		}

		public IconePesquisa getPesquisa(List<LabelIcone> listaLabelIcone, IconePesquisa pesquisa, String string,
				boolean porParte) {
			if (pesquisa == null) {
				return new IconePesquisa(listaLabelIcone, string, porParte);
			} else if (pesquisa.igual(string, porParte)) {
				return pesquisa;
			}
			return new IconePesquisa(listaLabelIcone, string, porParte);
		}

		@Override
		protected void copiar() {
			setNomeIconeCopiado(iconeSel);
			fechar();
		}

		@Override
		protected void limpar() {
			listener.limparIcone(objeto);
			if (labelIcone != null) {
				labelIcone.setIcon(null);
			}
			fechar();
		}
	}

	public static String getNomeIconeCopiado() {
		return nomeIconeCopiado;
	}

	public static void setNomeIconeCopiado(String nomeIconeCopiado) {
		IconeContainer.nomeIconeCopiado = nomeIconeCopiado;
	}

	class IconePesquisa implements Busca {
		private List<LabelIcone> lista = new ArrayList<>();
		private final List<LabelIcone> listaLabelIcone;
		final boolean porParte;
		final String string;
		int indice;

		public IconePesquisa(List<LabelIcone> listaLabelIcone, String string, boolean porParte) {
			this.listaLabelIcone = Objects.requireNonNull(listaLabelIcone);
			this.string = Objects.requireNonNull(string);
			this.porParte = porParte;
			if (!Util.isEmpty(string)) {
				inicializar();
			}
		}

		private void inicializar() {
			indice = 0;
			for (LabelIcone item : listaLabelIcone) {
				if (Util.existeEm(item.nome, string, porParte)) {
					lista.add(item);
				}
			}
		}

		public boolean igual(String string, boolean porParte) {
			return Util.iguaisEm(this.string, string, this.porParte, porParte);
		}

		public String getString() {
			return string;
		}

		public int getTotal() {
			return lista.size();
		}

		public int getIndice() {
			return indice;
		}

		public void selecionar(Label label) {
			if (label == null) {
				return;
			}
			if (indice < getTotal()) {
				LabelIcone item = lista.get(indice);
				item.selecionar(string);
				indice++;
				label.setText(indice + "/" + getTotal());
			} else {
				limparSelecao();
				label.limpar();
				indice = 0;
			}
		}

		@Override
		public void limparSelecao() {
			for (LabelIcone item : IconeContainer.this.listaLabelIcone) {
				item.setBorder(null);
			}
		}
	}
}