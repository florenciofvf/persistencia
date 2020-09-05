package br.com.persist.plugins.configuracao;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;

public class ConfiguracaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ConfiguracaoContainer container;

	private ConfiguracaoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_CONFIGURACOES));
		container = new ConfiguracaoContainer(this, formulario);
		container.setConfiguracaoFormulario(this);
		montarLayout();
	}

	private ConfiguracaoFormulario(ConfiguracaoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_CONFIGURACOES));
		container.setConfiguracaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ConfiguracaoContainer container) {
		ConfiguracaoFormulario form = new ConfiguracaoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		ConfiguracaoFormulario form = new ConfiguracaoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setConfiguracaoFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}

	@Override
	public void executarAoFecharFormulario() {
		Preferencias.salvar();
	}
}