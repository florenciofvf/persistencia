package br.com.persist.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class PrincipalFabrica extends AbstratoFabricaContainer {
	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new FecharServico());
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();

		if (menu.getItemCount() > 0) {
			menu.addSeparator();
		}

		JMenuItem item = new JMenuItem(Mensagens.getString("label.fechar"));
		item.addActionListener(e -> fechar(formulario));
		lista.add(item);

		return lista;
	}

	private void fechar(Formulario formulario) {
		formulario.eventoFechar();
	}
}

class FecharServico extends AbstratoServico {
	@Override
	public void fechandoFormulario(Formulario formulario) {
		if (Util.confirmar(formulario, "label.confirma_fechar")) {
			// Preferencias.setFecharConexao(fecharConexao);
			// FormularioUtil.fechar(Formulario.this);
			System.exit(0);
		}
	}
}