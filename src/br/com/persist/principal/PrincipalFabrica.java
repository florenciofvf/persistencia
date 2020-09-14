package br.com.persist.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class PrincipalFabrica extends AbstratoFabricaContainer {
	private JMenuItem itemFechar = new JMenuItem(Mensagens.getString("label.fechar"), Icones.SAIR);

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new FecharServico());
	}

	private class FecharServico extends AbstratoServico {
		@Override
		public void fechandoFormulario(Formulario formulario) {
			itemFechar.doClick();
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();

		if (menu.getItemCount() > 0) {
			menu.addSeparator();
		}

		JMenuItem itemFecharEConexao = new JMenuItem(Mensagens.getString("label.fechar_com_conexao"), Icones.SAIR);
		itemFecharEConexao.addActionListener(e -> fechar(formulario, true));
		lista.add(itemFecharEConexao);

		itemFechar.addActionListener(e -> fechar(formulario, false));
		lista.add(itemFechar);

		return lista;
	}

	private void fechar(Formulario formulario, boolean fecharConexao) {
		if (Util.confirmar(formulario, "label.confirma_fechar")) {
			Map<String, Object> args = new HashMap<>();
			args.put("fechar_conexoes", fecharConexao);
			args.put("fechar_formulario", true);
			formulario.processar(args);
			System.exit(0);
		}
	}
}