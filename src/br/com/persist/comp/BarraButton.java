package br.com.persist.comp;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JToolBar;

import br.com.persist.util.Action;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;

public class BarraButton extends JToolBar {
	private static final long serialVersionUID = 1L;
	private Action fecharAcao = Action.actionIcon(Constantes.LABEL_FECHAR, Icones.SAIR);
	protected Action baixarAcao = Action.actionIconBaixar();
	private Action salvarAcao = Action.actionIconSalvar();
	private Action limparAcao = Action.actionIconLimpar();
	protected transient ButtonDestacar buttonDestacar;
	protected transient IJanela janela;

	public void ini(IJanela janela, boolean limpar, boolean salvar) {
		this.janela = janela;

		fecharAcao.setActionListener(e -> fechar());
		addButton(fecharAcao);
		setJanela(janela);
		addSeparator();

		if (limpar) {
			limparAcao.setActionListener(e -> limpar());
			addButton(limparAcao);
		}

		if (salvar) {
			salvarAcao.setActionListener(e -> salvar());
			addButton(salvarAcao);
		}
	}

	protected class ButtonDestacar extends ButtonPopup {
		private static final long serialVersionUID = 1L;
		protected Action clonarEmForm = Action.actionMenu("label.clonar_em_formulario", null);
		protected Action abrirEmForm = Action.actionMenu("label.abrir_em_formulario", null);
		protected Action destaEmForm = Action.actionMenu("label.destac_formulario", null);
		protected Action retorAoFich = Action.actionMenu("label.destac_container", null);

		ButtonDestacar(ActionListener destacarEmFormulario, ActionListener abrirEmFormulario,
				ActionListener retornoAoFichario, ActionListener clonarEmFormulario) {
			super(Constantes.LABEL_DESTACAR, Icones.ARRASTAR);
			addMenuItem(destaEmForm);
			addMenuItem(retorAoFich);
			addMenuItem(abrirEmForm);
			addMenuItem(clonarEmForm);

			destaEmForm.setActionListener(destacarEmFormulario);
			clonarEmForm.setActionListener(clonarEmFormulario);
			abrirEmForm.setActionListener(abrirEmFormulario);
			retorAoFich.setActionListener(retornoAoFichario);

			destaEmForm.setEnabled(destacarEmFormulario != null);
			clonarEmForm.setEnabled(clonarEmFormulario != null);
			abrirEmForm.setEnabled(abrirEmFormulario != null);
			retorAoFich.setEnabled(retornoAoFichario != null);
		}
	}

	protected void limpar() {
		throw new UnsupportedOperationException();
	}

	protected void salvar() {
		throw new UnsupportedOperationException();
	}

	protected void configBaixarAcao(ActionListener listener) {
		baixarAcao.setActionListener(listener);
		addButton(baixarAcao);
	}

	protected void configButtonDestacar(ActionListener destacarEmFormulario, ActionListener abrirEmFormulario,
			ActionListener retornoAoFichario, ActionListener clonarEmFormulario) {
		buttonDestacar = new ButtonDestacar(destacarEmFormulario, abrirEmFormulario, retornoAoFichario,
				clonarEmFormulario);
		add(true, buttonDestacar);
	}

	public Action getBaixarAcao() {
		return baixarAcao;
	}

	public Action getSalvarAcao() {
		return salvarAcao;
	}

	public Action getLimparAcao() {
		return limparAcao;
	}

	public void configAbrirAutoFichario(String chave) {
		CheckBox chkAbrirAutoFichario = new CheckBox();
		chkAbrirAutoFichario.setSelected(Preferencias.getBoolean(chave));
		chkAbrirAutoFichario.setToolTipText(Mensagens.getString("label.abrir_auto_ficha"));
		chkAbrirAutoFichario.addActionListener(e -> Preferencias.setBoolean(chave, chkAbrirAutoFichario.isSelected()));

		if (Constantes.ABRIR_AUTO_FICHARIO_SET) {
			add(chkAbrirAutoFichario);
		}
	}

	protected void addButton(boolean separador, Action action) {
		if (separador) {
			addSeparator();
		}

		add(new Button(action));
	}

	protected void addButton(Action action) {
		addButton(false, action);
	}

	protected Component add(boolean separador, Component comp) {
		if (separador) {
			addSeparator();
		}

		return add(comp);
	}

	public void fechar() {
		if (janela != null) {
			janela.fechar();
		}
	}

	public IJanela getJanela() {
		return janela;
	}

	public void setJanela(IJanela janela) {
		fecharAcao.setEnabled(janela != null);
		this.janela = janela;
	}
}