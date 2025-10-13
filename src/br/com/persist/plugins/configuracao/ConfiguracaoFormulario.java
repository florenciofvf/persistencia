package br.com.persist.plugins.configuracao;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.formulario.Formulario;

public class ConfiguracaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ConfiguracaoContainer container;

	private ConfiguracaoFormulario(Formulario formulario) {
		super(formulario, Mensagens.getString(Constantes.LABEL_CONFIGURACOES));
		container = new ConfiguracaoContainer(this, formulario);
		container.setConfiguracaoFormulario(this);
		montarLayout();
	}

	private ConfiguracaoFormulario(ConfiguracaoContainer container) {
		super(container.getFormulario(), Mensagens.getString(Constantes.LABEL_CONFIGURACOES));
		container.setConfiguracaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
		setTitle(Mensagens.getString(Constantes.LABEL_CONFIGURACOES) + " [" + container.getTotalConfiguracoes() + "]");
	}

	public static void criar(Formulario formulario, ConfiguracaoContainer container) {
		ConfiguracaoFormulario form = new ConfiguracaoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		ConfiguracaoFormulario form = new ConfiguracaoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setConfiguracaoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(this);
	}

	@Override
	public void windowClosingHandler(Window window) {
		Preferencias.salvar();
	}
}