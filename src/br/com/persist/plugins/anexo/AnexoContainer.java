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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class AnexoContainer extends AbstratoContainer implements AnexoTreeListener {
	private static final long serialVersionUID = 1L;
	private final AnexoTree anexoTree = new AnexoTree(new AnexoModelo());
	private static final Map<String, Object> map = new HashMap<>();
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

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private final CheckBox chkSempreTopForm = new CheckBox();
		private final CheckBox chkSempreTopAnex = new CheckBox();

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR, SALVAR);
			chkSempreTopForm.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopForm"));
			chkSempreTopAnex.setToolTipText(Mensagens.getString("msg.anexo.sempreTopAnex"));
			add(chkSempreTopAnex);
			add(chkSempreTopForm);

			eventos();
		}

		private void eventos() {
			chkSempreTopAnex.addActionListener(e -> anexoFormulario.setAlwaysOnTop(chkSempreTopAnex.isSelected()));
			chkSempreTopForm.addActionListener(e -> topFormulario());
		}

		private void topFormulario() {
			formulario.setAlwaysOnTop(chkSempreTopForm.isSelected());
			if (chkSempreTopForm.isSelected()) {
				formulario.setExtendedState(Formulario.MAXIMIZED_BOTH);
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

		void formularioVisivel() {
			buttonDestacar.estadoFormulario();
			chkSempreTopAnex.setEnabled(anexoFormulario != null);
		}

		void paginaVisivel() {
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
		}

		@Override
		public void salvar() {
			try (PrintWriter pw = new PrintWriter(AnexoModelo.anexosInfo, StandardCharsets.UTF_8.name())) {
				Set<Entry<String, Anexo>> entrySet = AnexoModelo.getAnexos().entrySet();

				for (Entry<String, Anexo> entry : entrySet) {
					Anexo anexo = entry.getValue();
					pw.println(entry.getKey());
					pw.println(Constantes.ABRIR_VISIVEL + anexo.isAbrirVisivel());
					pw.println(Constantes.PADRAO_ABRIR + anexo.isPadraoAbrir());

					if (!Util.estaVazio(anexo.getNomeIcone())) {
						pw.println(Constantes.ICONE + anexo.getNomeIcone());
					}

					if (anexo.getCorFonte() != null) {
						pw.println(Constantes.COR_FONTE + anexo.getCorFonte().getRGB());
					}

					pw.println();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR_MAPA_ANEXOS", ex, AnexoContainer.this);
			}
		}
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
	public void diretorioAnexo(AnexoTree anexoTree) {
		Anexo anexo = anexoTree.getObjetoSelecionado();

		if (anexo != null) {
			try {
				File file = anexo.getFile();
				File parent = file.getParentFile();

				if (parent != null) {
					desktop.open(parent);
				}
			} catch (IOException e) {
				Util.mensagem(AnexoContainer.this, e.getMessage());
			}
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

		if (resp == null || Util.estaVazio(resp.toString())) {
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
			AnexoCorDialogo form = AnexoCorDialogo.criar((Frame) null, anexo);
			form.setLocationRelativeTo(AnexoContainer.this);
			form.setVisible(true);
			AnexoTreeUtil.refreshEstrutura(anexoTree, anexo);
		}
	}

	@Override
	public void iconeAnexo(AnexoTree anexo) {
		Anexo arquivo = anexo.getObjetoSelecionado();

		if (arquivo != null) {
			AnexoIconeDialogo form = AnexoIconeDialogo.criar((Frame) null, arquivo);
			form.setLocationRelativeTo(AnexoContainer.this);
			form.setVisible(true);
			AnexoTreeUtil.refreshEstrutura(anexo, arquivo);
		}
	}

	@Override
	public void copiarAtributosAnexo(AnexoTree anexo) {
		Anexo arquivo = anexo.getObjetoSelecionado();

		if (arquivo != null) {
			map.put(Constantes.ABRIR_VISIVEL, arquivo.isAbrirVisivel());
			map.put(Constantes.PADRAO_ABRIR, arquivo.isPadraoAbrir());
			map.put(Constantes.COR_FONTE, arquivo.getCorFonte());
			map.put(Constantes.ICONE, arquivo.getNomeIcone());
		}
	}

	@Override
	public void colarAtributosAnexo(AnexoTree anexo) {
		Anexo arquivo = anexo.getObjetoSelecionado();

		if (arquivo == null) {
			return;
		}

		Boolean abrirVisivel = (Boolean) map.get(Constantes.ABRIR_VISIVEL);
		arquivo.setAbrirVisivel(Boolean.TRUE.equals(abrirVisivel));

		Boolean padraoAbrir = (Boolean) map.get(Constantes.PADRAO_ABRIR);
		arquivo.setPadraoAbrir(Boolean.TRUE.equals(padraoAbrir));

		Color corFonte = (Color) map.get(Constantes.COR_FONTE);
		arquivo.setCorFonte(corFonte);

		String nome = (String) map.get(Constantes.ICONE);
		if (!Util.estaVazio(nome)) {
			Icon icone = Imagens.getIcon(nome);
			arquivo.setIcone(icone, nome);
		}

		AnexoModelo.putAnexo(arquivo);
		AnexoTreeUtil.refreshEstrutura(anexo, arquivo);
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.paginaVisivel();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
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
				return Mensagens.getString(Constantes.LABEL_ANEXOS_MIN);
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