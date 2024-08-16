package br.com.persist.plugins.objeto.config;

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

import javax.swing.BorderFactory;
import javax.swing.Icon;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.macro.MacroProvedor;

public class IconeContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final List<LabelIcone> listaLabelIcone;
	private final Toolbar toolbar = new Toolbar();
	private final transient Objeto objeto;
	private final Label labelIcone;
	private int totalIcones;

	public IconeContainer(Janela janela, Objeto objeto, Label label) {
		listaLabelIcone = new ArrayList<>();
		this.labelIcone = label;
		this.objeto = objeto;
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
			selecionar(objeto.getIcone());
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
					labelIcone.setToolTipText(nome);
					labelIcone.setIcon(getIcon());
					objeto.setIcone(nome);
					MacroProvedor.imagem(objeto.getIcone());
					toolbar.fechar();
				} catch (AssistenciaException ex) {
					Util.mensagem(IconeContainer.this, ex.getMessage());
				}
			}
		};
	}

	private class Toolbar extends BarraButton {
		private TextField txtPesquisa = new TextField(10);
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR, COPIAR);
			add(true, txtPesquisa);
			txtPesquisa.addActionListener(e -> selecionar());
		}

		private void selecionar() {
			String string = txtPesquisa.getText();
			if (Util.isEmpty(string)) {
				return;
			}
			for (LabelIcone icone : listaLabelIcone) {
				icone.selecionar(string);
			}
		}

		@Override
		protected void copiar() {
			br.com.persist.icone.IconeContainer.setNomeIconeCopiado(objeto.getIcone());
			fechar();
		}

		@Override
		protected void limpar() {
			MacroProvedor.imagem(null);
			labelIcone.setIcon(null);
			objeto.limparIcone();
			fechar();
		}
	}
}