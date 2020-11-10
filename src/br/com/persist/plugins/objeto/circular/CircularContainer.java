package br.com.persist.plugins.objeto.circular;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;

import br.com.persist.assistencia.Util;
import br.com.persist.assistencia.Vetor;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.Relacao;

import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;

public class CircularContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final TextField txtGrauTotal = new TextField("360");
	private final TextField txtGrauOrigem = new TextField("0");
	private final TextField txtRaio = new TextField("300");
	private final ObjetoSuperficie objetoSuperficie;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Objeto> comboObjeto;
	private final JComboBox<Tipo> comboTipo;

	public CircularContainer(Janela janela, ObjetoSuperficie objetoSuperficie, Tipo tipo) {
		comboObjeto = objetoSuperficie.criarComboObjetosSel();
		comboTipo = new JComboBox<>(Tipo.values());
		comboTipo.setSelectedItem(tipo);
		this.objetoSuperficie = objetoSuperficie;
		toolbar.ini(janela);
		montarLayout();
	}

	public enum Tipo {
		EXPORTACAO, IMPORTACAO, NORMAL
	}

	private void montarLayout() {
		Panel panel = new Panel(new GridLayout(5, 2, 10, 10));
		panel.add(new Label("label.pivo"));
		panel.add(comboObjeto);
		panel.add(new Label("label.tipo"));
		panel.add(comboTipo);
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

		public void ini(Janela janela) {
			super.ini(janela, ATUALIZAR);
		}

		private List<Objeto> getSelecionados(Objeto pivo) {
			List<Objeto> resposta = new ArrayList<>(objetoSuperficie.getSelecionados());
			Iterator<Objeto> it = resposta.iterator();
			while (it.hasNext()) {
				if (it.next().equals(pivo)) {
					it.remove();
				}
			}
			return resposta;
		}

		@Override
		protected void atualizar() {
			Objeto pivo = (Objeto) comboObjeto.getSelectedItem();
			Tipo tipo = (Tipo) comboTipo.getSelectedItem();
			if (pivo != null && tipo != null) {
				List<Objeto> selecionados = getSelecionados(pivo);
				if (!selecionados.isEmpty()) {
					atualizar(pivo, tipo, selecionados);
					objetoSuperficie.repaint();
				}
			}
		}

		private void atualizar(Objeto pivo, Tipo tipo, List<Objeto> selecionados) {
			Vetor vetor = new Vetor(Util.getInt(txtRaio.getText(), 300), 0);
			vetor.rotacionar(Util.getInt(txtGrauOrigem.getText(), 0));
			int graus = Util.getInt(txtGrauTotal.getText(), 360) / selecionados.size();
			for (Objeto objeto : selecionados) {
				objeto.setX(pivo.getX() + (int) vetor.getX());
				objeto.setY(pivo.getY() + (int) vetor.getY());
				vetor.rotacionar(graus);
				Relacao relacao = objetoSuperficie.getRelacao(pivo, objeto);
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
		}
	}
}