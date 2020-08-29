package br.com.persist.configuracao;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoFormulario;
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

	@Override
	public void executarAoFecharForm() {
		Preferencias.salvar();
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

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setConfiguracaoFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getConfiguracao().retornoAoFichario(formulario, container);
		dispose();
	}
}