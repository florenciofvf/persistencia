package br.com.persist.plugins.requisicao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.TextField;
import br.com.persist.formulario.Formulario;

public class RequisicaoConfiguracao extends AbstratoConfiguracao {
	private static final long serialVersionUID = 1L;
	private final TextField txtBinarios = new TextField();

	public RequisicaoConfiguracao(Formulario formulario) {
		super(formulario, RequisicaoMensagens.getString("label.plugin_requisicao"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		txtBinarios.setText(RequisicaoPreferencia.getBinarios());
		Muro muro = new Muro();
		muro.camada(panelS(new PanelCenter(criarLabel("label.tipos_binarios"), txtBinarios)));
		add(BorderLayout.CENTER, muro);
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
		txtBinarios.addActionListener(e -> RequisicaoPreferencia.setBinarios(txtBinarios.getText()));
		txtBinarios.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				RequisicaoPreferencia.setBinarios(txtBinarios.getText());
			}
		});
	}

	private Label criarLabel(String chaveRotulo) {
		return new Label(RequisicaoMensagens.getString(chaveRotulo), false);
	}
}