package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;
import java.io.IOException;

import br.com.persist.Arquivo;
import br.com.persist.anexo.Anexo;
import br.com.persist.anexo.AnexoUtil;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Button;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.dialogo.ArquivoIconeDialogo;
import br.com.persist.formulario.AnexoFormulario;
import br.com.persist.listener.AnexoListener;
import br.com.persist.modelo.AnexoModelo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class AnexoContainer extends Panel implements AnexoListener {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkSempreTopForm = new CheckBox();
	private final CheckBox chkSempreTopAnex = new CheckBox();
	private Anexo anexo = new Anexo(new AnexoModelo(true));
	private final AnexoFormulario anexoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private final transient Desktop desktop;
	private final Formulario formulario;

	public AnexoContainer(IJanela janela, Formulario formulario, AnexoFormulario anexoFormulario) {
		this.anexoFormulario = anexoFormulario;
		this.desktop = Desktop.getDesktop();
		this.formulario = formulario;
		toolbar.ini(janela);
		montarLayout();
		baixarArquivo();
	}

	private void montarLayout() {
		chkSempreTopForm.setToolTipText(Mensagens.getString("msg.arvore.sempreTopForm"));
		chkSempreTopAnex.setToolTipText(Mensagens.getString("msg.anexo.sempreTopAnex"));
		add(BorderLayout.CENTER, new ScrollPane(anexo));
		add(BorderLayout.NORTH, toolbar);
		anexo.adicionarOuvinte(this);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconBaixar();
		private Action salvarAcao = Action.actionIconSalvar();

		@Override
		public void ini(IJanela janela) {
			super.ini(janela);

			add(new Button(atualizarAcao));
			addButton(true, salvarAcao);
			if (anexoFormulario != null) {
				add(chkSempreTopAnex);
			}
			add(chkSempreTopForm);

			chkSempreTopAnex.addActionListener(e -> anexoFormulario.setAlwaysOnTop(chkSempreTopAnex.isSelected()));
			chkSempreTopForm.addActionListener(e -> {
				formulario.setAlwaysOnTop(chkSempreTopForm.isSelected());
				if (chkSempreTopForm.isSelected()) {
					formulario.setExtendedState(Formulario.MAXIMIZED_BOTH);
				}
			});
			atualizarAcao.setActionListener(e -> baixarArquivo());
		}
	}

	@Override
	public void editarArquivo(Anexo anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		try {
			desktop.edit(arquivo.getFile());
		} catch (IOException e) {
			Util.mensagem(AnexoContainer.this, e.getMessage());
		}
	}

	@Override
	public void abrirArquivo(Anexo anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		try {
			desktop.open(arquivo.getFile());
		} catch (IOException e) {
			Util.mensagem(AnexoContainer.this, e.getMessage());
		}
	}

	@Override
	public void excluirArquivo(Anexo anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo != null && arquivo.getPai() != null && Util.confirmaExclusao(AnexoContainer.this)) {
			arquivo.excluir();
			AnexoUtil.excluirEstrutura(anexo, arquivo);
		}
	}

	@Override
	public void renomearArquivo(Anexo anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null || arquivo.getPai() == null) {
			return;
		}

		Object resp = Util.getValorInputDialog(AnexoContainer.this, "label.renomear", arquivo.toString(),
				arquivo.toString());

		if (resp == null || Util.estaVazio(resp.toString())) {
			return;
		}

		if (arquivo.renomear(resp.toString())) {
			AnexoUtil.refreshEstrutura(anexo, arquivo);
		}
	}

	@Override
	public void iconeArquivo(Anexo anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		ArquivoIconeDialogo form = new ArquivoIconeDialogo((Frame) null, arquivo);
		form.setLocationRelativeTo(AnexoContainer.this);
		form.setVisible(true);
		AnexoUtil.refreshEstrutura(anexo, arquivo);
	}

	private void baixarArquivo() {
		AnexoModelo modelo = new AnexoModelo(true);
		anexo.setModel(modelo);
	}
}