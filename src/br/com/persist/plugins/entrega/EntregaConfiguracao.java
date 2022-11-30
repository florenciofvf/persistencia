package br.com.persist.plugins.entrega;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.formulario.Formulario;

public class EntregaConfiguracao extends AbstratoConfiguracao {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkExibirArqInvisivel = criarCheckBox("label.exibir_arq_invisivel");

	public EntregaConfiguracao(Formulario formulario) {
		super(formulario, EntregaMensagens.getString("label.plugin_entrega"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		chkExibirArqInvisivel.setSelected(EntregaPreferencia.isExibirArqInvisivel());

		Muro muro = new Muro();
		muro.camada(Muro.panelGrid(chkExibirArqInvisivel));
		add(BorderLayout.CENTER, muro);
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(EntregaMensagens.getString(chaveRotulo), false);
	}

	private void configurar() {
		chkExibirArqInvisivel
				.addActionListener(e -> EntregaPreferencia.setExibirArqInvisivel(chkExibirArqInvisivel.isSelected()));
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(EntregaMensagens.getString(chaveRotulo), false);
	}
}