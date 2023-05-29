package br.com.persist.plugins.instrucao;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.formulario.Formulario;

public class InstrucaoConfiguracao extends AbstratoConfiguracao {
	private final CheckBox chkExibirArqIgnorados = criarCheckBox("label.exibir_arq_ignorados");
	private static final long serialVersionUID = 1L;

	public InstrucaoConfiguracao(Formulario formulario) {
		super(formulario, InstrucaoMensagens.getString("label.plugin_instrucao"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		chkExibirArqIgnorados.setSelected(InstrucaoPreferencia.isExibirArqIgnorados());

		Muro muro = new Muro();
		muro.camada(Muro.panelGrid(chkExibirArqIgnorados));
		add(BorderLayout.CENTER, muro);
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(InstrucaoMensagens.getString(chaveRotulo), false);
	}

	private void configurar() {
		chkExibirArqIgnorados
				.addActionListener(e -> InstrucaoPreferencia.setExibirArqIgnorados(chkExibirArqIgnorados.isSelected()));
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(InstrucaoMensagens.getString(chaveRotulo), false);
	}
}