package br.com.persist.plugins.metadado;

import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Muro;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.componente.Label;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.TextField;
import br.com.persist.formulario.Formulario;

public class MetadadoConfiguracao extends AbstratoConfiguracao {
	private final TextField txtGetObjetos = new TextField();
	private static final long serialVersionUID = 1L;

	public MetadadoConfiguracao(Formulario formulario) {
		super(formulario, MetadadoMensagens.getString("label.plugin_metadado"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		txtGetObjetos.setText(Preferencias.getGetObjetosBanco());

		Muro muro = new Muro();
		muro.camada(Muro.panelGridBorderBottom(new PanelCenter(criarLabel("label.get_objetos_banco"), txtGetObjetos)));
		add(BorderLayout.CENTER, muro);
	}

	private void configurar() {
		txtGetObjetos.addActionListener(e -> Preferencias.setGetObjetosBanco(txtGetObjetos.getText()));
		txtGetObjetos.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setGetObjetosBanco(txtGetObjetos.getText());
			}
		});
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(MetadadoMensagens.getString(chaveRotulo), false);
	}
}