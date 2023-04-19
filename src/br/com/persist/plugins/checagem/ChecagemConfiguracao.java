package br.com.persist.plugins.checagem;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.formulario.Formulario;

public class ChecagemConfiguracao extends AbstratoConfiguracao {
	private final CheckBox chkExibirArqSentencas = criarCheckBox("label.exibir_arq_sentencas");
	private final CheckBox chkExibirArqIgnorados = criarCheckBox("label.exibir_arq_ignorados");
	private static final long serialVersionUID = 1L;

	public ChecagemConfiguracao(Formulario formulario) {
		super(formulario, ChecagemMensagens.getString("label.plugin_checagem"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		chkExibirArqIgnorados.setSelected(ChecagemPreferencia.isExibirArqIgnorados());
		chkExibirArqSentencas.setSelected(ChecagemPreferencia.isExibirArqSentencas());

		Muro muro = new Muro();
		muro.camada(Muro.panelGrid(chkExibirArqSentencas));
		muro.camada(Muro.panelGrid(chkExibirArqIgnorados));
		add(BorderLayout.CENTER, muro);
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(ChecagemMensagens.getString(chaveRotulo), false);
	}

	private void configurar() {
		chkExibirArqSentencas
				.addActionListener(e -> ChecagemPreferencia.setExibirArqSentencas(chkExibirArqSentencas.isSelected()));
		chkExibirArqIgnorados
				.addActionListener(e -> ChecagemPreferencia.setExibirArqIgnorados(chkExibirArqIgnorados.isSelected()));
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(ChecagemMensagens.getString(chaveRotulo), false);
	}
}