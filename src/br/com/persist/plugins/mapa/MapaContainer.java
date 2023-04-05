package br.com.persist.plugins.mapa;

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

public class MapaContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private static final File file = new File(MapaConstantes.MAPAS);
	private final MapaFichario fichario = new MapaFichario();
	private final Toolbar toolbar = new Toolbar();
	private MapaFormulario mapaFormulario;
	private MapaDialogo mapaDialogo;

	public MapaContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo, idPagina);
	}

	public MapaDialogo getMapaDialogo() {
		return mapaDialogo;
	}

	public void setMapaDialogo(MapaDialogo mapaDialogo) {
		this.mapaDialogo = mapaDialogo;
		if (mapaDialogo != null) {
			mapaFormulario = null;
		}
	}

	public MapaFormulario getMapaFormulario() {
		return mapaFormulario;
	}

	public void setMapaFormulario(MapaFormulario mapaFormulario) {
		this.mapaFormulario = mapaFormulario;
		if (mapaFormulario != null) {
			mapaDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
	}

	public String getConteudo() {
		MapaPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		MapaPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return null;
	}

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	static boolean ehArquivoReservado(String nome) {
		return MapaConstantes.IGNORADOS.equalsIgnoreCase(nome);
	}

	private void abrir(String conteudo, String idPagina) {
		ArquivoUtil.lerArquivo(MapaConstantes.MAPAS, new File(file, MapaConstantes.IGNORADOS));
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				List<MapaPagina> ordenadas = new ArrayList<>();
				for (File f : files) {
					if ((ehArquivoReservado(f.getName()) && !MapaPreferencia.isExibirArqInvisivel())
							|| !ArquivoUtil.contem(MapaConstantes.MAPAS, f.getName())) {
						continue;
					}
					ordenadas.add(new MapaPagina(f));
				}
				Collections.sort(ordenadas, (a1, a2) -> a1.getNome().compareTo(a2.getNome()));
				for (MapaPagina pagina : ordenadas) {
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
		return Action.acaoMenu(MapaMensagens.getString(chave), null);
	}

	static Action actionIcon(String chave, Icon icon) {
		return Action.acaoIcon(MapaMensagens.getString(chave), icon);
	}

	static Action actionIcon(String chave) {
		return actionIcon(chave, null);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action excluirAtivoAcao = Action.actionIcon("label.excluir", Icones.EXCLUIR);

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, SALVAR);
			addButton(excluirAtivoAcao);
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(MapaContainer.this)) {
				MapaFormulario.criar(formulario, MapaContainer.this);
			} else if (mapaDialogo != null) {
				mapaDialogo.excluirContainer();
				MapaFormulario.criar(formulario, MapaContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (mapaFormulario != null) {
				mapaFormulario.excluirContainer();
				formulario.adicionarPagina(MapaContainer.this);
			} else if (mapaDialogo != null) {
				mapaDialogo.excluirContainer();
				formulario.adicionarPagina(MapaContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (mapaDialogo != null) {
				mapaDialogo.excluirContainer();
			}
			MapaFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (mapaDialogo != null) {
				mapaDialogo.excluirContainer();
			}
			MapaFormulario.criar(formulario, null, null);
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
			Object resp = Util.getValorInputDialog(MapaContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			if (ehArquivoReservado(nome)) {
				Util.mensagem(MapaContainer.this, Mensagens.getString("label.indentificador_reservado"));
				return;
			}

			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(MapaContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					MapaPagina pagina = new MapaPagina(f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(MapaConstantes.PAINEL_MAPA, ex, MapaContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			MapaPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(MapaPagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			MapaPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(MapaContainer.this,
					MapaMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
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
		MapaPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return MapaFabrica.class;
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
				return MapaMensagens.getString(MapaConstantes.LABEL_MAPA_MIN);
			}

			@Override
			public String getTitulo() {
				return MapaMensagens.getString(MapaConstantes.LABEL_MAPA);
			}

			@Override
			public String getHint() {
				return MapaMensagens.getString(MapaConstantes.LABEL_MAPA);
			}

			@Override
			public Icon getIcone() {
				return Icones.BOLA_VERDE;
			}
		};
	}
}