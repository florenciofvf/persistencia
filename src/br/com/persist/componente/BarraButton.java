package br.com.persist.componente;

import java.awt.Component;

import javax.swing.JToolBar;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;

import static br.com.persist.componente.BarraButtonEnum.*;

public class BarraButton extends JToolBar {
	private static final long serialVersionUID = 1L;
	private Action salvarComoAcao = Action.actionIcon("label.salvar_como", Icones.SALVARC);
	private Action fecharAcao = Action.actionIcon(Constantes.LABEL_FECHAR, Icones.SAIR);
	protected Action aplicarAcao = Action.actionIcon("label.aplicar", Icones.SUCESSO);
	private Action excluirAcao = Action.actionMenu("label.excluir2", Icones.EXCLUIR);
	private Action limpar2Acao = Action.actionIcon("label.limpar2", Icones.PANEL4);
	private Action copiar2Acao = Action.actionIcon("label.copiar2", Icones.COPIA);
	private Action baixar2Acao = Action.actionIcon("label.baixar2", Icones.COLAR);
	private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);
	private Action colar2Acao = Action.actionIcon("label.colar2", Icones.COLAR);
	protected Action colarAcao = Action.actionIcon("label.colar", Icones.COLAR);
	private Action novoAcao = Action.actionIcon("label.novo", Icones.PANEL4);
	protected LabelTextTemp labelTextTempSalvo = new LabelTextTemp();
	protected LabelTextTemp labelTextTemp = new LabelTextTemp();
	private Action atualizarAcao = Action.actionIconAtualizar();
	private LabelTextTemp labelTextTemp2 = new LabelTextTemp();
	private Action baixarAcao = Action.actionIconBaixar();
	private Action salvarAcao = Action.actionIconSalvar();
	private Action limparAcao = Action.actionIconLimpar();
	protected transient ButtonDestacar buttonDestacar;
	protected transient Janela janela;

	public void ini(Janela janela, BarraButtonEnum... enuns) {
		this.janela = janela;
		fecharAcao.setActionListener(e -> fechar());
		Button button = new Button(fecharAcao);
		button.setFocusable(false);
		add(button);
		setJanela(janela);
		addSeparator();
		configButtonDestacar(enuns);
		configNovo(enuns);
		configLimpar(enuns);
		configLimpar2(enuns);
		configSalvar(enuns);
		configSalvarComo(enuns);
		configAtualizar(enuns);
		configExcluir(enuns);
		configBaixar(enuns);
		configBaixar2(enuns);
		configCopiar(enuns);
		configColar(enuns);
		configCopiar2(enuns);
		configColar2(enuns);
		configAplicar(enuns);
	}

	private void configAplicar(BarraButtonEnum... enuns) {
		if (contem(APLICAR, enuns)) {
			addButton(aplicarAcao);
			aplicarAcao.setActionListener(e -> aplicar());
		}
	}

	private void configColar2(BarraButtonEnum... enuns) {
		if (contem(COLAR2, enuns)) {
			addButton(colar2Acao);
			colar2Acao.setActionListener(e -> colar2());
		}
	}

	private void configCopiar2(BarraButtonEnum... enuns) {
		if (contem(COPIAR2, enuns)) {
			addButton(copiar2Acao);
			add(labelTextTemp2);
			copiar2Acao.setActionListener(e -> copiar2());
		}
	}

	private void configColar(BarraButtonEnum... enuns) {
		if (contem(COLAR, enuns)) {
			addButton(colarAcao);
			colarAcao.setActionListener(e -> colar());
		}
	}

	private void configCopiar(BarraButtonEnum... enuns) {
		if (contem(COPIAR, enuns)) {
			addButton(copiarAcao);
			add(labelTextTemp);
			copiarAcao.setActionListener(e -> copiar());
		}
	}

	private void configBaixar2(BarraButtonEnum... enuns) {
		if (contem(BAIXAR2, enuns)) {
			addButton(baixar2Acao);
			baixar2Acao.setActionListener(e -> baixar2());
		}
	}

	private void configBaixar(BarraButtonEnum... enuns) {
		if (contem(BAIXAR, enuns)) {
			addButton(baixarAcao);
			baixarAcao.setActionListener(e -> baixar());
		}
	}

	private void configExcluir(BarraButtonEnum... enuns) {
		if (contem(EXCLUIR, enuns)) {
			addButton(excluirAcao);
			excluirAcao.setActionListener(e -> excluir());
		}
	}

	private void configAtualizar(BarraButtonEnum... enuns) {
		if (contem(ATUALIZAR, enuns)) {
			addButton(atualizarAcao);
			atualizarAcao.setActionListener(e -> atualizar());
		}
	}

	private void configSalvarComo(BarraButtonEnum... enuns) {
		if (contem(SALVAR_COMO, enuns)) {
			addButton(salvarComoAcao);
			salvarComoAcao.setActionListener(e -> salvarComo());
		}
	}

	private void configSalvar(BarraButtonEnum... enuns) {
		if (contem(SALVAR, enuns)) {
			addButton(salvarAcao);
			add(labelTextTempSalvo);
			salvarAcao.setActionListener(e -> salvar());
		}
	}

	private void configLimpar2(BarraButtonEnum... enuns) {
		if (contem(LIMPAR2, enuns)) {
			addButton(limpar2Acao);
			limpar2Acao.setActionListener(e -> limpar2());
		}
	}

	private void configLimpar(BarraButtonEnum... enuns) {
		if (contem(LIMPAR, enuns)) {
			addButton(limparAcao);
			limparAcao.setActionListener(e -> limpar());
		}
	}

	private void configNovo(BarraButtonEnum... enuns) {
		if (contem(NOVO, enuns)) {
			addButton(novoAcao);
			novoAcao.setActionListener(e -> novo());
		}
	}

	private void configButtonDestacar(BarraButtonEnum... enuns) {
		if (comButtonDestacar(enuns)) {
			buttonDestacar = new ButtonDestacar();
			buttonDestacar.ini(enuns);
			add(buttonDestacar);
		}
	}

	protected class ButtonDestacar extends ButtonPopup {
		private static final long serialVersionUID = 1L;
		protected Action destacarEmFormulario = Action.actionMenu("label.destacar_formulario", null);
		protected Action retornarAoFichario = Action.actionMenu("label.retornar_ao_fichario", null);
		protected Action clonarEmFormulario = Action.actionMenu("label.clonar_em_formulario", null);
		protected Action abrirEmFormulario = Action.actionMenu("label.abrir_em_formulario", null);

		private ButtonDestacar() {
			super(Constantes.LABEL_DESTACAR, Icones.ARRASTAR);
		}

		private void ini(BarraButtonEnum... enuns) {
			byte adicionados = 0;

			if (contem(DESTACAR_EM_FORMULARIO, enuns)) {
				adicionados++;
				addMenuItem(destacarEmFormulario);
				destacarEmFormulario.setActionListener(e -> destacarEmFormulario());
			}

			if (contem(RETORNAR_AO_FICHARIO, enuns)) {
				adicionados++;
				addMenuItem(retornarAoFichario);
				retornarAoFichario.setActionListener(e -> retornarAoFichario());
			}

			if (contem(CLONAR_EM_FORMULARIO, enuns)) {
				addMenuItem(adicionados > 0, clonarEmFormulario);
				clonarEmFormulario.setActionListener(e -> clonarEmFormulario());
			}

			if (contem(ABRIR_EM_FORMULARO, enuns)) {
				addMenuItem(adicionados > 0 && !contem(CLONAR_EM_FORMULARIO, enuns), abrirEmFormulario);
				abrirEmFormulario.setActionListener(e -> abrirEmFormulario());
			}
		}

		public void habilitar(boolean b) {
			destacarEmFormulario.setEnabled(b);
			retornarAoFichario.setEnabled(b);
			clonarEmFormulario.setEnabled(b);
			abrirEmFormulario.setEnabled(b);
		}

		public void habilitar(boolean destacar, boolean retornar, boolean clonar, boolean abrir) {
			destacarEmFormulario.setEnabled(destacar);
			retornarAoFichario.setEnabled(retornar);
			clonarEmFormulario.setEnabled(clonar);
			abrirEmFormulario.setEnabled(abrir);
		}

		public void estadoFormulario() {
			habilitar(false, true, true, true);
		}

		public void estadoFichario() {
			habilitar(true, false, true, true);
		}

		public void estadoDialogo() {
			habilitar(true, true, false, true);
		}
	}

	private boolean comButtonDestacar(BarraButtonEnum... enuns) {
		return contem(DESTACAR_EM_FORMULARIO, enuns) || contem(RETORNAR_AO_FICHARIO, enuns)
				|| contem(CLONAR_EM_FORMULARIO, enuns) || contem(ABRIR_EM_FORMULARO, enuns);
	}

	protected void copiarMensagem(String string) {
		if (!Util.estaVazio(string)) {
			labelTextTemp.mensagemChave("msg.copiado");
		}
	}

	protected void salvoMensagem() {
		labelTextTempSalvo.mensagemChave("msg.salvo");
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

	protected void aplicar() {
		throw new UnsupportedOperationException();
	}

	protected void salvar() {
		throw new UnsupportedOperationException();
	}

	protected void salvarComo() {
		throw new UnsupportedOperationException();
	}

	protected void atualizar() {
		throw new UnsupportedOperationException();
	}

	public Action getAtualizarAcao() {
		return atualizarAcao;
	}

	protected void excluir() {
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