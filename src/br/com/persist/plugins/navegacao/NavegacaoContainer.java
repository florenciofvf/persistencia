package br.com.persist.plugins.navegacao;

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
import java.util.LinkedHashSet;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.arquivo.ArquivoPesquisa;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;

public class NavegacaoContainer extends AbstratoContainer {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private NavegacaoFormulario navegacaoFormulario;
	private NavegacaoDialogo navegacaoDialogo;
	private final NavegacaoSplit split;

	public NavegacaoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		split = new NavegacaoSplit();
		split.inicializar(this);
		toolbar.ini(janela);
		montarLayout();
	}

	public NavegacaoDialogo getNavegacaoDialogo() {
		return navegacaoDialogo;
	}

	public void setNavegacaoDialogo(NavegacaoDialogo navegacaoDialogo) {
		this.navegacaoDialogo = navegacaoDialogo;
		if (navegacaoDialogo != null) {
			navegacaoFormulario = null;
		}
	}

	public NavegacaoFormulario getNavegacaoFormulario() {
		return navegacaoFormulario;
	}

	public void setNavegacaoFormulario(NavegacaoFormulario navegacaoFormulario) {
		this.navegacaoFormulario = navegacaoFormulario;
		if (navegacaoFormulario != null) {
			navegacaoDialogo = null;
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

	public void focusInputPesquisar() {
		toolbar.focusInputPesquisar();
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private final CheckBox chkPorParte = new CheckBox(true);
		private final TextField txtArquivo = new TextField(35);
		private final CheckBox chkPsqConteudo = new CheckBox();
		private static final long serialVersionUID = 1L;
		private transient ArquivoPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, BAIXAR, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO);
			chkPsqConteudo.setToolTipText(Mensagens.getString("msg.pesq_no_conteudo"));
			chkPorParte.setToolTipText(Mensagens.getString("label.por_parte"));
			txtArquivo.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtArquivo.addActionListener(this);
			add(txtArquivo);
			add(chkPorParte);
			add(chkPsqConteudo);
			add(label);
		}

		protected void focusInputPesquisar() {
			txtArquivo.requestFocus();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtArquivo.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					split.contemConteudo(set, txtArquivo.getText(), chkPorParte.isSelected());
					Util.mensagem(NavegacaoContainer.this, getString(set));
				} else {
					pesquisa = split.getTree().getPesquisa(pesquisa, txtArquivo.getText(), chkPorParte.isSelected());
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
			if (formulario.excluirPagina(NavegacaoContainer.this)) {
				NavegacaoFormulario.criar(formulario, NavegacaoContainer.this);
			} else if (navegacaoDialogo != null) {
				navegacaoDialogo.excluirContainer();
				NavegacaoFormulario.criar(formulario, NavegacaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (navegacaoFormulario != null) {
				navegacaoFormulario.excluirContainer();
				formulario.adicionarPagina(NavegacaoContainer.this);
			} else if (navegacaoDialogo != null) {
				navegacaoDialogo.excluirContainer();
				formulario.adicionarPagina(NavegacaoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (navegacaoDialogo != null) {
				navegacaoDialogo.excluirContainer();
			}
			NavegacaoFormulario.criar(formulario);
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
			split.inicializar(NavegacaoContainer.this);
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
		return NavegacaoFabrica.class;
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
				return NavegacaoMensagens.getString(NavegacaoConstantes.LABEL_NAVEGACAO_MIN);
			}

			@Override
			public String getTitulo() {
				return NavegacaoMensagens.getString(NavegacaoConstantes.LABEL_NAVEGACAO);
			}

			@Override
			public String getHint() {
				return NavegacaoMensagens.getString(NavegacaoConstantes.LABEL_NAVEGACAO);
			}

			@Override
			public Icon getIcone() {
				return Icones.URL;
			}
		};
	}
}