package br.com.persist.plugins.atributo;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.formulario.Formulario;

public class AtributoConfiguracao extends AbstratoConfiguracao {
	private final CheckBox chkExibirArqIgnorados = criarCheckBox("label.exibir_arq_ignorados");
	private static final long serialVersionUID = 1L;

	public AtributoConfiguracao(Formulario formulario) {
		super(formulario, AtributoMensagens.getString("label.plugin_atributo"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		chkExibirArqIgnorados.setSelected(AtributoPreferencia.isExibirArqIgnorados());

		Muro muro = new Muro();
		muro.camada(Muro.panelGrid(chkExibirArqIgnorados));
		add(BorderLayout.CENTER, muro);
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(AtributoMensagens.getString(chaveRotulo), false);
	}

	private void configurar() {
		chkExibirArqIgnorados
				.addActionListener(e -> AtributoPreferencia.setExibirArqIgnorados(chkExibirArqIgnorados.isSelected()));
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(AtributoMensagens.getString(chaveRotulo), false);
	}
}