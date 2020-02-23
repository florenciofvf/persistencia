package br.com.persist.comp;

import java.awt.Component;

import javax.swing.JToolBar;

import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;

public class BarraButton extends JToolBar {
	private static final long serialVersionUID = 1L;
	protected transient IJanela janela;

	public void ini(IJanela janela) {
		this.janela = janela;

		if (janela != null) {
			Action fecharAcao = Action.actionIcon(Constantes.LABEL_FECHAR, Icones.SAIR);
			fecharAcao.setActionListener(e -> fechar());
			addButton(fecharAcao);
			addSeparator();
		}
	}

	public void configAbrirAutoFichario(String chave) {
		CheckBox chkAbrirAutoFichario = new CheckBox();
		chkAbrirAutoFichario.setSelected(Preferencias.getBoolean(chave));
		chkAbrirAutoFichario.setToolTipText(Mensagens.getString("label.abrir_auto_ficha"));
		chkAbrirAutoFichario.addActionListener(e -> Preferencias.setBoolean(chave, chkAbrirAutoFichario.isSelected()));
		add(chkAbrirAutoFichario);
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
		this.janela = janela;
	}
}