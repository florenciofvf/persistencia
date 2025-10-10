package br.com.persist.plugins.execucao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginArvore;
import br.com.persist.arquivo.ArquivoPesquisa;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.TextPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;

public class ExecucaoContainer extends AbstratoContainer implements PluginArvore {
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
		split.inicializar(this);
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

	public void focusInputPesquisarTree() {
		toolbar.focusInputPesquisar();
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private transient ArquivoPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, BAIXAR, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(chkPorParte);
			add(chkPsqConteudo);
			add(label);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					split.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected());
					Util.mensagem(ExecucaoContainer.this, getString(set));
				} else {
					pesquisa = split.getTree().getPesquisa(pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}

		private String getString(Set<String> set) {
			StringBuilder sb = new StringBuilder();
			for (String string : set) {
				if (sb.length() > 0) {
					sb.append(Constantes.QL);
				}
				sb.append(string);
			}
			return sb.toString();
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
			split.inicializar(ExecucaoContainer.this);
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