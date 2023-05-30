package br.com.persist.plugins.variaveis;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.EXCLUIR;
import static br.com.persist.componente.BarraButtonEnum.NOVO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.CellRenderer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class VariavelContainer extends AbstratoContainer {
	private final VariavelModelo variavelModelo = new VariavelModelo();
	private final JTable tabela = new JTable(variavelModelo);
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private VariavelFormulario variavelFormulario;
	private final Toolbar toolbar = new Toolbar();
	private VariavelDialogo variavelDialogo;

	public VariavelContainer(Janela janela, Formulario formulario, VariavelColetor coletor) {
		super(formulario);
		toolbar.ini(janela, coletor);
		montarLayout();
		configurar();
	}

	public VariavelDialogo getVariavelDialogo() {
		return variavelDialogo;
	}

	public void setVariavelDialogo(VariavelDialogo variavelDialogo) {
		this.variavelDialogo = variavelDialogo;
		if (variavelDialogo != null) {
			variavelFormulario = null;
		}
	}

	public VariavelFormulario getVariavelFormulario() {
		return variavelFormulario;
	}

	public void setVariavelFormulario(VariavelFormulario variavelFormulario) {
		this.variavelFormulario = variavelFormulario;
		if (variavelFormulario != null) {
			variavelDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer());
		tabela.getColumnModel().getColumn(1).setCellEditor(new VariavelEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.baixar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private transient VariavelColetor coletor;

		public void ini(Janela janela, VariavelColetor coletor) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR,
					EXCLUIR, COPIAR, APLICAR);
			setColetor(coletor);
		}

		private void setColetor(VariavelColetor coletor) {
			aplicarAcao.setEnabled(coletor != null);
			this.coletor = coletor;
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(VariavelContainer.this)) {
				VariavelFormulario.criar(formulario, VariavelContainer.this);
			} else if (variavelDialogo != null) {
				variavelDialogo.excluirContainer();
				VariavelFormulario.criar(formulario, VariavelContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (variavelFormulario != null) {
				variavelFormulario.excluirContainer();
				formulario.adicionarPagina(VariavelContainer.this);
			} else if (variavelDialogo != null) {
				variavelDialogo.excluirContainer();
				formulario.adicionarPagina(VariavelContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (variavelDialogo != null) {
				variavelDialogo.excluirContainer();
			}
			VariavelFormulario.criar(formulario);
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
		protected void novo() {
			String nome = getValor(Constantes.VAZIO);
			if (nome != null) {
				adicionar(new Variavel(nome));
			}
		}

		private void adicionar(Variavel var) {
			if (VariavelProvedor.contem(var)) {
				Util.mensagem(VariavelContainer.this,
						Mensagens.getString("label.indentificador_ja_existente") + " " + var.getNome());
				return;
			}
			VariavelProvedor.adicionar(var);
			variavelModelo.fireTableDataChanged();
			ajustarTabela();
		}

		private String getValor(String padrao) {
			Object resp = Util.getValorInputDialog(VariavelContainer.this, "label.id",
					VariavelMensagens.getString("label.nome_variavel"), padrao);
			if (resp == null || Util.estaVazio(resp.toString())) {
				return null;
			}
			return resp.toString();
		}

		@Override
		protected void baixar() {
			VariavelProvedor.inicializar();
			variavelModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void salvar() {
			try {
				VariavelProvedor.salvar();
				salvoMensagem();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}

		@Override
		protected void copiar() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null) {
				for (int i : linhas) {
					Variavel v = VariavelProvedor.getVariavel(i);
					String nome = getValor(v.getNome());
					if (nome != null) {
						adicionar(v.clonar(nome));
					}
				}
			}
		}

		@Override
		protected void excluir() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0 && Util.confirmaExclusao(VariavelContainer.this, false)) {
				VariavelProvedor.excluir(linhas);
				variavelModelo.fireTableDataChanged();
			}
		}

		@Override
		protected void aplicar() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0) {
				aplicarLista(linhas);
			} else {
				mensagem();
			}
		}

		private void aplicarLista(int[] linhas) {
			List<Variavel> lista = new ArrayList<>();
			for (int i : linhas) {
				lista.add(VariavelProvedor.getVariavel(i));
			}
			coletor.setLista(lista);
			janela.fechar();
		}

		private void mensagem() {
			Util.mensagem(VariavelContainer.this, Mensagens.getString("msg.nenhum_registro_selecionado"));
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
		ajustarTabela();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
		ajustarTabela();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.dialogOpenedHandler(dialog);
		ajustarTabela();
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return VariavelFabrica.class;
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
				return VariavelMensagens.getString(VariavelConstantes.LABEL_VARIAVEIS_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_VARIAVEIS);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_VARIAVEIS);
			}

			@Override
			public Icon getIcone() {
				return Icones.VAR;
			}
		};
	}

	private void ajustarTabela() {
		Util.ajustar(tabela, getGraphics());
	}
}