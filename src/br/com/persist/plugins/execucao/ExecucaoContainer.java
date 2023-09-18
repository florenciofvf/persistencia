package br.com.persist.plugins.execucao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.TextPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;

public class ExecucaoContainer extends AbstratoContainer {
	private static final Map<String, EditorCor> mapEditorCor;
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private ExecucaoFormulario execucaoFormulario;
	private ExecucaoDialogo execucaoDialogo;
	private final ExecucaoSplit split;

	public ExecucaoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		split = new ExecucaoSplit(formulario);
		split.inicializar();
		toolbar.ini(janela);
		montarLayout();
	}

	public ExecucaoDialogo getExecucaoDialogo() {
		return execucaoDialogo;
	}

	public void setExecucaoDialogo(ExecucaoDialogo execucaoDialogo) {
		this.execucaoDialogo = execucaoDialogo;
		if (execucaoDialogo != null) {
			execucaoFormulario = null;
		}
	}

	public ExecucaoFormulario getExecucaoFormulario() {
		return execucaoFormulario;
	}

	public void setExecucaoFormulario(ExecucaoFormulario execucaoFormulario) {
		this.execucaoFormulario = execucaoFormulario;
		if (execucaoFormulario != null) {
			execucaoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, split);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, BAIXAR, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(ExecucaoContainer.this)) {
				ExecucaoFormulario.criar(formulario, ExecucaoContainer.this);
			} else if (execucaoDialogo != null) {
				execucaoDialogo.excluirContainer();
				ExecucaoFormulario.criar(formulario, ExecucaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (execucaoFormulario != null) {
				execucaoFormulario.excluirContainer();
				formulario.adicionarPagina(ExecucaoContainer.this);
			} else if (execucaoDialogo != null) {
				execucaoDialogo.excluirContainer();
				formulario.adicionarPagina(ExecucaoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (execucaoDialogo != null) {
				execucaoDialogo.excluirContainer();
			}
			ExecucaoFormulario.criar(formulario);
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
		protected void baixar() {
			split.inicializar();
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
		try {
			split.salvar();
		} catch (XMLException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return ExecucaoFabrica.class;
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
				return ExecucaoMensagens.getString(ExecucaoConstantes.LABEL_EXECUCOES_MIN);
			}

			@Override
			public String getTitulo() {
				return ExecucaoMensagens.getString(ExecucaoConstantes.LABEL_EXECUCOES);
			}

			@Override
			public String getHint() {
				return ExecucaoMensagens.getString(ExecucaoConstantes.LABEL_EXECUCOES);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL;
			}
		};
	}

	static {
		mapEditorCor = new HashMap<>();
		mapEditorCor.put("git", new GitCor());
	}

	public static EditorCor getEditorCor(String chave) {
		return mapEditorCor.get(chave);
	}
}

interface EditorCor {
	void processar(TextPane textPane, StringBuilder sb);
}