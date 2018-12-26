package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.sql.Connection;

import javax.swing.JComboBox;
import javax.swing.JToolBar;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.Button;
import br.com.persist.comp.Panel;
import br.com.persist.comp.TextArea;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class PainelSelect extends Panel {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;
	private final PainelObjetoListener listener;

	public PainelSelect(PainelObjetoListener listener, Conexao padrao) {
		cmbConexao = new JComboBox<>(listener.getConexoes());
		if (padrao != null) {
			cmbConexao.setSelectedItem(padrao);
		}
		this.listener = listener;
		toolbar.add(cmbConexao);
		montarLayout();
	}

	public Frame getFrame() {
		return listener.getFrame();
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		Toolbar() {
			add(new Button(new FecharAcao()));
			addSeparator();
			add(new Button(new AtualizarRegistrosAcao()));
		}

		class FecharAcao extends Acao {
			private static final long serialVersionUID = 1L;

			FecharAcao() {
				super(false, "label.fechar", Icones.SAIR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				listener.dispose();
			}
		}

		class AtualizarRegistrosAcao extends Acao {
			private static final long serialVersionUID = 1L;

			AtualizarRegistrosAcao() {
				super(false, "label.atualizar", Icones.ATUALIZAR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				atualizar();
			}
		}
	}

	public void atualizar() {
		if (Util.estaVazio(textArea.getText())) {
			return;
		}

		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		try {
			Connection conn = Conexao.getConnection(conexao);
			int atualizados = Persistencia.executar(textArea.getText(), conn);
			listener.setTitle("ATUALIZADOS [" + atualizados + "]");
		} catch (Exception ex) {
			Util.stackTraceAndMessage("PAINEL SELECT", ex, this);
		}
	}
}