package br.com.persist.painel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JToolBar;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.Button;
import br.com.persist.comp.Panel;
import br.com.persist.comp.TextArea;
import br.com.persist.listener.PainelObjetoListener;
import br.com.persist.util.Action;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class UpdatePainel extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient PainelObjetoListener listener;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;

	public UpdatePainel(PainelObjetoListener listener, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor) {
		textArea.setText(UpdatePainel.subst(instrucao, mapaChaveValor));
		cmbConexao = new JComboBox<>(listener.getConexoes());
		if (padrao != null) {
			cmbConexao.setSelectedItem(padrao);
		}
		this.listener = listener;
		toolbar.add(cmbConexao);
		montarLayout();
	}

	public static String subst(String instrucao, Map<String, String> mapaChaveValor) {
		if (instrucao == null) {
			instrucao = "";
		}

		if (mapaChaveValor == null || mapaChaveValor.isEmpty()) {
			return instrucao;
		}

		Iterator<Map.Entry<String, String>> it = mapaChaveValor.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			instrucao = instrucao.replaceAll("#" + entry.getKey().toUpperCase() + "#", entry.getValue());
			instrucao = instrucao.replaceAll("#" + entry.getKey().toLowerCase() + "#", entry.getValue());
			instrucao = instrucao.replaceAll("#" + entry.getKey() + "#", entry.getValue());
		}

		return instrucao;
	}

	public Frame getFrame() {
		return listener.getFrame();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIcon("label.atualizar", Icones.UPDATE);
		private Action fecharAcao = Action.actionIcon("label.fechar", Icones.SAIR);

		Toolbar() {
			add(new Button(fecharAcao));
			addSeparator();
			add(new Button(atualizarAcao));

			eventos();
		}

		private void eventos() {
			fecharAcao.setActionListener(e -> listener.dispose());
			atualizarAcao.setActionListener(e -> atualizar());
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
			Util.stackTraceAndMessage("PAINEL UPDATE", ex, this);
		}
	}
}