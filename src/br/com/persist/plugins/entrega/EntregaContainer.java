package br.com.persist.plugins.entrega;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.CLONAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.NOVO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginFichario;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.FicharioPesquisa;
import br.com.persist.componente.Janela;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class EntregaContainer extends AbstratoContainer implements PluginFichario {
	private static final File file = new File(EntregaConstantes.ENTREGAS);
	private final EntregaFichario fichario = new EntregaFichario();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private EntregaFormulario entregaFormulario;
	private EntregaDialogo entregaDialogo;

	public EntregaContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo, idPagina);
	}

	public EntregaDialogo getEntregaDialogo() {
		return entregaDialogo;
	}

	public void setEntregaDialogo(EntregaDialogo entregaDialogo) {
		this.entregaDialogo = entregaDialogo;
		if (entregaDialogo != null) {
			entregaFormulario = null;
		}
	}

	public EntregaFormulario getEntregaFormulario() {
		return entregaFormulario;
	}

	public void setEntregaFormulario(EntregaFormulario entregaFormulario) {
		this.entregaFormulario = entregaFormulario;
		if (entregaFormulario != null) {
			entregaDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
		fichario.setListener(e -> toolbar.focusInputPesquisar());
	}

	public String getConteudo() {
		EntregaPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		EntregaPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return null;
	}

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	static boolean ehArquivoReservado(String nome) {
		return EntregaConstantes.IGNORADOS.equalsIgnoreCase(nome);
	}

	private void abrir(String conteudo, String idPagina) {
		ArquivoUtil.lerArquivo(EntregaConstantes.ENTREGAS, new File(file, EntregaConstantes.IGNORADOS));
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				files = ArquivoUtil.ordenar(files);
				List<EntregaPagina> ordenados = new ArrayList<>();
				for (File f : files) {
					if ((ehArquivoReservado(f.getName()) && !EntregaPreferencia.isExibirArqIgnorados())
							|| ArquivoUtil.contem(EntregaConstantes.ENTREGAS, f.getName())) {
						continue;
					}
					ordenados.add(new EntregaPagina(f));
				}
				for (EntregaPagina pagina : ordenados) {
					fichario.adicionarPagina(pagina);
				}
			}
		}
		fichario.setConteudo(conteudo, idPagina);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action excluirAtivoAcao = actionIconExcluir();
		private static final long serialVersionUID = 1L;
		private transient FicharioPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, SALVAR);
			addButton(excluirAtivoAcao);
			add(txtPesquisa);
			add(chkPorParte);
			chkPsqConteudo.setTag("FICHARIO");
			add(chkPsqConteudo);
			add(label);
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
			txtPesquisa.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					fichario.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected());
					Util.mensagem(EntregaContainer.this, Util.getString(set));
				} else {
					pesquisa = fichario.getPesquisa(pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(EntregaContainer.this)) {
				EntregaFormulario.criar(formulario, EntregaContainer.this);
			} else if (entregaDialogo != null) {
				entregaDialogo.excluirContainer();
				EntregaFormulario.criar(formulario, EntregaContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (entregaFormulario != null) {
				entregaFormulario.excluirContainer();
				formulario.adicionarPagina(EntregaContainer.this);
			} else if (entregaDialogo != null) {
				entregaDialogo.excluirContainer();
				formulario.adicionarPagina(EntregaContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (entregaDialogo != null) {
				entregaDialogo.excluirContainer();
			}
			EntregaFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (entregaDialogo != null) {
				entregaDialogo.excluirContainer();
			}
			EntregaFormulario.criar(formulario, null, null);
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
			Object resp = Util.getValorInputDialog(EntregaContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.isEmpty(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			if (ehArquivoReservado(nome)) {
				Util.mensagem(EntregaContainer.this, Mensagens.getString("label.indentificador_reservado"));
				return;
			}

			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(EntregaContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					EntregaPagina pagina = new EntregaPagina(f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(EntregaConstantes.PAINEL_ENTREGA, ex, EntregaContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			EntregaPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(EntregaPagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			EntregaPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(EntregaContainer.this,
					EntregaMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
				int indice = fichario.getSelectedIndex();
				ativa.excluir();
				fichario.remove(indice);
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
		EntregaPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return EntregaFabrica.class;
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
				return EntregaMensagens.getString(EntregaConstantes.LABEL_ENTREGA_MIN);
			}

			@Override
			public String getTitulo() {
				return EntregaMensagens.getString(EntregaConstantes.LABEL_ENTREGA);
			}

			@Override
			public String getHint() {
				return EntregaMensagens.getString(EntregaConstantes.LABEL_ENTREGA);
			}

			@Override
			public Icon getIcone() {
				return Icones.BOLA_VERDE;
			}
		};
	}
}