package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.componente.Button;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.TextField;

public class FormularioConfiguracao extends AbstratoConfiguracao {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkFecharComESCFormulario = new CheckBox("label.fechar_com_esc_formulario");
	private final CheckBox chkFecharComESCInternal = new CheckBox("label.fechar_com_esc_internal");
	private final CheckBox chkFecharComESCDialogo = new CheckBox("label.fechar_com_esc_dialogo");
	private final CheckBox chkMonitorPreferencial = new CheckBox("label.monitor_preferencial");
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");
	private final CheckBox chkTituloAbaMin = new CheckBox("label.titulo_aba_min");
	private final TextField txtFormFichaDialogo = new TextField();
	private final TextField txtFormDialogo = new TextField();
	private final TextField txtFormFicha = new TextField();

	public FormularioConfiguracao(Formulario formulario) {
		super(formulario, Mensagens.getTituloAplicacao());
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		chkFecharComESCFormulario.setSelected(Preferencias.isFecharComESCFormulario());
		chkFecharComESCInternal.setSelected(Preferencias.isFecharComESCInternal());
		chkFecharComESCDialogo.setSelected(Preferencias.isFecharComESCDialogo());
		chkMonitorPreferencial.setSelected(Preferencias.isMonitorPreferencial());
		chkFicharioScroll.setSelected(Preferencias.isFicharioComRolagem());
		txtFormFichaDialogo.setText(Preferencias.getFormFichaDialogo());
		chkTituloAbaMin.setSelected(Preferencias.isTituloAbaMin());
		txtFormDialogo.setText(Preferencias.getFormDialogo());
		txtFormFicha.setText(Preferencias.getFormFicha());

		Panel container = new Panel(new GridLayout(0, 1));
		if (Preferencias.isMonitorPreferencial()) {
			container.add(criarLabelTitulo("label.monitor_preferencial"));
			container.add(new PainelMonitorPreferencial());
			container.add(new JSeparator());
		}
		container.add(new PanelCenter(new Label("label.form_ficha_dialogo"), txtFormFichaDialogo));
		container.add(new PanelCenter(new Label("label.form_dialogo"), txtFormDialogo));
		container.add(new PanelCenter(new Label("label.form_ficha"), txtFormFicha));
		container.add(new JSeparator());
		container.add(chkFecharComESCFormulario);
		container.add(chkFecharComESCInternal);
		container.add(chkFecharComESCDialogo);
		container.add(chkTituloAbaMin);
		container.add(chkFicharioScroll);
		container.add(chkMonitorPreferencial);

		Insets insets = new Insets(5, 10, 5, 5);
		chkFecharComESCFormulario.setMargin(insets);
		chkFecharComESCInternal.setMargin(insets);
		chkFecharComESCDialogo.setMargin(insets);
		chkMonitorPreferencial.setMargin(insets);
		chkFicharioScroll.setMargin(insets);
		chkTituloAbaMin.setMargin(insets);

		add(BorderLayout.CENTER, container);
	}

	private void configurar() {
		chkFicharioScroll.addActionListener(e -> {
			Preferencias.setFicharioComRolagem(chkFicharioScroll.isSelected());
			formulario.setTabLayoutPolicy(
					Preferencias.isFicharioComRolagem() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		});
		chkFecharComESCFormulario
				.addActionListener(e -> Preferencias.setFecharComESCFormulario(chkFecharComESCFormulario.isSelected()));
		chkFecharComESCInternal
				.addActionListener(e -> Preferencias.setFecharComESCInternal(chkFecharComESCInternal.isSelected()));
		txtFormFichaDialogo.addActionListener(e -> Preferencias.setFormFichaDialogo(txtFormFichaDialogo.getText()));
		chkFecharComESCDialogo
				.addActionListener(e -> Preferencias.setFecharComESCDialogo(chkFecharComESCDialogo.isSelected()));
		chkMonitorPreferencial
				.addActionListener(e -> Preferencias.setMonitorPreferencial(chkMonitorPreferencial.isSelected()));
		chkTituloAbaMin.addActionListener(e -> Preferencias.setTituloAbaMin(chkTituloAbaMin.isSelected()));
		txtFormDialogo.addActionListener(e -> Preferencias.setFormDialogo(txtFormDialogo.getText()));
		txtFormFicha.addActionListener(e -> Preferencias.setFormFicha(txtFormFicha.getText()));
		txtFormFichaDialogo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setFormFichaDialogo(txtFormFichaDialogo.getText());
			}
		});
		txtFormDialogo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setFormDialogo(txtFormDialogo.getText());
			}
		});
		txtFormFicha.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setFormFicha(txtFormFicha.getText());
			}
		});
	}

	private Label criarLabelTitulo(String chaveRotulo) {
		Label label = new Label(chaveRotulo);
		label.setHorizontalAlignment(Label.CENTER);
		return label;
	}

	private class PainelMonitorPreferencial extends Panel {
		private static final long serialVersionUID = 1L;
		private Button buttonNaoPreferencial = new Button("label.nao_preferencial");
		private Button buttonPreferencial = new Button("label.preferencial");

		private PainelMonitorPreferencial() {
			super(new FlowLayout());
			add(buttonPreferencial);
			add(buttonNaoPreferencial);
			buttonPreferencial.addActionListener(e -> formulario.salvarMonitorComoPreferencial());
			buttonNaoPreferencial.addActionListener(e -> formulario.excluirMonitorComoPreferencial());
		}
	}
}