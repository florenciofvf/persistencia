package br.com.persist.plugins.anexo;

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

import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;

public class AnexoIconeContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final List<LabelIcone> listaLabelIcone;
	private final Toolbar toolbar = new Toolbar();
	private final transient Anexo anexo;
	private int totalIcones;

	public AnexoIconeContainer(Janela janela, Anexo arquivo) {
		listaLabelIcone = new ArrayList<>();
		this.anexo = arquivo;
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
			selecionar(anexo.getNomeIcone());
		}

		private void selecionar(String nomeIcone) {
			if (nome.equals(nomeIcone)) {
				setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
			}
		}

		private transient MouseListener mouseListenerInner = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				anexo.setIcone(getIcon(), nome);
				AnexoModelo.putAnexo(anexo);
				toolbar.fechar();
			}
		};
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR);
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
		protected void limpar() {
			AnexoModelo.putAnexo(anexo);
			anexo.limparIcone();
			fechar();
		}
	}
}