package br.com.persist.plugins.objeto.circular;

import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;

import br.com.persist.abstrato.PluginBasico;
import br.com.persist.assistencia.Util;
import br.com.persist.assistencia.Vetor;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoException;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.ObjetoSuperficieUtil;
import br.com.persist.plugins.objeto.Relacao;

public class CircularContainer extends Panel implements PluginBasico {
	private final TextField txtGrauTotal = new TextField("360");
	private final TextField txtGrauOrigem = new TextField("0");
	private final TextField txtRaio = new TextField("300");
	private static final long serialVersionUID = 1L;
	private final ObjetoSuperficie objetoSuperficie;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Objeto> comboObjeto;
	private final JComboBox<Tipo> comboTipo;

	public CircularContainer(Janela janela, ObjetoSuperficie objetoSuperficie, Tipo tipo) {
		comboObjeto = ObjetoSuperficieUtil.criarComboObjetosSel(objetoSuperficie);
		comboTipo = new JComboBox<>(Tipo.values());
		this.objetoSuperficie = objetoSuperficie;
		comboTipo.setSelectedItem(tipo);
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
			List<Objeto> resposta = new ArrayList<>(ObjetoSuperficieUtil.getSelecionados(objetoSuperficie));
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
				try {
					atualizar(pivo, tipo);
				} catch (ObjetoException ex) {
					Util.mensagem(CircularContainer.this, ex.getMessage());
				}
			}
		}

		private void atualizar(Objeto pivo, Tipo tipo) throws ObjetoException {
			List<Objeto> selecionados = getSelecionados(pivo);
			if (!selecionados.isEmpty()) {
				atualizar(pivo, tipo, selecionados);
				objetoSuperficie.repaint();
			}
		}

		private void atualizar(Objeto pivo, Tipo tipo, List<Objeto> selecionados) throws ObjetoException {
			int graus = definirGrauUnidade(selecionados);
			Vetor vetor = criarVetor();
			for (Objeto objeto : selecionados) {
				localizar(objeto, pivo, vetor);
				vetor.rotacionar(graus);
				processarRelacao(objeto, pivo, tipo);
			}
		}

		private int definirGrauUnidade(List<Objeto> selecionados) {
			return Util.getInt(txtGrauTotal.getText(), 360) / selecionados.size();
		}

		private Vetor criarVetor() {
			Vetor vetor = new Vetor(Util.getInt(txtRaio.getText(), 300), 0);
			vetor.rotacionar(Util.getInt(txtGrauOrigem.getText(), 0));
			return vetor;
		}

		private void localizar(Objeto objeto, Objeto pivo, Vetor vetor) {
			objeto.setX(pivo.getX() + (int) vetor.getX());
			objeto.setY(pivo.getY() + (int) vetor.getY());
		}

		private void processarRelacao(Objeto objeto, Objeto pivo, Tipo tipo) throws ObjetoException {
			Relacao relacao = ObjetoSuperficieUtil.getRelacao(objetoSuperficie, pivo, objeto);
			if (relacao != null && tipo == Tipo.NORMAL) {
				pontoNormal(relacao);
			} else if (relacao != null && tipo == Tipo.EXPORTACAO) {
				pontoExportacao(pivo, relacao);
			} else if (relacao != null && tipo == Tipo.IMPORTACAO) {
				pontoImportacao(pivo, relacao);
			}
		}

		private void pontoNormal(Relacao relacao) {
			relacao.setPontoOrigem(false);
			relacao.setPontoDestino(false);
		}

		private void pontoExportacao(Objeto pivo, Relacao relacao) {
			relacao.setPontoOrigem(pivo != relacao.getOrigem());
			relacao.setPontoDestino(pivo != relacao.getDestino());
		}

		private void pontoImportacao(Objeto pivo, Relacao relacao) {
			relacao.setPontoOrigem(pivo == relacao.getOrigem());
			relacao.setPontoDestino(pivo == relacao.getDestino());
		}
	}
}