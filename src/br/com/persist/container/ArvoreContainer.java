package br.com.persist.container;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.Arquivo;
import br.com.persist.arvore.Arvore;
import br.com.persist.arvore.ArvoreUtil;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Button;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.ArvoreFormulario;
import br.com.persist.listener.ArvoreListener;
import br.com.persist.modelo.ArvoreModelo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;

public class ArvoreContainer extends AbstratoContainer implements ArvoreListener, Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkSempreTopForm = new CheckBox();
	private final CheckBox chkSempreTopArvo = new CheckBox();
	private Arvore arvore = new Arvore(new ArvoreModelo());
	private final CheckBox chkLinkAuto = new CheckBox();
	private final CheckBox chkDuplicar = new CheckBox();
	private final Toolbar toolbar = new Toolbar();
	private ArvoreFormulario arvoreFormulario;

	public ArvoreContainer(IJanela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		baixarArquivo();
	}

	public ArvoreFormulario getArvoreFormulario() {
		return arvoreFormulario;
	}

	public void setArvoreFormulario(ArvoreFormulario arvoreFormulario) {
		this.arvoreFormulario = arvoreFormulario;
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
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

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().getArvore().destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().getArvore().clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		ArvoreFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (arvoreFormulario != null) {
			arvoreFormulario.retornoAoFichario();
		}
	}

	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action fecharAcao = Action.actionIcon("label.fechar_todos", Icones.FECHAR);
		private Action statusAcao = Action.actionIcon("label.abertos", Icones.HIERARQUIA);

		public void ini(IJanela janela) {
			super.ini(janela, false, false);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario(), false);
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_ARQUIVO);
			configBaixarAcao(e -> baixarArquivo());

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
			fecharAcao.setActionListener(e -> formulario.getFichario().getArquivos().fecharTodos());
			statusAcao.setActionListener(e -> statusArquivo());
		}

		private void statusArquivo() {
			ArvoreModelo modelo = (ArvoreModelo) arvore.getModel();

			List<Arquivo> lista = new ArrayList<>();
			modelo.listar(lista);

			for (Arquivo arquivo : lista) {
				arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));

				if (arquivo.isArquivoAberto()) {
					ArvoreUtil.selecionarObjeto(arvore, arquivo);
				} else {
					ArvoreUtil.refreshEstrutura(arvore, arquivo);
				}
			}

			arvore.clearSelection();

			for (Arquivo arquivo : lista) {
				if (formulario.getFichario().getArquivos().isAtivo(arquivo.getFile())) {
					ArvoreUtil.selecionarObjeto(arvore, arquivo);
				}
			}
		}
	}

	@Override
	public void abrirFormArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.getArquivos().abrir(arquivo.getFile(), false);
		}
	}

	@Override
	public void abrirFichArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			if (chkDuplicar.isSelected()) {
				formulario.getArquivos().abrir(arquivo.getFile(), true);
			} else {
				if (!formulario.getFichario().getArquivos().isAberto(arquivo.getFile())) {
					formulario.getArquivos().abrir(arquivo.getFile(), true);
				}
			}

			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
			formulario.getFichario().getArquivos().selecionarAba(arquivo.getFile());
			ArvoreUtil.refreshEstrutura(arvore, arquivo);
		}
	}

	@Override
	public void selecionarArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.getFichario().getArquivos().selecionarAba(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
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
			formulario.getFichario().getArquivos().selecionarAba(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
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
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
			ArvoreUtil.refreshEstrutura(arvore, arquivo);
		}
	}

	private void baixarArquivo() {
		ArvoreModelo modelo = new ArvoreModelo();
		arvore.setModel(modelo);

		List<Arquivo> lista = new ArrayList<>();
		modelo.listar(lista);

		for (Arquivo arquivo : lista) {
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
		}
	}

	@Override
	public void fecharArquivo(Arvore arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.getFichario().getArquivos().fecharArquivo(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
			ArvoreUtil.refreshEstrutura(arvore, arquivo);
		}
	}
}