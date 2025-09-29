package br.com.persist.plugins.anexo;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class AnexoContainer extends AbstratoContainer implements AnexoTreeListener {
	private final AnexoTree anexoTree = new AnexoTree(new AnexoModelo());
	private static final Map<String, Object> map = new HashMap<>();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private AnexoFormulario anexoFormulario;
	private final transient Desktop desktop;

	public AnexoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		this.desktop = Desktop.getDesktop();
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public AnexoFormulario getAnexoFormulario() {
		return anexoFormulario;
	}

	public void setAnexoFormulario(AnexoFormulario anexoFormulario) {
		this.anexoFormulario = anexoFormulario;
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(anexoTree));
	}

	private void configurar() {
		anexoTree.adicionarOuvinte(this);
		toolbar.baixar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private final CheckBox chkSempreTopForm = new CheckBox();
		private final CheckBox chkSempreTopAnex = new CheckBox();
		private final CheckBox chkPorParte = new CheckBox(true);
		private final TextField txtAnexo = new TextField(35);
		private static final long serialVersionUID = 1L;
		private transient AnexoPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR, SALVAR);
			chkSempreTopAnex.setToolTipText(AnexoMensagens.getString("msg.anexo.sempreTopAnex"));
			chkSempreTopForm.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopForm"));
			chkPorParte.setToolTipText(Mensagens.getString("label.por_parte"));
			txtAnexo.setToolTipText(Mensagens.getString("label.pesquisar"));
			add(chkSempreTopAnex);
			add(chkSempreTopForm);
			add(txtAnexo);
			add(chkPorParte);
			add(label);
			eventos();
		}

		@Override
		protected void focusInputPesquisar() {
			txtAnexo.requestFocus();
		}

		private void eventos() {
			chkSempreTopAnex.addActionListener(e -> anexoFormulario.setAlwaysOnTop(chkSempreTopAnex.isSelected()));
			chkSempreTopForm.addActionListener(e -> topFormulario());
			txtAnexo.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtAnexo.getText())) {
				pesquisa = getPesquisa(anexoTree, pesquisa, txtAnexo.getText(), chkPorParte.isSelected());
				pesquisa.selecionar(label);
			} else {
				label.limpar();
			}
		}

		public AnexoPesquisa getPesquisa(AnexoTree arquivoTree, AnexoPesquisa pesquisa, String string,
				boolean porParte) {
			if (pesquisa == null) {
				return new AnexoPesquisa(arquivoTree, string, porParte);
			} else if (pesquisa.igual(string, porParte)) {
				return pesquisa;
			}
			return new AnexoPesquisa(arquivoTree, string, porParte);
		}

		private void topFormulario() {
			formulario.setAlwaysOnTop(chkSempreTopForm.isSelected());
			if (chkSempreTopForm.isSelected()) {
				formulario.setExtendedState(Frame.MAXIMIZED_BOTH);
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(AnexoContainer.this)) {
				AnexoFormulario.criar(formulario, AnexoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (anexoFormulario != null) {
				anexoFormulario.excluirContainer();
				formulario.adicionarPagina(AnexoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			AnexoFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
			chkSempreTopAnex.setEnabled(anexoFormulario != null);
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
			chkSempreTopAnex.setEnabled(anexoFormulario != null);
		}

		@Override
		public void baixar() {
			AnexoModelo modelo = new AnexoModelo();
			anexoTree.setModel(modelo);
			Set<Entry<String, Anexo>> entrySet = AnexoModelo.getAnexos().entrySet();
			Iterator<Entry<String, Anexo>> iterator = entrySet.iterator();
			boolean removido = false;
			while (iterator.hasNext()) {
				Entry<String, Anexo> next = iterator.next();
				if (!next.getValue().isChecado()) {
					iterator.remove();
					removido = true;
				}
			}
			if (removido) {
				salvar();
			}
			modelo.abrirVisivel(anexoTree);
			pesquisa = null;
			label.limpar();
		}

		@Override
		public void salvar() {
			try (PrintWriter pw = new PrintWriter(AnexoModelo.anexosInfo, StandardCharsets.UTF_8.name())) {
				Set<Entry<String, Anexo>> entrySet = AnexoModelo.getAnexos().entrySet();
				for (Entry<String, Anexo> entry : entrySet) {
					Anexo anexo = entry.getValue();
					pw.println(entry.getKey());
					pw.println(AnexoConstantes.ABRIR_VISIVEL + anexo.isAbrirVisivel());
					pw.println(AnexoConstantes.PADRAO_ABRIR + anexo.isPadraoAbrir());
					if (!Util.isEmpty(anexo.getNomeIcone())) {
						pw.println(Constantes.ICONE + anexo.getNomeIcone());
					}
					if (anexo.getCorFonte() != null) {
						pw.println(Constantes.COR_FONTE + anexo.getCorFonte().getRGB());
					}
					pw.println();
				}
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR_MAPA_ANEXOS", ex, AnexoContainer.this);
			}
		}
	}

	@Override
	public void focusInputPesquisar(AnexoTree anexoTree) {
		toolbar.focusInputPesquisar();
	}

	@Override
	public void imprimirAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();
		if (anexo != null) {
			try {
				desktop.print(anexo.getFile());
			} catch (IOException e) {
				Util.mensagem(AnexoContainer.this, e.getMessage());
			}
		}
	}

	@Override
	public void editarAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();
		if (anexo != null) {
			try {
				desktop.edit(anexo.getFile());
			} catch (IOException e) {
				Util.mensagem(AnexoContainer.this, e.getMessage());
			}
		}
	}

	@Override
	public void abrirAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();
		if (anexo != null) {
			try {
				desktop.open(anexo.getFile());
			} catch (IOException e) {
				Util.mensagem(AnexoContainer.this, e.getMessage());
			}
		}
	}

	@Override
	public void conteudoAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();
		if (anexo != null) {
			conteudo(anexo);
		}
	}

	private void conteudo(Anexo anexo) {
		try {
			Util.conteudo(AnexoContainer.this, anexo.getFile());
		} catch (IOException e) {
			Util.mensagem(AnexoContainer.this, e.getMessage());
		}
	}

	@Override
	public void clonarAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();
		if (anexo != null) {
			clonar(anexoTree, anexo);
		}
	}

	private void clonar(AnexoTree anexoTree, Anexo anexo) {
		try {
			AtomicReference<File> ref = new AtomicReference<>();
			String resp = Util.clonar(AnexoContainer.this, anexo.getFile(), ref);
			if (Preferencias.isExibirTotalBytesClonados()) {
				Util.mensagem(AnexoContainer.this, resp);
			}
			adicionar(anexoTree, anexo.getPai(), ref.get());
		} catch (IOException e) {
			Util.mensagem(AnexoContainer.this, e.getMessage());
		}
	}

	private void adicionar(AnexoTree anexoTree, Anexo anexo, File file) {
		if (anexo != null && file != null) {
			Anexo novo = anexo.adicionar(file);
			if (novo != null) {
				AnexoTreeUtil.atualizarEstrutura(anexoTree, anexo);
				requestFocus();
				AnexoTreeUtil.selecionarObjeto(anexoTree, novo);
				anexoTree.repaint();
			}
		}
	}

	@Override
	public void diretorioAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();
		if (anexo != null) {
			desktopOpen(anexo);
		}
	}

	private void desktopOpen(Anexo anexo) {
		try {
			ArquivoUtil.diretorio(anexo.getFile());
		} catch (IOException e) {
			Util.mensagem(AnexoContainer.this, e.getMessage());
		}
	}

	@Override
	public void excluirAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();
		if (anexo != null && anexo.getPai() != null && !AnexoModelo.anexosInfo.equals(anexo.getFile())
				&& Util.confirmaExclusao(AnexoContainer.this, false)) {
			anexo.excluir();
			AnexoTreeUtil.excluirEstrutura(anexoTree, anexo);
		}
	}

	@Override
	public void renomearAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();
		if (anexo == null || anexo.getPai() == null || AnexoModelo.anexosInfo.equals(anexo.getFile())) {
			return;
		}
		Object resp = Util.getValorInputDialog(AnexoContainer.this, "label.renomear", anexo.toString(),
				anexo.toString());
		if (resp == null || Util.isEmpty(resp.toString())) {
			return;
		}
		if (anexo.renomear(resp.toString())) {
			AnexoTreeUtil.refreshEstrutura(anexoTree, anexo);
			AnexoModelo.putAnexo(anexo);
		}
	}

	@Override
	public void corFonteAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();
		if (anexo != null) {
			Frame frame = Util.getViewParentFrame(AnexoContainer.this);
			AnexoCorDialogo form = AnexoCorDialogo.criar(frame, anexo);
			config(frame, form);
			form.setVisible(true);
			AnexoTreeUtil.refreshEstrutura(anexoTree, anexo);
		}
	}

	private void config(Frame frame, AbstratoDialogo form) {
		Util.configSizeLocation(frame, form, AnexoContainer.this);
	}

	@Override
	public void iconeAnexo(AnexoTree anexoTree) {
		Anexo arquivo = anexoTree.getObjetoSelecionado();
		if (arquivo != null) {
			Frame frame = Util.getViewParentFrame(AnexoContainer.this);
			AnexoIconeDialogo form = AnexoIconeDialogo.criar(frame, arquivo);
			config(frame, form);
			form.setVisible(true);
			AnexoTreeUtil.refreshEstrutura(anexoTree, arquivo);
		}
	}

	@Override
	public void copiarAtributosAnexo(AnexoTree anexoTree) {
		Anexo arquivo = anexoTree.getObjetoSelecionado();
		if (arquivo != null) {
			map.put(AnexoConstantes.ABRIR_VISIVEL, arquivo.isAbrirVisivel());
			map.put(AnexoConstantes.PADRAO_ABRIR, arquivo.isPadraoAbrir());
			map.put(Constantes.COR_FONTE, arquivo.getCorFonte());
			map.put(Constantes.ICONE, arquivo.getNomeIcone());
		}
	}

	@Override
	public void colarAtributosAnexo(AnexoTree anexoTree) throws AssistenciaException {
		Anexo arquivo = anexoTree.getObjetoSelecionado();
		if (arquivo == null) {
			return;
		}
		Boolean abrirVisivel = (Boolean) map.get(AnexoConstantes.ABRIR_VISIVEL);
		Boolean padraoAbrir = (Boolean) map.get(AnexoConstantes.PADRAO_ABRIR);
		arquivo.setAbrirVisivel(Boolean.TRUE.equals(abrirVisivel));
		arquivo.setPadraoAbrir(Boolean.TRUE.equals(padraoAbrir));
		Color corFonte = (Color) map.get(Constantes.COR_FONTE);
		arquivo.setCorFonte(corFonte);
		String nome = (String) map.get(Constantes.ICONE);
		if (!Util.isEmpty(nome)) {
			Icon icone = Imagens.getIcon(nome);
			arquivo.setIcone(icone, nome);
		}
		AnexoModelo.putAnexo(arquivo);
		AnexoTreeUtil.refreshEstrutura(anexoTree, arquivo);
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return AnexoFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return AnexoMensagens.getString(AnexoConstantes.LABEL_ANEXOS_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_ANEXOS);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_ANEXOS);
			}

			@Override
			public Icon getIcone() {
				return Icones.ANEXO;
			}
		};
	}
}