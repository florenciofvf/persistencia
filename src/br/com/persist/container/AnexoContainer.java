package br.com.persist.container;

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

import br.com.persist.Arquivo;
import br.com.persist.anexo.Anexo;
import br.com.persist.anexo.AnexoUtil;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.ScrollPane;
import br.com.persist.dialogo.ArquivoCorDialogo;
import br.com.persist.dialogo.ArquivoIconeDialogo;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.AnexoFormulario;
import br.com.persist.listener.AnexoListener;
import br.com.persist.modelo.AnexoModelo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Imagens;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class AnexoContainer extends AbstratoContainer implements AnexoListener, Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkSempreTopForm = new CheckBox();
	private final CheckBox chkSempreTopAnex = new CheckBox();
	private Anexo anexo = new Anexo(new AnexoModelo(true));
	private final Toolbar toolbar = new Toolbar();
	private AnexoFormulario anexoFormulario;
	private final transient Desktop desktop;

	public AnexoContainer(IJanela janela, Formulario formulario) {
		super(formulario);
		this.desktop = Desktop.getDesktop();
		toolbar.ini(janela);
		montarLayout();
		baixarArquivo();
	}

	public AnexoFormulario getAnexoFormulario() {
		return anexoFormulario;
	}

	public void setAnexoFormulario(AnexoFormulario anexoFormulario) {
		this.anexoFormulario = anexoFormulario;
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		chkSempreTopForm.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopForm"));
		chkSempreTopAnex.setToolTipText(Mensagens.getString("msg.anexo.sempreTopAnex"));
		add(BorderLayout.CENTER, new ScrollPane(anexo));
		add(BorderLayout.NORTH, toolbar);
		anexo.adicionarOuvinte(this);
	}

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().getAnexos().destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().getAnexos().clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		AnexoFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (anexoFormulario != null) {
			anexoFormulario.retornoAoFichario();
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
		}

		@Override
		public void salvar() {
			try (PrintWriter pw = new PrintWriter(AnexoModelo.anexosInfo, StandardCharsets.UTF_8.name())) {
				Set<Entry<String, Arquivo>> entrySet = AnexoModelo.getArquivos().entrySet();

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
				Util.stackTraceAndMessage("SALVAR_MAPA_ANEXOS", ex, AnexoContainer.this);
			}
		}
	}

	@Override
	public void imprimirArquivo(Anexo anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		try {
			desktop.print(arquivo.getFile());
		} catch (IOException e) {
			Util.mensagem(AnexoContainer.this, e.getMessage());
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
	public void pastaArquivo(Anexo anexo) {
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
			Util.mensagem(AnexoContainer.this, e.getMessage());
		}
	}

	@Override
	public void excluirArquivo(Anexo anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo != null && arquivo.getPai() != null && !AnexoModelo.anexosInfo.equals(arquivo.getFile())
				&& Util.confirmaExclusao(AnexoContainer.this)) {
			arquivo.excluir();
			AnexoUtil.excluirEstrutura(anexo, arquivo);
		}
	}

	@Override
	public void renomearArquivo(Anexo anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null || arquivo.getPai() == null || AnexoModelo.anexosInfo.equals(arquivo.getFile())) {
			return;
		}

		Object resp = Util.getValorInputDialog(AnexoContainer.this, "label.renomear", arquivo.toString(),
				arquivo.toString());

		if (resp == null || Util.estaVazio(resp.toString())) {
			return;
		}

		if (arquivo.renomear(resp.toString())) {
			AnexoUtil.refreshEstrutura(anexo, arquivo);
			AnexoModelo.putArquivo(arquivo);
		}
	}

	@Override
	public void corFonteArquivo(Anexo anexo) {
		Arquivo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		ArquivoCorDialogo form = new ArquivoCorDialogo((Frame) null, arquivo);
		form.setLocationRelativeTo(AnexoContainer.this);
		form.setVisible(true);
		AnexoUtil.refreshEstrutura(anexo, arquivo);
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

	@Override
	public void copiarAtributosArquivo(Anexo anexo) {
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
	public void colarAtributosArquivo(Anexo anexo) {
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

		AnexoModelo.putArquivo(arquivo);
		AnexoUtil.refreshEstrutura(anexo, arquivo);
	}

	private void baixarArquivo() {
		AnexoModelo modelo = new AnexoModelo(true);
		anexo.setModel(modelo);

		Set<Entry<String, Arquivo>> entrySet = AnexoModelo.getArquivos().entrySet();
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

		modelo.abrirVisivel(anexo);
	}
}