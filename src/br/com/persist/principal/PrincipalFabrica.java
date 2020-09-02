package br.com.persist.principal;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenuItem;

import br.com.persist.fabrica.AbstratoFabricaContainer;
import br.com.persist.servico.AbstratoServico;
import br.com.persist.servico.Servico;
import br.com.persist.util.Mensagens;

public class PrincipalFabrica extends AbstratoFabricaContainer {
	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new FecharServico());
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario) {
		List<JMenuItem> lista = new ArrayList<>();

		JMenuItem item = new JMenuItem(Mensagens.getString("label.fechar"));
		item.addActionListener(e -> fechar(formulario));
		lista.add(item);

		return lista;
	}

	private void fechar(Formulario formulario) {
		WindowEvent event = new WindowEvent(formulario, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
	}
}

class FecharServico extends AbstratoServico {
	@Override
	public void fechandoFormulario(Formulario formulario) {
		formulario.fechar(true);
	}
}