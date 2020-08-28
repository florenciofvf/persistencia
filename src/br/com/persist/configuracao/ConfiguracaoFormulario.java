package br.com.persist.configuracao;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;

public class ConfiguracaoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ConfiguracaoContainer container;

	private ConfiguracaoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_CONFIGURACOES));
		container = new ConfiguracaoContainer(this, formulario);
		container.setConfiguracaoFormulario(this);
		montarLayout();
		configurar();
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

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Preferencias.salvar();
			}
		});
	}

	@Override
	public void fechar() {
		dispose();
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