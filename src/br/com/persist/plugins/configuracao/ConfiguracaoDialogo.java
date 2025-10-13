package br.com.persist.plugins.configuracao;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class ConfiguracaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ConfiguracaoContainer container;

	private ConfiguracaoDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONFIGURACOES));
		container = new ConfiguracaoContainer(this, formulario);
		container.setConfiguracaoDialogo(this);
		setModalityType(ModalityType.MODELESS);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
		setTitle(Mensagens.getString(Constantes.LABEL_CONFIGURACOES) + " [" + container.getTotalConfiguracoes() + "]");
	}

	public static void criar(Formulario formulario) {
		ConfiguracaoDialogo form = new ConfiguracaoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setConfiguracaoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(this);
	}

	@Override
	public void dialogClosingHandler(Dialog dialog) {
		Preferencias.salvar();
	}
}