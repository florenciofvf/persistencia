package br.com.persist.formulario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;

public class FormularioFabrica extends AbstratoFabricaContainer {
	private JMenuItem itemFechar = new JMenuItem(Mensagens.getString("label.fechar"), Icones.SAIR);

	@Override
	public void inicializar() {
		Util.criarDiretorio("imagens");
		Util.criarDiretorio("libs");
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new FormularioConfiguracao(formulario);
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new FecharServico(), new FormularioServico());
	}

	private class FecharServico extends AbstratoServico {
		@Override
		public void fechandoFormulario(Formulario formulario) {
			itemFechar.doClick();
		}
	}

	private class FormularioServico extends AbstratoServico {
		@Override
		public void visivelFormulario(Formulario formulario) {
			formulario.definirLarguraEmPorcentagem(Preferencias.getPorcHorizontalLocalForm());
			formulario.definirAlturaEmPorcentagem(Preferencias.getPorcVerticalLocalForm());
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
			args.put(FormularioEvento.FECHAR_CONEXOES, fecharConexao);
			args.put(FormularioEvento.FECHAR_FORMULARIO, true);
			formulario.salvarGC();
			formulario.processar(args);
			System.exit(0);
		}
	}
}