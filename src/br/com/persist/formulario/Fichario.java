package br.com.persist.formulario;

import java.io.File;
import java.util.List;

import javax.swing.JTabbedPane;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.util.Mensagens;

public class Fichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public void novo(Formulario formulario) {
		Container container = new Container(formulario);
		addTab(Mensagens.getString("label.novo"), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	public void abrir(Formulario formulario, File file, List<Objeto> objetos, List<Relacao> relacoes) {
		Container container = new Container(formulario);
		container.abrir(file, objetos, relacoes);
		addTab(Mensagens.getString("label.novo"), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this);
		setTabComponentAt(ultimoIndice, tituloAba);
		setTitleAt(ultimoIndice, file.getName());
		setSelectedIndex(ultimoIndice);
	}
}