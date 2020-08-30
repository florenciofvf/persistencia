package br.com.persist.arquivo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.ScrollPane;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.icone.Icones;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class ArquivoTreeContainer extends AbstratoContainer implements ArquivoTreeListener {
	private static final long serialVersionUID = 1L;
	private final ArquivoTree arquivoTree = new ArquivoTree(new ArquivoTreeModelo());
	private final CheckBox chkSempreTopForm = new CheckBox();
	private final CheckBox chkSempreTopArvo = new CheckBox();
	private final CheckBox chkLinkAuto = new CheckBox();
	private final CheckBox chkDuplicar = new CheckBox();
	private ArquivoTreeFormulario arquivoTreeFormulario;
	private final Toolbar toolbar = new Toolbar();

	public ArquivoTreeContainer(IJanela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		baixarArquivo();
	}

	public ArquivoTreeFormulario getArquivoTreeFormulario() {
		return arquivoTreeFormulario;
	}

	public void setArquivoTreeFormulario(ArquivoTreeFormulario arquivoTreeFormulario) {
		this.arquivoTreeFormulario = arquivoTreeFormulario;
	}

	private void montarLayout() {
		chkSempreTopForm.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopForm"));
		chkSempreTopArvo.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopArqu"));
		chkLinkAuto.setToolTipText(Mensagens.getString("msg.arquivo.link_auto"));
		chkDuplicar.setToolTipText(Mensagens.getString("msg.arquivo.duplicar"));
		add(BorderLayout.CENTER, new ScrollPane(arquivoTree));
		add(BorderLayout.NORTH, toolbar);
		arquivoTree.adicionarOuvinte(this);
		chkLinkAuto.setSelected(true);
	}

	@Override
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
			configBaixarAcao(e -> baixarArquivo());
			add(new Button(statusAcao));
			if (arquivoTreeFormulario != null) {
				add(chkSempreTopArvo);
			}
			add(chkSempreTopForm);
			add(chkLinkAuto);
			add(chkDuplicar);
			add(fecharAcao);

			chkSempreTopArvo
					.addActionListener(e -> arquivoTreeFormulario.setAlwaysOnTop(chkSempreTopArvo.isSelected()));
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
			ArquivoTreeModelo modelo = (ArquivoTreeModelo) arquivoTree.getModel();

			List<Arquivo> lista = new ArrayList<>();
			modelo.listar(lista);

			for (Arquivo arquivo : lista) {
				arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));

				if (arquivo.isArquivoAberto()) {
					ArquivoTreeUtil.selecionarObjeto(arquivoTree, arquivo);
				} else {
					ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
				}
			}

			arquivoTree.clearSelection();

			for (Arquivo arquivo : lista) {
				if (formulario.getFichario().getArquivos().isAtivo(arquivo.getFile())) {
					ArquivoTreeUtil.selecionarObjeto(arquivoTree, arquivo);
				}
			}
		}
	}

	@Override
	public void abrirFormArquivo(ArquivoTree arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.getArquivos().abrir(arquivo.getFile(), false, null);
		}
	}

	@Override
	public void pastaArquivo(ArquivoTree arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		Desktop desktop = Desktop.getDesktop();

		try {
			File file = arquivo.getFile();
			File parent = file.getParentFile();

			if (parent != null) {
				desktop.open(parent);
			}
		} catch (IOException e) {
			Util.mensagem(ArquivoTreeContainer.this, e.getMessage());
		}
	}

	@Override
	public void abrirFichArquivo(ArquivoTree arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			if (chkDuplicar.isSelected()) {
				formulario.getArquivos().abrir(arquivo.getFile(), true, null);
			} else {
				if (!formulario.getFichario().getArquivos().isAberto(arquivo.getFile())) {
					formulario.getArquivos().abrir(arquivo.getFile(), true, null);
				}
			}

			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
			formulario.getFichario().getArquivos().selecionarAba(arquivo.getFile());
			ArquivoTreeUtil.refreshEstrutura(arvore, arquivo);
		}
	}

	@Override
	public void selecionarArquivo(ArquivoTree arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.getFichario().getArquivos().selecionarAba(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arvore, arquivo);

			if (arquivo.isFile()) {
				formulario.toFront();
			}
		}
	}

	@Override
	public void clickArquivo(ArquivoTree arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null && chkLinkAuto.isSelected()) {
			formulario.getFichario().getArquivos().selecionarAba(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arvore, arquivo);

			if (arquivo.isFile()) {
				formulario.toFront();
			}
		}
	}

	@Override
	public void excluirArquivo(ArquivoTree arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();
		File root = ArquivoTreeUtil.getRoot(this.arquivoTree);

		if (arquivo != null && arquivo.getPai() != null && root != null && !root.equals(arquivo.getFile())
				&& Util.confirmaExclusao(ArquivoTreeContainer.this, false)) {
			arquivo.excluir();
			ArquivoTreeUtil.excluirEstrutura(arvore, arquivo);
		}
	}

	@Override
	public void atualizarArvore(ArquivoTree arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arvore, arquivo);
		}
	}

	private void baixarArquivo() {
		ArquivoTreeModelo modelo = new ArquivoTreeModelo();
		arquivoTree.setModel(modelo);

		List<Arquivo> lista = new ArrayList<>();
		modelo.listar(lista);

		for (Arquivo arquivo : lista) {
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
		}
	}

	@Override
	public void fecharArquivo(ArquivoTree arvore) {
		Arquivo arquivo = arvore.getObjetoSelecionado();

		if (arquivo != null) {
			formulario.getFichario().getArquivos().fecharArquivo(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.getFichario().getArquivos().isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arvore, arquivo);
		}
	}

	@Override
	protected void destacarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			ArquivoTreeFormulario.criar(formulario, this);
		}
	}

	@Override
	protected void clonarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			ArquivoTreeFormulario.criar(formulario);
		}
	}

	@Override
	protected void abrirEmFormulario() {
		ArquivoTreeFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (arquivoTreeFormulario != null) {
			arquivoTreeFormulario.retornoAoFichario();
			formulario.adicionarFicharioAba(this);
		}
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(getClasseFabricaEContainerDetalhe());
	}

	@Override
	public String getClasseFabricaEContainerDetalhe() {
		return classeFabricaEContainer(ArquivoTreeFabrica.class, ArquivoTreeContainer.class);
	}

	@Override
	public String getChaveTituloMin() {
		return Constantes.LABEL_ARQUIVOS_MIN;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getChaveTitulo() {
		return Constantes.LABEL_ARQUIVOS;
	}

	@Override
	public String getHintTitulo() {
		return Mensagens.getString(Constantes.LABEL_ARQUIVOS);
	}

	@Override
	public Icon getIcone() {
		return Icones.EXPANDIR;
	}
}