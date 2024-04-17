package br.com.persist.formulario;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
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
import br.com.persist.assistencia.Constantes;
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
		public void windowClosingHandler(Window window) {
			itemFechar.doClick();
		}
	}

	private class FormularioServico extends AbstratoServico {
		@Override
		public void windowOpenedHandler(Window window) {
			((Formulario) window).definirLarguraEmPorcentagem(Preferencias.getPorcHorizontalLocalForm());
			((Formulario) window).definirAlturaEmPorcentagem(Preferencias.getPorcVerticalLocalForm());
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		JMenuItem itemOutraInstancia = new JMenuItem(FormularioMensagens.getString("label.abrir_outra_instancia"),
				Icones.CRIAR2);
		JMenuItem itemFecharEConexao = new JMenuItem(FormularioMensagens.getString("label.fechar_com_conexao"),
				Icones.SAIR);
		itemOutraInstancia.addActionListener(e -> abrirOutraInstancia(formulario));
		itemFecharEConexao.addActionListener(e -> fechar(formulario, true));
		itemFechar.addActionListener(e -> fechar(formulario, false));
		menu.add(itemOutraInstancia);
		menu.addSeparator();
		menu.add(itemFecharEConexao);
		menu.add(itemFechar);
		return new ArrayList<>();
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

	private void abrirOutraInstancia(Formulario formulario) {
		String string = getPath();
		if (string == null) {
			String msg = Mensagens.getString("msg.arquivo_inexistente") + Constantes.QL2 + string;
			Util.mensagem(formulario, msg);
		} else {
			try {
				Runtime.getRuntime().exec("java -jar " + string);
			} catch (IOException e) {
				Util.mensagem(formulario, e.getMessage());
			}
		}
	}

	private static String getPath() {
		String string = new File("persistencia_launcher.jar").getAbsolutePath();
		if (new File(string).exists()) {
			return string;
		}
		string = new File("persistencia.jar").getAbsolutePath();
		if (new File(string).exists()) {
			return string;
		}
		string = new File("launcher.jar").getAbsolutePath();
		if (new File(string).exists()) {
			return string;
		}
		return null;
	}
}