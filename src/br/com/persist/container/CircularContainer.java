package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.TextField;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Relacao;
import br.com.persist.desktop.Superficie;
import br.com.persist.util.Action;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;
import br.com.persist.util.Vetor;

public class CircularContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private TextField txtGrauTotal = new TextField("360");
	private TextField txtGrauOrigem = new TextField("0");
	private TextField txtRaio = new TextField("300");
	private final JComboBox<Objeto> cmbObjeto;
	private final JComboBox<Tipo> cmbTipo;
	private final Superficie superficie;

	public CircularContainer(IJanela janela, Superficie superficie, Tipo tipo) {
		cmbObjeto = Util.criarComboObjetosSel(superficie);
		cmbTipo = new JComboBox<>(Tipo.values());
		cmbTipo.setSelectedItem(tipo);
		this.superficie = superficie;
		toolbar.ini(janela);
		montarLayout();
	}

	public enum Tipo {
		EXPORTACAO, IMPORTACAO, NORMAL
	}

	private void montarLayout() {
		Panel panel = new Panel(new GridLayout(5, 2, 10, 10));
		panel.add(new Label("label.pivo"));
		panel.add(cmbObjeto);
		panel.add(new Label("label.tipo"));
		panel.add(cmbTipo);
		panel.add(new Label("label.raio"));
		panel.add(txtRaio);
		panel.add(new Label("label.grau_origem"));
		panel.add(txtGrauOrigem);
		panel.add(new Label("label.grau_total"));
		panel.add(txtGrauTotal);

		add(BorderLayout.CENTER, panel);
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconAtualizar();

		public void ini(IJanela janela) {
			super.ini(janela, false, false);

			addButton(atualizarAcao);

			atualizarAcao.setActionListener(e -> atualizar());
		}

		private List<Objeto> getSelecionados(Objeto pivo) {
			List<Objeto> resposta = new ArrayList<>(superficie.getSelecionados());

			Iterator<Objeto> it = resposta.iterator();

			while (it.hasNext()) {
				if (it.next().equals(pivo)) {
					it.remove();
				}
			}

			return resposta;
		}

		private void atualizar() {
			Objeto pivo = (Objeto) cmbObjeto.getSelectedItem();
			Tipo tipo = (Tipo) cmbTipo.getSelectedItem();

			if (pivo == null || tipo == null) {
				return;
			}

			List<Objeto> selecionados = getSelecionados(pivo);

			if (selecionados.isEmpty()) {
				return;
			}

			Vetor vetor = new Vetor(Util.getInt(txtRaio.getText(), 300), 0);
			vetor.rotacionar(Util.getInt(txtGrauOrigem.getText(), 0));

			int graus = Util.getInt(txtGrauTotal.getText(), 360) / selecionados.size();

			for (Objeto objeto : selecionados) {
				objeto.setX(pivo.getX() + (int) vetor.getX());
				objeto.setY(pivo.getY() + (int) vetor.getY());

				vetor.rotacionar(graus);

				Relacao relacao = superficie.getRelacao(pivo, objeto);

				if (relacao != null && tipo == Tipo.NORMAL) {
					relacao.setPontoOrigem(false);
					relacao.setPontoDestino(false);

				} else if (relacao != null && tipo == Tipo.EXPORTACAO) {
					relacao.setPontoOrigem(pivo != relacao.getOrigem());
					relacao.setPontoDestino(pivo != relacao.getDestino());

				} else if (relacao != null && tipo == Tipo.IMPORTACAO) {
					relacao.setPontoOrigem(pivo == relacao.getOrigem());
					relacao.setPontoDestino(pivo == relacao.getDestino());
				}
			}

			superficie.repaint();
		}
	}
}