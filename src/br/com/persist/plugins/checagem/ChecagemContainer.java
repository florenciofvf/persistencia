package br.com.persist.plugins.checagem;

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

public class ChecagemContainer extends AbstratoContainer {
	private static final File file = new File(ChecagemConstantes.CHECAGENS);
	private final ChecagemFichario fichario = new ChecagemFichario();
	private static final long serialVersionUID = 1L;
	private ChecagemFormulario checagemFormulario;
	private final Toolbar toolbar = new Toolbar();
	private ChecagemDialogo checagemDialogo;

	public ChecagemContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo, idPagina);
	}

	public ChecagemDialogo getChecagemDialogo() {
		return checagemDialogo;
	}

	public void setChecagemDialogo(ChecagemDialogo checagemDialogo) {
		this.checagemDialogo = checagemDialogo;
		if (checagemDialogo != null) {
			checagemFormulario = null;
		}
	}

	public ChecagemFormulario getChecagemFormulario() {
		return checagemFormulario;
	}

	public void setChecagemFormulario(ChecagemFormulario checagemFormulario) {
		this.checagemFormulario = checagemFormulario;
		if (checagemFormulario != null) {
			checagemDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
	}

	public String getConteudo() {
		ChecagemPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		ChecagemPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return null;
	}

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	static boolean ehArquivoReservadoSentencas(String nome) {
		return ChecagemConstantes.CHECAGENS.equalsIgnoreCase(nome);
	}

	static boolean ehArquivoReservadoIgnorados(String nome) {
		return ChecagemConstantes.IGNORADOS.equalsIgnoreCase(nome);
	}

	private boolean vetarAdicionarPagina(File file) {
		return (ehArquivoReservadoSentencas(file.getName()) && !ChecagemPreferencia.isExibirArqSentencas())
				|| (ehArquivoReservadoIgnorados(file.getName()) && !ChecagemPreferencia.isExibirArqIgnorados());
	}

	private void abrir(String conteudo, String idPagina) {
		ArquivoUtil.lerArquivo(ChecagemConstantes.CHECAGENS, new File(file, ChecagemConstantes.IGNORADOS));
		ChecagemUtil.initModulos();
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				List<ChecagemPagina> ordenados = new ArrayList<>();
				for (File f : files) {
					if (vetarAdicionarPagina(f) || ArquivoUtil.contem(ChecagemConstantes.CHECAGENS, f.getName())) {
						continue;
					}
					ordenados.add(new ChecagemPagina(f));
				}
				Collections.sort(ordenados, (a1, a2) -> a1.getNome().compareTo(a2.getNome()));
				for (ChecagemPagina pagina : ordenados) {
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

	static Action actionMenu(String chave) {
		return Action.acaoMenu(ChecagemMensagens.getString(chave), null);
	}

	static Action acaoIcon(String chave, Icon icon) {
		return Action.acaoIcon(ChecagemMensagens.getString(chave), icon);
	}

	static Action acaoIcon(String chave) {
		return acaoIcon(chave, null);
	}

	private class Toolbar extends BarraButton {
		private Action excluirAtivoAcao = actionIcon("label.excluir", Icones.EXCLUIR);
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, SALVAR);
			addButton(excluirAtivoAcao);
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(ChecagemContainer.this)) {
				ChecagemFormulario.criar(formulario, ChecagemContainer.this);
			} else if (checagemDialogo != null) {
				checagemDialogo.excluirContainer();
				ChecagemFormulario.criar(formulario, ChecagemContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (checagemFormulario != null) {
				checagemFormulario.excluirContainer();
				formulario.adicionarPagina(ChecagemContainer.this);
			} else if (checagemDialogo != null) {
				checagemDialogo.excluirContainer();
				formulario.adicionarPagina(ChecagemContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (checagemDialogo != null) {
				checagemDialogo.excluirContainer();
			}
			ChecagemFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (checagemDialogo != null) {
				checagemDialogo.excluirContainer();
			}
			ChecagemFormulario.criar(formulario, null, null);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		@Override
		protected void novo() {
			Object resp = Util.getValorInputDialog(ChecagemContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			if (ehArquivoReservadoSentencas(nome)) {
				Util.mensagem(ChecagemContainer.this, Mensagens.getString("label.indentificador_reservado"));
				return;
			}

			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(ChecagemContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					ChecagemPagina pagina = new ChecagemPagina(f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(ChecagemConstantes.PAINEL_CHECAGEM, ex, ChecagemContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			ChecagemPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(ChecagemPagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			ChecagemPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(ChecagemContainer.this,
					ChecagemMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
				if (ativa.ehArquivoReservadoSentencas() || ativa.ehArquivoReservadoIgnorados()) {
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
		ChecagemPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return ChecagemFabrica.class;
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
				return ChecagemMensagens.getString(ChecagemConstantes.LABEL_CHECAGEM_MIN);
			}

			@Override
			public String getTitulo() {
				return ChecagemMensagens.getString(ChecagemConstantes.LABEL_CHECAGEM);
			}

			@Override
			public String getHint() {
				return ChecagemMensagens.getString(ChecagemConstantes.LABEL_CHECAGEM);
			}

			@Override
			public Icon getIcone() {
				return Icones.SUCESSO;
			}
		};
	}
}