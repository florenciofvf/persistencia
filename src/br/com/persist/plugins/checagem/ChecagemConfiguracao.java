package br.com.persist.plugins.checagem;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.formulario.Formulario;

public class ChecagemConfiguracao extends AbstratoConfiguracao {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkExibirArqSentencas = criarCheckBox("label.exibir_arq_sentencas");

	public ChecagemConfiguracao(Formulario formulario) {
		super(formulario, ChecagemMensagens.getString("label.plugin_checagem"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		chkExibirArqSentencas.setSelected(ChecagemPreferencia.isExibirArqSentencas());

		Muro muro = new Muro();
		muro.camada(Muro.panelGrid(chkExibirArqSentencas));
		add(BorderLayout.CENTER, muro);
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(ChecagemMensagens.getString(chaveRotulo), false);
	}

	private void configurar() {
		chkExibirArqSentencas
				.addActionListener(e -> ChecagemPreferencia.setExibirArqSentencas(chkExibirArqSentencas.isSelected()));
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(ChecagemMensagens.getString(chaveRotulo), false);
	}
}