package br.com.persist.plugins.requisicao;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.formulario.Formulario;

public class RequisicaoConfiguracao extends AbstratoConfiguracao {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkAbrirModoTabela = criarCheckBox("label.padrao_abrir_tabela");
	private final CheckBox chkExibirArqMimes = criarCheckBox("label.exibir_arq_mimes");

	public RequisicaoConfiguracao(Formulario formulario) {
		super(formulario, RequisicaoMensagens.getString("label.plugin_requisicao"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		chkAbrirModoTabela.setSelected(RequisicaoPreferencia.isAbrirModoTabela());
		chkExibirArqMimes.setSelected(RequisicaoPreferencia.isExibirArqMimes());

		Muro muro = new Muro();
		muro.camada(Muro.panelGrid(chkExibirArqMimes, chkAbrirModoTabela));
		add(BorderLayout.CENTER, muro);
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(RequisicaoMensagens.getString(chaveRotulo), false);
	}

	private void configurar() {
		chkAbrirModoTabela
				.addActionListener(e -> RequisicaoPreferencia.setAbrirModoTabela(chkAbrirModoTabela.isSelected()));
		chkExibirArqMimes
				.addActionListener(e -> RequisicaoPreferencia.setExibirArqMimes(chkExibirArqMimes.isSelected()));
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(RequisicaoMensagens.getString(chaveRotulo), false);
	}
}