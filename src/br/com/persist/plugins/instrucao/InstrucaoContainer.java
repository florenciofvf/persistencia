package br.com.persist.plugins.instrucao;

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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class InstrucaoContainer extends AbstratoContainer {
	private static final File file = new File(InstrucaoConstantes.INSTRUCAO);
	private final InstrucaoFichario fichario = new InstrucaoFichario();
	private InstrucaoFormulario instrucaoFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private InstrucaoDialogo instrucaoDialogo;

	public InstrucaoContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo, idPagina);
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
		add(BorderLayout.CENTER, fichario);
	}

	public String getConteudo() {
		InstrucaoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		InstrucaoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return null;
	}

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	static boolean ehArquivoReservadoIgnorados(String nome) {
		return InstrucaoConstantes.IGNORADOS.equalsIgnoreCase(nome);
	}

	private boolean vetarAdicionarPagina(File file) {
		return ehArquivoReservadoIgnorados(file.getName()) && !InstrucaoPreferencia.isExibirArqIgnorados();
	}

	private void abrir(String conteudo, String idPagina) {
		ArquivoUtil.lerArquivo(InstrucaoConstantes.INSTRUCAO, new File(file, InstrucaoConstantes.IGNORADOS));
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				List<InstrucaoPagina> ordenados = new ArrayList<>();
				for (File f : files) {
					if (vetarAdicionarPagina(f) || ArquivoUtil.contem(InstrucaoConstantes.INSTRUCAO, f.getName())) {
						continue;
					}
					ordenados.add(new InstrucaoPagina(f));
				}
				Collections.sort(ordenados, (a1, a2) -> a1.getNome().compareTo(a2.getNome()));
				for (InstrucaoPagina pagina : ordenados) {
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

	private class Toolbar extends BarraButton {
		private Action excluirAtivoAcao = actionIconExcluir();
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, SALVAR);
			addButton(excluirAtivoAcao);
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
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
		protected void clonarEmFormulario() {
			if (instrucaoDialogo != null) {
				instrucaoDialogo.excluirContainer();
			}
			InstrucaoFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (instrucaoDialogo != null) {
				instrucaoDialogo.excluirContainer();
			}
			InstrucaoFormulario.criar(formulario, null, null);
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
			Object resp = Util.getValorInputDialog(InstrucaoContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(InstrucaoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					InstrucaoPagina pagina = new InstrucaoPagina(f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(InstrucaoConstantes.PAINEL_INSTRUCAO, ex, InstrucaoContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			InstrucaoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(InstrucaoPagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			InstrucaoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(InstrucaoContainer.this,
					InstrucaoMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
				if (ativa.ehArquivoReservadoIgnorados()) {
					ativa.mensagemReservado();
				} else {
					int indice = fichario.getSelectedIndex();
					ativa.excluir();
					fichario.remove(indice);
				}
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
		InstrucaoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
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