package br.com.persist.anexo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.Icon;

import java.util.Set;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoIconeDialogo;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.ScrollPane;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.Fichario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Imagens;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class AnexoTreeContainer extends AbstratoContainer implements AnexoTreeListener, Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private AnexoTree anexoTree = new AnexoTree(new AnexoTreeModelo(true));
	private final CheckBox chkSempreTopForm = new CheckBox();
	private final CheckBox chkSempreTopAnex = new CheckBox();
	private AnexoTreeFormulario anexoTreeFormulario;
	private final Toolbar toolbar = new Toolbar();
	private final transient Desktop desktop;

	public AnexoTreeContainer(IJanela janela, Formulario formulario) {
		super(formulario);
		this.desktop = Desktop.getDesktop();
		toolbar.ini(janela);
		montarLayout();
		baixarArquivo();
	}

	public AnexoTreeFormulario getAnexoTreeFormulario() {
		return anexoTreeFormulario;
	}

	public void setAnexoTreeFormulario(AnexoTreeFormulario anexoTreeFormulario) {
		this.anexoTreeFormulario = anexoTreeFormulario;
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		chkSempreTopForm.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopForm"));
		chkSempreTopAnex.setToolTipText(Mensagens.getString("msg.anexo.sempreTopAnex"));
		add(BorderLayout.CENTER, new ScrollPane(anexoTree));
		add(BorderLayout.NORTH, toolbar);
		anexoTree.adicionarOuvinte(this);
	}

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().getAnexoTree().destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().getAnexoTree().clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		AnexoTreeFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (anexoTreeFormulario != null) {
			anexoTreeFormulario.retornoAoFichario();
		}
	}

	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(IJanela janela) {
			super.ini(janela, false, true);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_ANEXO);
			configBaixarAcao(e -> baixarArquivo());

			if (anexoTreeFormulario != null) {
				add(chkSempreTopAnex);
			}
			add(chkSempreTopForm);

			chkSempreTopAnex.addActionListener(e -> anexoTreeFormulario.setAlwaysOnTop(chkSempreTopAnex.isSelected()));
			chkSempreTopForm.addActionListener(e -> {
				formulario.setAlwaysOnTop(chkSempreTopForm.isSelected());
				if (chkSempreTopForm.isSelected()) {
					formulario.setExtendedState(Formulario.MAXIMIZED_BOTH);
				}
			});
		}

		@Override
		public void salvar() {
			try (PrintWriter pw = new PrintWriter(AnexoTreeModelo.anexosInfo, StandardCharsets.UTF_8.name())) {
				Set<Entry<String, Arquivo>> entrySet = AnexoTreeModelo.getArquivos().entrySet();

				for (Entry<String, Arquivo> entry : entrySet) {
					Arquivo arquivo = entry.getValue();
					pw.println(entry.getKey());
					pw.println(Constantes.ABRIR_VISIVEL + arquivo.isAbrirVisivel());
					pw.println(Constantes.PADRAO_ABRIR + arquivo.isPadraoAbrir());

					if (!Util.estaVazio(arquivo.getNomeIcone())) {
						pw.println(Constantes.ICONE + arquivo.getNomeIcone());
					}

					if (arquivo.getCorFonte() != null) {
						pw.println(Constantes.COR_FONTE + arquivo.getCorFonte().getRGB());
					}

					pw.println();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR_MAPA_ANEXOS", ex, AnexoTreeContainer.this);
			}
		}
	}

	@Override
	public void imprimirArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		try {
			desktop.print(arquivo.getFile());
		} catch (IOException e) {
			Util.mensagem(AnexoTreeContainer.this, e.getMessage());
		}
	}

	@Override
	public void editarArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		try {
			desktop.edit(arquivo.getFile());
		} catch (IOException e) {
			Util.mensagem(AnexoTreeContainer.this, e.getMessage());
		}
	}

	@Override
	public void abrirArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		try {
			desktop.open(arquivo.getFile());
		} catch (IOException e) {
			Util.mensagem(AnexoTreeContainer.this, e.getMessage());
		}
	}

	@Override
	public void pastaArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		try {
			File file = arquivo.getFile();
			File parent = file.getParentFile();

			if (parent != null) {
				desktop.open(parent);
			}
		} catch (IOException e) {
			Util.mensagem(AnexoTreeContainer.this, e.getMessage());
		}
	}

	@Override
	public void excluirArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo != null && arquivo.getPai() != null && !AnexoTreeModelo.anexosInfo.equals(arquivo.getFile())
				&& Util.confirmaExclusao(AnexoTreeContainer.this, false)) {
			arquivo.excluir();
			AnexoTreeUtil.excluirEstrutura(anexo, arquivo);
		}
	}

	@Override
	public void renomearArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null || arquivo.getPai() == null || AnexoTreeModelo.anexosInfo.equals(arquivo.getFile())) {
			return;
		}

		Object resp = Util.getValorInputDialog(AnexoTreeContainer.this, "label.renomear", arquivo.toString(),
				arquivo.toString());

		if (resp == null || Util.estaVazio(resp.toString())) {
			return;
		}

		if (arquivo.renomear(resp.toString())) {
			AnexoTreeUtil.refreshEstrutura(anexo, arquivo);
			AnexoTreeModelo.putArquivo(arquivo);
		}
	}

	@Override
	public void corFonteArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		AnexoCorDialogo form = new AnexoCorDialogo((Frame) null, arquivo);
		form.setLocationRelativeTo(AnexoTreeContainer.this);
		form.setVisible(true);
		AnexoTreeUtil.refreshEstrutura(anexo, arquivo);
	}

	@Override
	public void iconeArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		ArquivoIconeDialogo form = new ArquivoIconeDialogo((Frame) null, arquivo);
		form.setLocationRelativeTo(AnexoTreeContainer.this);
		form.setVisible(true);
		AnexoTreeUtil.refreshEstrutura(anexo, arquivo);
	}

	@Override
	public void copiarAtributosArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		Formulario.getMap().put(Constantes.ABRIR_VISIVEL, arquivo.isAbrirVisivel());
		Formulario.getMap().put(Constantes.PADRAO_ABRIR, arquivo.isPadraoAbrir());
		Formulario.getMap().put(Constantes.COR_FONTE, arquivo.getCorFonte());
		Formulario.getMap().put(Constantes.ICONE, arquivo.getNomeIcone());
	}

	@Override
	public void colarAtributosArquivo(AnexoTree anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		Boolean abrirVisivel = (Boolean) Formulario.getMap().get(Constantes.ABRIR_VISIVEL);

		if (abrirVisivel != null) {
			arquivo.setAbrirVisivel(abrirVisivel);
		}

		Boolean padraoAbrir = (Boolean) Formulario.getMap().get(Constantes.PADRAO_ABRIR);

		if (padraoAbrir != null) {
			arquivo.setPadraoAbrir(padraoAbrir);
		}

		Color corFonte = (Color) Formulario.getMap().get(Constantes.COR_FONTE);

		if (corFonte != null) {
			arquivo.setCorFonte(corFonte);
		}

		String nome = (String) Formulario.getMap().get(Constantes.ICONE);

		if (!Util.estaVazio(nome)) {
			Icon icone = Imagens.getIcon(nome);
			arquivo.setIcone(icone, nome);
		}

		AnexoTreeModelo.putArquivo(arquivo);
		AnexoTreeUtil.refreshEstrutura(anexo, arquivo);
	}

	private void baixarArquivo() {
		AnexoTreeModelo modelo = new AnexoTreeModelo(true);
		anexoTree.setModel(modelo);

		Set<Entry<String, Arquivo>> entrySet = AnexoTreeModelo.getArquivos().entrySet();
		Iterator<Entry<String, Arquivo>> iterator = entrySet.iterator();
		boolean removido = false;

		while (iterator.hasNext()) {
			Entry<String, Arquivo> next = iterator.next();

			if (!next.getValue().isChecado()) {
				iterator.remove();
				removido = true;
			}
		}

		if (removido) {
			toolbar.getSalvarAcao().actionPerformed(null);
		}

		modelo.abrirVisivel(anexoTree);
	}
}