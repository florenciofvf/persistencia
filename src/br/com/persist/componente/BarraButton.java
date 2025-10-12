package br.com.persist.componente;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;

import br.com.persist.abstrato.DialogHandler;
import br.com.persist.abstrato.WindowHandler;
import br.com.persist.abstrato.WindowInternalHandler;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.FicharioHandler;

import static br.com.persist.componente.BarraButtonEnum.*;

public abstract class BarraButton extends JToolBar
		implements WindowHandler, DialogHandler, FicharioHandler, WindowInternalHandler {
	protected Action aplicarAcao = actionIcon(Constantes.LABEL_APLICAR, Icones.SUCESSO);
	private Action salvarComoAcao = actionIcon("label.salvar_como", Icones.SALVARC);
	private Action fecharAcao = actionIcon(Constantes.LABEL_FECHAR, Icones.SAIR);
	private Action limpar2Acao = actionIcon("label.limpar2", Icones.PANEL4);
	private Action copiar2Acao = actionIcon("label.copiar2", Icones.COPIA);
	private Action baixar2Acao = actionIcon("label.baixar2", Icones.COLAR);
	protected Action copiarAcao = actionIcon("label.copiar", Icones.COPIA);
	protected Action colar0Acao = actionIcon("label.colar", Icones.COLAR);
	private Action colar2Acao = actionIcon("label.colar2", Icones.COLAR);
	private Action novoAcao = actionIcon("label.novo", Icones.PANEL4);
	protected LabelTextTemp labelTextTempSalvo = new LabelTextTemp();
	protected Action atualizarAcao = Action.actionIconAtualizar();
	protected LabelTextTemp labelTextTemp = new LabelTextTemp();
	private LabelTextTemp labelTextTemp2 = new LabelTextTemp();
	protected final TextField txtPesquisa = new TextField(25);
	protected final CheckBox chkPorParte = new CheckBox(true);
	protected final CheckBox chkPsqConteudo = new CheckBox();
	protected Action salvarAcao = Action.actionIconSalvar();
	protected ButtonColar buttonColar = new ButtonColar();
	private Action baixarAcao = Action.actionIconBaixar();
	private Action limparAcao = Action.actionIconLimpar();
	protected transient ButtonDestacar buttonDestacar;
	private Action excluirAcao = actionIconExcluir();
	private static final long serialVersionUID = 1L;
	protected transient ButtonAplicar buttonAplicar;
	private Label labelNomeBackup = new Label();
	protected Label label = new Label();
	protected transient Janela janela;

	public void ini(Janela janela, BarraButtonEnum... enuns) {
		this.janela = janela;
		fecharAcao.setActionListener(e -> fechar());
		if (!(janela instanceof Nil)) {
			Button button = new Button(fecharAcao);
			button.setFocusable(false);
			add(button);
			setJanela(janela);
			addSeparator();
		}
		configButtonDestacar(enuns);
		configNovo(enuns);
		configLimpar(enuns);
		configLimpar2(enuns);
		configBaixar(enuns);
		configBaixar2(enuns);
		configSalvar(enuns);
		configSalvarComo(enuns);
		configAtualizar(enuns);
		configExcluir(enuns);
		configCopiar(enuns);
		configColar0(enuns);
		configColar(enuns);
		configCopiar2(enuns);
		configColar2(enuns);
		configAplicar(enuns);
		configButtonAplicar(enuns);
		configButtonAplicar3(enuns);
		configBackup(enuns);
	}

	public void focusInputPesquisar() {
		txtPesquisa.requestFocus();
	}

	protected Action actionIcon(String chaveRotulo, Icon icone) {
		return Action.actionIcon(chaveRotulo, icone);
	}

	protected Action actionIconExcluir() {
		return Action.actionIconExcluir();
	}

	private void configColar2(BarraButtonEnum... enuns) {
		if (contem(COLAR2, enuns)) {
			addButton(colar2Acao);
			colar2Acao.setActionListener(e -> colar2());
		}
	}

	private void configCopiar2(BarraButtonEnum... enuns) {
		if (contem(COPIAR2, enuns)) {
			addCopiar2();
			copiar2Acao.setActionListener(e -> copiar2());
		}
	}

	public Action addCopiar2() {
		addButton(copiar2Acao);
		add(labelTextTemp2);
		return copiar2Acao;
	}

	private void configColar0(BarraButtonEnum... enuns) {
		if (contem(COLAR0, enuns)) {
			addButton(colar0Acao);
			colar0Acao.setActionListener(e -> colar0());
		}
	}

	private void configColar(BarraButtonEnum... enuns) {
		if (contem(COLAR, enuns)) {
			add(buttonColar);
		}
	}

	protected class ButtonColar extends ButtonPopup {
		private Action numeroAcao = actionMenu("label.numeros");
		private Action letraAcao = actionMenu("label.letras");
		private Action todosAcao = actionMenu("label.todos");
		private static final long serialVersionUID = 1L;

		protected ButtonColar() {
			super("label.colar", Icones.COLAR);
			addMenuItem(numeroAcao);
			addMenuItem(letraAcao);
			addMenuItem(todosAcao);
			numeroAcao.setActionListener(e -> colar(true, false));
			letraAcao.setActionListener(e -> colar(false, true));
			todosAcao.setActionListener(e -> colar(false, false));
		}

		public void addItem(Action action) {
			addMenuItem(action);
		}
	}

	private void configBackup(BarraButtonEnum... enuns) {
		if (contem(BACKUP, enuns)) {
			add(new ButtonBackup());
			add(labelNomeBackup);
		}
	}

	public void setNomeBackup(String string) {
		labelNomeBackup.setText(" Backup: " + string);
	}

	public void limparNomeBackup() {
		labelNomeBackup.limpar();
	}

	protected class ButtonBackup extends ButtonPopup {
		private Action criarAcao = actionMenu("label.criar");
		private Action abrirAcao = actionMenu("label.abrir");
		private static final long serialVersionUID = 1L;

		protected ButtonBackup() {
			super("label.backup", Icones.BACKUP);
			addMenuItem(criarAcao);
			addMenuItem(true, abrirAcao);
			criarAcao.setActionListener(e -> criarBackup());
			abrirAcao.setActionListener(e -> abrirBackup());
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

	private void configAplicar(BarraButtonEnum... enuns) {
		if (contem(APLICAR, enuns)) {
			addButton(aplicarAcao);
			aplicarAcao.setActionListener(e -> aplicar());
		}
	}

	private void configButtonAplicar(BarraButtonEnum... enuns) {
		if (contem(APLICAR_BOTAO, enuns)) {
			buttonAplicar = new ButtonAplicar(false);
			add(buttonAplicar);
		}
	}

	private void configButtonAplicar3(BarraButtonEnum... enuns) {
		if (contem(APLICAR_BOTAO3, enuns)) {
			buttonAplicar = new ButtonAplicar(true);
			add(buttonAplicar);
		}
	}

	protected class ButtonAplicar extends ButtonPopup {
		protected Action aplicar3Acao = actionMenu(Constantes.LABEL_APLICAR);
		protected Action aplicar2Acao = actionMenu(Constantes.LABEL_APLICAR);
		protected Action aplicarAcao = actionMenu(Constantes.LABEL_APLICAR);
		private static final long serialVersionUID = 1L;

		protected ButtonAplicar(boolean tres) {
			super(Constantes.LABEL_APLICAR, Icones.SUCESSO);
			addMenuItem(aplicarAcao);
			aplicarAcao.setActionListener(e -> aplicar());
			addMenuItem(true, aplicar2Acao);
			aplicar2Acao.setActionListener(e -> aplicar2());
			if (tres) {
				addMenuItem(true, aplicar3Acao);
				aplicar3Acao.setActionListener(e -> aplicar3());
			}
		}

		public void setTextAplicar3(String text) {
			aplicar3Acao.text(text);
		}

		public void setTextAplicar2(String text) {
			aplicar2Acao.text(text);
		}

		public void setEnableAplicar2Acao(boolean b) {
			aplicar2Acao.setEnabled(b);
		}
	}

	protected class ButtonDestacar extends ButtonPopup {
		protected Action destacarEmFormulario = actionMenu("label.destacar_formulario");
		protected Action retornarAoFichario = actionMenu("label.retornar_ao_fichario");
		protected Action clonarEmFormulario = actionMenu("label.clonar_em_formulario");
		protected Action abrirEmFormulario = actionMenu("label.abrir_em_formulario");
		private static final long serialVersionUID = 1L;

		protected ButtonDestacar() {
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
			focusInputPesquisar();
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
		if (!Util.isEmpty(string)) {
			labelTextTemp.mensagemChave("msg.copiado");
		}
	}

	protected void salvoMensagem() {
		labelTextTempSalvo.mensagemChave("msg.salvo");
	}

	public void copiar2Mensagem(String string) {
		if (!Util.isEmpty(string)) {
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
		//
	}

	protected void clonarEmFormulario() {
		//
	}

	protected void retornarAoFichario() {
		//
	}

	protected void abrirEmFormulario() {
		//
	}

	protected void baixar2() {
		//
	}

	protected void limpar2() {
		//
	}

	protected void copiar2() {
		//
	}

	protected void copiar() {
		//
	}

	protected void colar2() {
		//
	}

	protected void aplicar() {
		//
	}

	protected void aplicar2() {
		//
	}

	protected void aplicar3() {
		//
	}

	protected void salvar() {
		//
	}

	protected void salvarComo() {
		//
	}

	protected void atualizar() {
		//
	}

	public Action getAtualizarAcao() {
		return atualizarAcao;
	}

	protected void excluir() {
		//
	}

	protected void baixar() {
		//
	}

	protected void limpar() {
		//
	}

	protected void colar(boolean numeros, boolean letras) {
		//
	}

	protected void colar0() {
		//
	}

	protected void novo() {
		//
	}

	protected void criarBackup() {
		//
	}

	protected void abrirBackup() {
		//
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

	public void addButton(Action action) {
		addButton(false, action);
	}

	@Override
	public Component add(Component comp) {
		if (comp == txtPesquisa) {
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
		} else if (comp == chkPorParte) {
			chkPorParte.setToolTipText(Mensagens.getString("label.por_parte"));
		} else if (comp == chkPsqConteudo) {
			if ("TABELA".equals(chkPsqConteudo.getTag())) {
				chkPsqConteudo.setToolTipText(Mensagens.getString("msg.pesq_no_conteudo_tabela"));
			} else if ("FICHARIO".equals(chkPsqConteudo.getTag())) {
				chkPsqConteudo.setToolTipText(Mensagens.getString("msg.pesq_no_conteudo_fichario"));
			} else {
				chkPsqConteudo.setToolTipText(Mensagens.getString("msg.pesq_no_conteudo"));
			}
		}
		return super.add(comp);
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

	public void removeAction(Action action) {
		Button button = null;
		int total = getComponentCount();
		Component[] components = getComponents();
		for (int i = 0; i < total; i++) {
			Component comp = components[i];
			if (comp instanceof Button) {
				Button b = (Button) comp;
				if (b.getAction() != null && b.getAction() == action) {
					button = b;
					break;
				}
			}
		}
		if (button != null) {
			button.removeActionListener(action.getActionListener());
			remove(button);
		}
	}

	@Override
	public void windowInternalActivatedHandler(JInternalFrame internal) {
	}

	@Override
	public void windowInternalClosingHandler(JInternalFrame internal) {
	}

	@Override
	public void windowInternalOpenedHandler(JInternalFrame internal) {
	}

	@Override
	public void tabActivatedHandler(Fichario fichario) {
	}

	@Override
	public void dialogActivatedHandler(Dialog dialog) {
	}

	@Override
	public void dialogClosingHandler(Dialog dialog) {
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
	}

	@Override
	public void windowActivatedHandler(Window window) {
	}

	@Override
	public void windowClosingHandler(Window window) {
	}

	@Override
	public void windowOpenedHandler(Window window) {
	}
}