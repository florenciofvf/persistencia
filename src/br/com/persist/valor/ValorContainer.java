package br.com.persist.valor;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import br.com.persist.busca_auto.GrupoBuscaAutoUtil;
import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextArea;
import br.com.persist.icone.Icones;
import br.com.persist.link_auto.GrupoLinkAuto;
import br.com.persist.link_auto.LinkAutoUtil;
import br.com.persist.objeto.Objeto;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class ValorContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final transient Objeto objeto;
	private final Tipo tipo;

	public ValorContainer(IJanela janela, Objeto objeto, Tipo tipo) {
		this.objeto = objeto;
		toolbar.ini(janela);
		this.tipo = tipo;
		montarLayout(objeto);
	}

	private void montarLayout(Objeto objeto) {
		add(BorderLayout.CENTER, textArea);
		add(BorderLayout.NORTH, toolbar);

		StringBuilder builder = new StringBuilder();

		if (Tipo.CHAVE.equals(tipo)) {
			chave(builder);

		} else if (Tipo.MAPA.equals(tipo)) {
			mapa(builder);

		} else if (Tipo.BUSCA.equals(tipo)) {
			buscaAuto(objeto, builder);

		} else if (Tipo.LINK.equals(tipo)) {
			linkAuto(objeto, builder);
		}

		textArea.setText(builder.toString().trim());
	}

	private void chave(StringBuilder builder) {
		Map<String, List<String>> campoNomes = Util.criarMapaCampoNomes(!Util.estaVazio(objeto.getChaveamento())
				? objeto.getChaveamento() : Mensagens.getString("hint.chaveamento"));
		int i = 0;

		for (Map.Entry<String, List<String>> entry : campoNomes.entrySet()) {
			String chave = entry.getKey();
			List<String> nomes = entry.getValue();
			builder.append(campoDetalhe(chave, nomes));

			if (i + 1 < campoNomes.size()) {
				builder.append(";");
			}

			builder.append(Constantes.QL);
			i++;
		}
	}

	private void mapa(StringBuilder builder) {
		Map<String, String> campoChave = Util.criarMapaCampoChave(!Util.estaVazio(objeto.getMapeamento())
				? objeto.getMapeamento() : Mensagens.getString("hint.mapeamento"));
		int i = 0;

		for (Map.Entry<String, String> entry : campoChave.entrySet()) {
			String chave = entry.getKey();
			String valor = entry.getValue();
			builder.append(chave + "=" + valor);

			if (i + 1 < campoChave.size()) {
				builder.append(";");
			}

			builder.append(Constantes.QL);
			i++;
		}
	}

	private void buscaAuto(Objeto objeto, StringBuilder builder) {
		List<GrupoBuscaAuto> listaGrupo = GrupoBuscaAutoUtil.listaGrupoBuscaAuto(objeto,
				!Util.estaVazio(objeto.getBuscaAutomatica()) ? objeto.getBuscaAutomatica()
						: Mensagens.getString("hint.buscaAuto"));

		for (int i = 0; i < listaGrupo.size(); i++) {
			GrupoBuscaAuto grupo = listaGrupo.get(i);
			builder.append(grupo.getDetalhe());

			if (i + 1 < listaGrupo.size()) {
				builder.append(";");
			}

			builder.append(Constantes.QL);
		}
	}

	private void linkAuto(Objeto objeto, StringBuilder builder) {
		List<GrupoLinkAuto> listaLink = LinkAutoUtil.listaGrupoLinkAuto(objeto,
				!Util.estaVazio(objeto.getLinkAutomatico()) ? objeto.getLinkAutomatico()
						: Mensagens.getString("hint.linkAuto"));

		for (int i = 0; i < listaLink.size(); i++) {
			GrupoLinkAuto link = listaLink.get(i);
			builder.append(link.getDetalhe());

			if (i + 1 < listaLink.size()) {
				builder.append(";");
			}

			builder.append(Constantes.QL);
		}
	}

	public enum Tipo {
		CHAVE, BUSCA, LINK, MAPA
	}

	private String campoDetalhe(String chave, List<String> lista) {
		StringBuilder sb = new StringBuilder(chave + "=" + Constantes.QL);

		for (int i = 0; i < lista.size(); i++) {
			String string = lista.get(i);
			sb.append(Constantes.TAB + string);

			if (i + 1 < lista.size()) {
				sb.append(",");
			}

			sb.append(Constantes.QL);
		}

		return sb.toString();
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action sucessoAcao = Action.actionIcon("label.aplicar", Icones.SUCESSO);

		public void ini(IJanela janela) {
			super.ini(janela, true, false);

			addButton(sucessoAcao);
			configCopiar1Acao(true);

			sucessoAcao.setActionListener(e -> {
				try {
					if (Tipo.BUSCA.equals(tipo)) {
						String string = Util.normalizar(textArea.getText(), false);
						GrupoBuscaAutoUtil.listaGrupoBuscaAuto(objeto, string);
						objeto.setBuscaAutomatica(string);

					} else if (Tipo.LINK.equals(tipo)) {
						String string = Util.normalizar(textArea.getText(), false);
						LinkAutoUtil.listaGrupoLinkAuto(objeto, string);
						objeto.setLinkAutomatico(string);

					} else if (Tipo.CHAVE.equals(tipo)) {
						objeto.setChaveamento(Util.normalizar(textArea.getText(), false));

					} else if (Tipo.MAPA.equals(tipo)) {
						objeto.setMapeamento(Util.normalizar(textArea.getText(), false));
					}

					fechar();
				} catch (Exception ex) {
					Util.stackTraceAndMessage("CONFIG OBJETO", ex, ValorContainer.this);
				}
			});
		}

		@Override
		protected void copiar1() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiar1Mensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar1() {
			Util.getContentTransfered(textArea.getTextAreaInner());
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}
	}
}