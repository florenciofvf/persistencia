package br.com.persist.container;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.util.List;

import javax.swing.JComboBox;

import br.com.persist.Metadado;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.metadado.Metadados;
import br.com.persist.modelo.MetadadoModelo;
import br.com.persist.util.Action;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class MetadadosContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private Metadados metadados = new Metadados();
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;

	public MetadadosContainer(IJanela janela, ConexaoProvedor provedor, Conexao padrao) {
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(metadados));
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconAtualizar();

		@Override
		public void ini(IJanela janela) {
			super.ini(janela);

			addButton(atualizarAcao);
			add(true, cmbConexao);
			add(true, new ButtonInfo());

			eventos();
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizar());
		}

		class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action queExportamAcao = Action.actionMenu("label.tabelas_que_exportam", null);
			private Action naoExportamAcao = Action.actionMenu("label.tabelas_nao_exportam", null);
			private Action pksMultiplaAcao = Action.actionMenu("label.pks_multiplas", null);
			private Action pksAusentesAcao = Action.actionMenu("label.pks_ausente", null);

			ButtonInfo() {
				super("label.funcoes", Icones.INFO);

				addMenuItem(pksMultiplaAcao);
				addMenuItem(true, naoExportamAcao);
				addMenuItem(true, queExportamAcao);
				addMenuItem(true, pksAusentesAcao);

				queExportamAcao.setActionListener(e -> Util.mensagem(MetadadosContainer.this, metadados.queExportam()));
				naoExportamAcao.setActionListener(e -> Util.mensagem(MetadadosContainer.this, metadados.naoExportam()));
				pksMultiplaAcao.setActionListener(e -> Util.mensagem(MetadadosContainer.this, metadados.pksMultipla()));
				pksAusentesAcao.setActionListener(e -> Util.mensagem(MetadadosContainer.this, metadados.pksAusente()));
			}
		}
	}

	public void atualizar() {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		try {
			Connection conn = Conexao.getConnection(conexao);
			List<Metadado> lista = Persistencia.listarMetadados(conn, conexao);
			Metadado raiz = new Metadado(Mensagens.getString(Constantes.LABEL_METADADOS) + " - " + lista.size());

			for (Metadado metadado : lista) {
				metadado.setTabela(true);

				List<Metadado> fks = Persistencia.listarImportados(conn, conexao, metadado);
				List<Metadado> eks = Persistencia.listarExportados(conn, conexao, metadado);
				List<Metadado> pks = Persistencia.listarPrimarias(conn, conexao, metadado);

				criarAtributoMetadado(metadado, pks, Constantes.PKS, Constantes.PK, 'E');
				criarAtributoMetadado(metadado, fks, Constantes.FKS, Constantes.FK, 'I');
				criarAtributoMetadado(metadado, eks, Constantes.EKS, Constantes.EK, ' ');

				raiz.add(metadado);
			}

			raiz.montarOrdenacoes();
			metadados.setModel(new MetadadoModelo(raiz));
		} catch (Exception ex) {
			Util.stackTraceAndMessage("META-DADOS", ex, this);
		}
	}

	private void criarAtributoMetadado(Metadado metadado, List<Metadado> listaMetadado, String plural, String singular,
			char chave) {
		if (listaMetadado.isEmpty()) {
			return;
		}

		Metadado titulo = new Metadado(listaMetadado.size() > 1 ? plural : singular);
		metadado.add(titulo);

		for (Metadado meta : listaMetadado) {
			titulo.add(meta);

			if (chave == 'E') {
				metadado.setTotalExportados(metadado.getTotalExportados() + 1);
			} else if (chave == 'I') {
				metadado.setTotalImportados(metadado.getTotalImportados() + 1);
			}
		}
	}
}