package br.com.persist.plugins.propriedade;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.swing.Icon;
import javax.swing.JSplitPane;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.update.UpdateConstantes;

public class PropriedadeContainer extends AbstratoContainer {
	private final TextPane textAreaResult = new TextPane();
	private PropriedadeFormulario propriedadeFormulario;
	private final TextPane textArea = new TextPane();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private PropriedadeDialogo propriedadeDialogo;
	private final File file;

	public PropriedadeContainer(Janela janela, Formulario formulario) {
		super(formulario);
		file = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR + PropriedadeConstantes.PROPRIEDADES
				+ ".xml");
		toolbar.ini(janela);
		montarLayout();
		abrir();
	}

	public PropriedadeDialogo getPropriedadeDialogo() {
		return propriedadeDialogo;
	}

	public void setPropriedadeDialogo(PropriedadeDialogo propriedadeDialogo) {
		this.propriedadeDialogo = propriedadeDialogo;
		if (propriedadeDialogo != null) {
			propriedadeFormulario = null;
		}
	}

	public PropriedadeFormulario getPropriedadeFormulario() {
		return propriedadeFormulario;
	}

	public void setPropriedadeFormulario(PropriedadeFormulario propriedadeFormulario) {
		this.propriedadeFormulario = propriedadeFormulario;
		if (propriedadeFormulario != null) {
			propriedadeDialogo = null;
		}
	}

	private void montarLayout() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new ScrollPane(textArea),
				new ScrollPane(textAreaResult));
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, splitPane);
	}

	private void abrir() {
		abrirArquivo(file);
	}

	private void abrirArquivo(File file) {
		textArea.limpar();
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					textArea.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action gerarAcao = acaoIcon("label.gerar_conteudo", Icones.EXECUTAR);

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR, SALVAR);
			addButton(gerarAcao);
			gerarAcao.setActionListener(e -> gerar());
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(PropriedadeMensagens.getString(chave), icon);
		}

		private void gerar() {
			String string = textArea.getText();
			if (Util.estaVazio(string)) {
				textAreaResult.setText("Editor vazio.");
				return;
			}
			try {
				Raiz raiz = PropriedadeUtil.criarRaiz(string);
				textAreaResult.setText(PropriedadeUtil.getString(raiz));
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(PropriedadeContainer.this)) {
				PropriedadeFormulario.criar(formulario, PropriedadeContainer.this);
			} else if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
				PropriedadeFormulario.criar(formulario, PropriedadeContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (propriedadeFormulario != null) {
				propriedadeFormulario.excluirContainer();
				formulario.adicionarPagina(PropriedadeContainer.this);
			} else if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
				formulario.adicionarPagina(PropriedadeContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
			}
			PropriedadeFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		public void baixar() {
			abrir();
		}

		@Override
		public void salvar() {
			if (Util.confirmaSalvar(PropriedadeContainer.this, Constantes.TRES)) {
				salvarArquivo(file);
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(UpdateConstantes.PAINEL_UPDATE, ex, PropriedadeContainer.this);
			}
		}
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
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.dialogOpenedHandler(dialog);
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return PropriedadeFabrica.class;
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
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE_MIN);
			}

			@Override
			public String getTitulo() {
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE);
			}

			@Override
			public String getHint() {
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE);
			}

			@Override
			public Icon getIcone() {
				return Icones.EDIT;
			}
		};
	}
}