package br.com.persist.plugins.instrucao;

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

public class InstrucaoContainer extends AbstratoContainer {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private InstrucaoFormulario instrucaoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private InstrucaoDialogo instrucaoDialogo;
	private final InstrucaoSplit split;

	public InstrucaoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		split = new InstrucaoSplit();
		split.inicializar(this);
		toolbar.ini(janela);
		montarLayout();
	}

	public InstrucaoDialogo getInstrucaoDialogo() {
		return instrucaoDialogo;
	}

	public void setInstrucaoDialogo(InstrucaoDialogo instrucaoDialogo) {
		this.instrucaoDialogo = instrucaoDialogo;
		if (instrucaoDialogo != null) {
			instrucaoFormulario = null;
		}
	}

	public InstrucaoFormulario getInstrucaoFormulario() {
		return instrucaoFormulario;
	}

	public void setInstrucaoFormulario(InstrucaoFormulario instrucaoFormulario) {
		this.instrucaoFormulario = instrucaoFormulario;
		if (instrucaoFormulario != null) {
			instrucaoDialogo = null;
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

		@Override
		protected void focusInputPesquisar() {
			txtArquivo.requestFocus();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtArquivo.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					split.contemConteudo(set, txtArquivo.getText(), chkPorParte.isSelected());
					Util.mensagem(InstrucaoContainer.this, getString(set));
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
			if (formulario.excluirPagina(InstrucaoContainer.this)) {
				InstrucaoFormulario.criar(formulario, InstrucaoContainer.this);
			} else if (instrucaoDialogo != null) {
				instrucaoDialogo.excluirContainer();
				InstrucaoFormulario.criar(formulario, InstrucaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (instrucaoFormulario != null) {
				instrucaoFormulario.excluirContainer();
				formulario.adicionarPagina(InstrucaoContainer.this);
			} else if (instrucaoDialogo != null) {
				instrucaoDialogo.excluirContainer();
				formulario.adicionarPagina(InstrucaoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (instrucaoDialogo != null) {
				instrucaoDialogo.excluirContainer();
			}
			InstrucaoFormulario.criar(formulario);
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
			split.inicializar(InstrucaoContainer.this);
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
		return InstrucaoFabrica.class;
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
				return InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO_MIN);
			}

			@Override
			public String getTitulo() {
				return InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO);
			}

			@Override
			public String getHint() {
				return InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO);
			}

			@Override
			public Icon getIcone() {
				return Icones.FRAGMENTO;
			}
		};
	}
}