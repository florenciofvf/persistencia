package br.com.persist.container;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.Arquivo;
import br.com.persist.arvore.Arvore;
import br.com.persist.arvore.ArvoreUtil;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Button;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.formulario.ArvoreFormulario;
import br.com.persist.listener.ArvoreListener;
import br.com.persist.modelo.ArvoreModelo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;

public class ArvoreContainer extends Panel implements ArvoreListener {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkSempreTopForm = new CheckBox();
	private final CheckBox chkSempreTopArvo = new CheckBox();
	private Arvore arvore = new Arvore(new ArvoreModelo());
	private final CheckBox chkLinkAuto = new CheckBox();
	private final CheckBox chkDuplicar = new CheckBox();
	private final ArvoreFormulario arvoreFormulario;
	private final Toolbar toolbar = new Toolbar();
	private final Formulario formulario;

	public ArvoreContainer(IJanela janela, Formulario formulario, ArvoreFormulario arvoreFormulario) {
		this.arvoreFormulario = arvoreFormulario;
		this.formulario = formulario;
		toolbar.ini(janela);
		montarLayout();
		baixarArquivo();
	}

	private void montarLayout() {
		chkSempreTopForm.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopForm"));
		chkSempreTopArvo.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopArqu"));
		chkLinkAuto.setToolTipText(Mensagens.getString("msg.arquivo.link_auto"));
		chkDuplicar.setToolTipText(Mensagens.getString("msg.arquivo.duplicar"));
		add(BorderLayout.CENTER, new ScrollPane(arvore));
		add(BorderLayout.NORTH, toolbar);
		arvore.adicionarOuvinte(this);
		chkLinkAuto.setSelected(true);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action fecharAcao = Action.actionIcon("label.fechar_todos", Icones.FECHAR);
		private Action statusAcao = Action.actionIcon("label.status", Icones.HIERARQUIA);
		private Action atualizarAcao = Action.actionIconBaixar();

		@Override
		public void ini(IJanela janela) {
			super.ini(janela);
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_ARQUIVO);

			add(new Button(atualizarAcao));
			add(new Button(statusAcao));
			if (arvoreFormulario != null) {
				add(chkSempreTopArvo);
			}
			add(chkSempreTopForm);
			add(chkLinkAuto);
			add(chkDuplicar);
			add(fecharAcao);

			chkSempreTopArvo.addActionListener(e -> arvoreFormulario.setAlwaysOnTop(chkSempreTopArvo.isSelected()));
			chkSempreTopForm.addActionListener(e -> {
				formulario.setAlwaysOnTop(chkSempreTopForm.isSelected());
				if (chkSempreTopForm.isSelected()) {
					formulario.setExtendedState(Formulario.MAXIMIZED_BOTH);
				}
			});
			fecharAcao.setActionListener(e -> formulario.getFichario().fecharTodos());
			atualizarAcao.setActionListener(e -> baixarArquivo());
			statusAcao.setActionListener(e -> statusArquivo());
		}

		private void statusArquivo() {
			ArvoreModelo modelo = (ArvoreModelo) arvore.getModel();

			List<Arquivo> lista = new ArrayList<>();
			modelo.listar(lista);

			for (Arquivo arquivo : lista) {
				arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));

				if (arquivo.isArquivoAberto()) {
					ArvoreUtil.selecionarObjeto(arvore, arquivo);
				} else {
					ArvoreUtil.refreshEstrutura(arvore, arquivo);
				}
			}

			arvore.clearSelection();

			for (Arquivo arquivo : lista) {
				if (formulario.getFichario().isAtivo(arquivo.getFile())) {
					ArvoreUtil.selecionarObjeto(arvore, arquivo);
				}
			}
		}
	}

	@Override
	public void abrirFormArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.abrirArquivo(arquivo.getFile(), false);
		}
	}

	@Override
	public void abrirFichArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			if (chkDuplicar.isSelected()) {
				formulario.abrirArquivo(arquivo.getFile(), true);
			} else {
				if (!formulario.getFichario().isAberto(arquivo.getFile())) {
					formulario.abrirArquivo(arquivo.getFile(), true);
				}
			}

			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
			formulario.getFichario().selecionarAba(arquivo.getFile());
			ArvoreUtil.refreshEstrutura(arvore, arquivo);
		}
	}

	@Override
	public void selecionarArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.getFichario().selecionarAba(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
			ArvoreUtil.refreshEstrutura(arvore, arquivo);

			if (arquivo.isFile()) {
				formulario.toFront();
			}
		}
	}

	@Override
	public void clickArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null && chkLinkAuto.isSelected()) {
			formulario.getFichario().selecionarAba(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
			ArvoreUtil.refreshEstrutura(arvore, arquivo);

			if (arquivo.isFile()) {
				formulario.toFront();
			}
		}
	}

	@Override
	public void atualizarArvore(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
			ArvoreUtil.refreshEstrutura(arvore, arquivo);
		}
	}

	private void baixarArquivo() {
		ArvoreModelo modelo = new ArvoreModelo();
		arvore.setModel(modelo);

		List<Arquivo> lista = new ArrayList<>();
		modelo.listar(lista);

		for (Arquivo arquivo : lista) {
			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
		}
	}

	@Override
	public void fecharArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.getFichario().fecharArquivo(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().isAberto(arquivo.getFile()));
			ArvoreUtil.refreshEstrutura(arvore, arquivo);
		}
	}
}