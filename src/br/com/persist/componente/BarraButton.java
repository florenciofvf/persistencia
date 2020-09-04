package br.com.persist.componente;

import java.awt.Component;

import javax.swing.JToolBar;

import br.com.persist.util.Icones;
import br.com.persist.util.Constantes;
import br.com.persist.util.Util;
import static br.com.persist.componente.BarraButtonEnum.*;

public class BarraButton extends JToolBar {
	private static final long serialVersionUID = 1L;
	private Action fecharAcao = Action.actionIcon(Constantes.LABEL_FECHAR, Icones.SAIR);
	private Action limpar2Acao = Action.actionIcon("label.limpar2", Icones.PANEL4);
	private Action copiar2Acao = Action.actionIcon("label.copiar2", Icones.COPIA);
	private Action baixar2Acao = Action.actionIcon("label.baixar2", Icones.COLAR);
	private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);
	private Action colar2Acao = Action.actionIcon("label.colar2", Icones.COLAR);
	private Action colarAcao = Action.actionIcon("label.colar", Icones.COLAR);
	private Action novoAcao = Action.actionIcon("label.novo", Icones.PANEL4);
	private LabelTextTemp labelTextTemp2 = new LabelTextTemp();
	private LabelTextTemp labelTextTemp = new LabelTextTemp();
	private Action baixarAcao = Action.actionIconBaixar();
	private Action salvarAcao = Action.actionIconSalvar();
	private Action limparAcao = Action.actionIconLimpar();
	protected transient ButtonDestacar buttonDestacar;
	protected transient Janela janela;

	public void ini(Janela janela, BarraButtonEnum... enuns) {
		this.janela = janela;

		fecharAcao.setActionListener(e -> fechar());
		addButton(fecharAcao);
		setJanela(janela);
		addSeparator();

		if (comButtonDestacar(enuns)) {
			buttonDestacar = new ButtonDestacar();
			buttonDestacar.ini(enuns);
			add(buttonDestacar);
		}

		if (contem(NOVO, enuns)) {
			addButton(novoAcao);
			novoAcao.setActionListener(e -> novo());
		}

		if (contem(LIMPAR, enuns)) {
			addButton(limparAcao);
			limparAcao.setActionListener(e -> limpar());
		}

		if (contem(LIMPAR2, enuns)) {
			addButton(limpar2Acao);
			limpar2Acao.setActionListener(e -> limpar2());
		}

		if (contem(SALVAR, enuns)) {
			addButton(salvarAcao);
			salvarAcao.setActionListener(e -> salvar());
		}

		if (contem(BAIXAR, enuns)) {
			addButton(baixarAcao);
			baixarAcao.setActionListener(e -> baixar());
		}

		if (contem(BAIXAR2, enuns)) {
			addButton(baixar2Acao);
			baixar2Acao.setActionListener(e -> baixar2());
		}

		if (contem(COPIAR, enuns)) {
			addButton(copiarAcao);
			add(labelTextTemp);
			copiarAcao.setActionListener(e -> copiar());
		}

		if (contem(COLAR, enuns)) {
			addButton(colarAcao);
			colarAcao.setActionListener(e -> colar());
		}

		if (contem(COPIAR2, enuns)) {
			addButton(copiar2Acao);
			add(labelTextTemp2);
			copiar2Acao.setActionListener(e -> copiar2());
		}

		if (contem(COLAR2, enuns)) {
			addButton(colar2Acao);
			colar2Acao.setActionListener(e -> colar2());
		}
	}

	private class ButtonDestacar extends ButtonPopup {
		private static final long serialVersionUID = 1L;
		protected Action clonarEmFormulario = Action.actionMenu("label.clonar_em_formulario", null);
		protected Action destacarEmFormulario = Action.actionMenu("label.destac_formulario", null);
		protected Action abrirEmFormulario = Action.actionMenu("label.abrir_em_formulario", null);
		protected Action retornarAoFichario = Action.actionMenu("label.retornar_ao_fich", null);

		private ButtonDestacar() {
			super(Constantes.LABEL_DESTACAR, Icones.ARRASTAR);
		}

		private void ini(BarraButtonEnum... enuns) {
			if (contem(DESTAC_EM_FORM, enuns)) {
				addMenuItem(destacarEmFormulario);
				destacarEmFormulario.setActionListener(e -> destacarEmFormulario());
			}

			if (contem(RETORNAR_AO_FICHARIO, enuns)) {
				addMenuItem(retornarAoFichario);
				retornarAoFichario.setActionListener(e -> retornarAoFichario());
			}

			if (contem(ABRIR_EM_F0RM, enuns)) {
				addMenuItem(abrirEmFormulario);
				abrirEmFormulario.setActionListener(e -> abrirEmFormulario());
			}

			if (contem(CLONAR_EM_FORM, enuns)) {
				addMenuItem(clonarEmFormulario);
				clonarEmFormulario.setActionListener(e -> clonarEmFormulario());
			}
		}
	}

	private boolean comButtonDestacar(BarraButtonEnum... enuns) {
		return contem(DESTAC_EM_FORM, enuns) || contem(RETORNAR_AO_FICHARIO, enuns) || contem(ABRIR_EM_F0RM, enuns)
				|| contem(CLONAR_EM_FORM, enuns);
	}

	protected void copiarMensagem(String string) {
		if (!Util.estaVazio(string)) {
			labelTextTemp.mensagemChave("msg.copiado");
		}
	}

	protected void copiar2Mensagem(String string) {
		if (!Util.estaVazio(string)) {
			labelTextTemp2.mensagemChave("msg.copiado");
		}
	}

	private boolean contem(BarraButtonEnum obj, BarraButtonEnum... enuns) {
		for (BarraButtonEnum e : enuns) {
			if (e == obj) {
				return true;
			}
		}

		return false;
	}

	protected void destacarEmFormulario() {
		throw new UnsupportedOperationException();
	}

	protected void clonarEmFormulario() {
		throw new UnsupportedOperationException();
	}

	protected void retornarAoFichario() {
		throw new UnsupportedOperationException();
	}

	protected void abrirEmFormulario() {
		throw new UnsupportedOperationException();
	}

	protected void baixar2() {
		throw new UnsupportedOperationException();
	}

	protected void limpar2() {
		throw new UnsupportedOperationException();
	}

	protected void copiar2() {
		throw new UnsupportedOperationException();
	}

	protected void copiar() {
		throw new UnsupportedOperationException();
	}

	protected void colar2() {
		throw new UnsupportedOperationException();
	}

	protected void salvar() {
		throw new UnsupportedOperationException();
	}

	protected void baixar() {
		throw new UnsupportedOperationException();
	}

	protected void limpar() {
		throw new UnsupportedOperationException();
	}

	protected void colar() {
		throw new UnsupportedOperationException();
	}

	protected void novo() {
		throw new UnsupportedOperationException();
	}

	protected Component add(boolean separador, Component comp) {
		if (separador) {
			addSeparator();
		}

		return add(comp);
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

	public void setJanela(Janela janela) {
		fecharAcao.setEnabled(janela != null);
		this.janela = janela;
	}

	public void fechar() {
		if (janela != null) {
			janela.fechar();
		}
	}
}