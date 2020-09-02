package br.com.persist.componente;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JToolBar;

import br.com.persist.util.Icones;
import br.com.persist.mensagem.MensagemTemporaria;
import br.com.persist.util.Action;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class BarraButton extends JToolBar {
	private static final long serialVersionUID = 1L;
	private Action fecharAcao = Action.actionIcon(Constantes.LABEL_FECHAR, Icones.SAIR);
	private Action copiar1Acao = Action.actionIcon("label.copiar", Icones.COPIA);
	private Action copiar2Acao = Action.actionIcon("label.copiar", Icones.COPIA);
	private Action colar1Acao = Action.actionIcon("label.colar", Icones.COLAR);
	private Action colar2Acao = Action.actionIcon("label.colar", Icones.COLAR);
	private MensagemTemporaria mensagemTempCopiado = new MensagemTemporaria();
	private Action novoAcao = Action.actionIcon("label.novo", Icones.PANEL4);
	private Action baixarAcao = Action.actionIconBaixar();
	private Action salvarAcao = Action.actionIconSalvar();
	private Action limparAcao = Action.actionIconLimpar();
	protected transient ButtonDestacar buttonDestacar;
	protected transient IJanela janela;

	public void ini(IJanela janela, boolean novo, boolean limpar, boolean salvar) {
		this.janela = janela;

		fecharAcao.setActionListener(e -> fechar());
		addButton(fecharAcao);
		setJanela(janela);
		addSeparator();

		if (novo) {
			novoAcao.setActionListener(e -> novo());
			addButton(novoAcao);
		}

		if (limpar) {
			limparAcao.setActionListener(e -> limpar());
			addButton(limparAcao);
		}

		if (salvar) {
			salvarAcao.setActionListener(e -> salvar());
			addButton(salvarAcao);
		}
	}

	public void ini(IJanela janela, boolean limpar, boolean salvar) {
		this.ini(janela, false, limpar, salvar);
	}

	protected class ButtonDestacar extends ButtonPopup {
		private static final long serialVersionUID = 1L;
		protected Action clonarEmForm = Action.actionMenu("label.clonar_em_formulario", null);
		protected Action abrirEmForm = Action.actionMenu("label.abrir_em_formulario", null);
		protected Action destaEmForm = Action.actionMenu("label.destac_formulario", null);
		protected Action retorAoFich = Action.actionMenu("label.destac_container", null);

		protected ButtonDestacar(ActionListener destacarEmFormulario, ActionListener abrirEmFormulario,
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

	protected void novo() {
		throw new UnsupportedOperationException();
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

	protected void configCopiar1Acao(boolean colar) {
		copiar1Acao.setActionListener(e -> copiar1());
		addButton(copiar1Acao);
		add(mensagemTempCopiado);

		if (colar) {
			colar1Acao.setActionListener(e -> colar1());
			addButton(colar1Acao);
		}
	}

	protected void copiar1Mensagem(String string) {
		if (!Util.estaVazio(string)) {
			mensagemTempCopiado.mensagemChave("msg.copiado");
		}
	}

	protected void copiar1() {
		throw new UnsupportedOperationException();
	}

	protected void colar1() {
		throw new UnsupportedOperationException();
	}

	protected void configCopiar2Acao(boolean colar) {
		copiar2Acao.setActionListener(e -> copiar2());
		addButton(copiar2Acao);

		if (colar) {
			colar2Acao.setActionListener(e -> colar2());
			addButton(colar2Acao);
		}
	}

	protected void copiar2() {
		throw new UnsupportedOperationException();
	}

	protected void colar2() {
		throw new UnsupportedOperationException();
	}

	protected void configButtonDestacar(ActionListener destacarEmFormulario, ActionListener abrirEmFormulario,
			ActionListener retornoAoFichario, ActionListener clonarEmFormulario, boolean separado) {
		buttonDestacar = new ButtonDestacar(destacarEmFormulario, abrirEmFormulario, retornoAoFichario,
				clonarEmFormulario);
		add(separado, buttonDestacar);
	}

	protected void configButtonDestacar(ActionListener destacarEmFormulario, ActionListener abrirEmFormulario,
			ActionListener retornoAoFichario, ActionListener clonarEmFormulario) {
		configButtonDestacar(destacarEmFormulario, abrirEmFormulario, retornoAoFichario, clonarEmFormulario, false);
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