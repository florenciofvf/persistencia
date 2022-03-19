package br.com.persist.plugins.requisicao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
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
		muro.camada(panel(0, 0, chkExibirArqMimes, chkAbrirModoTabela));
		add(BorderLayout.CENTER, muro);
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(RequisicaoMensagens.getString(chaveRotulo), false);
	}

	public static Panel panel(int top, int bottom, Component... comps) {
		Panel container = new Panel(new GridLayout(0, 1));
		container.setBorder(BorderFactory.createMatteBorder(top, 0, bottom, 0, Color.GRAY));
		for (Component c : comps) {
			container.add(c);
		}
		return container;
	}

	public static Panel panelN(Component... comps) {
		return panel(1, 0, comps);
	}

	public static Panel panelS(Component... comps) {
		return panel(0, 1, comps);
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