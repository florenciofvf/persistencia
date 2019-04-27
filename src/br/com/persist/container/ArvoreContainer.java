package br.com.persist.container;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JToolBar;

import br.com.persist.Arquivo;
import br.com.persist.arvore.Arvore;
import br.com.persist.arvore.ArvoreUtil;
import br.com.persist.comp.Button;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.listener.ArvoreListener;
import br.com.persist.modelo.ArvoreModelo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;

public class ArvoreContainer extends Panel implements ArvoreListener {
	private static final long serialVersionUID = 1L;
	private Arvore arvore = new Arvore(new ArvoreModelo());
	private final CheckBox chkSempreTop = new CheckBox();
	private final CheckBox chkLinkAuto = new CheckBox();
	private final CheckBox chkDuplicar = new CheckBox();
	private final Toolbar toolbar = new Toolbar();
	private final Formulario formulario;

	public ArvoreContainer(Formulario formulario) {
		this.formulario = formulario;
		montarLayout();
		baixarArquivo();
	}

	private void montarLayout() {
		chkSempreTop.setToolTipText(Mensagens.getString("msg.arvore.sempreTop"));
		chkLinkAuto.setToolTipText(Mensagens.getString("msg.arvore.link_auto"));
		chkDuplicar.setToolTipText(Mensagens.getString("msg.arvore.duplicar"));
		add(BorderLayout.CENTER, new ScrollPane(arvore));
		add(BorderLayout.NORTH, toolbar);
		arvore.adicionarOuvinte(this);
		chkLinkAuto.setSelected(true);
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action statusAcao = Action.actionIcon("label.status", Icones.HIERARQUIA);
		private Action atualizarAcao = Action.actionIconBaixar();

		Toolbar() {
			add(new Button(atualizarAcao));
			add(new Button(statusAcao));
			add(chkSempreTop);
			add(chkLinkAuto);
			add(chkDuplicar);

			chkSempreTop.addActionListener(e -> formulario.setAlwaysOnTop(chkSempreTop.isSelected()));
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
		}
	}
}