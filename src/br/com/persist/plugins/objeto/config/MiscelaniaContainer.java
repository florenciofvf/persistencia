package br.com.persist.plugins.objeto.config;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextArea;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoUtil;
import br.com.persist.plugins.objeto.auto.GrupoLinkAuto;
import br.com.persist.plugins.objeto.auto.LinkAutoUtil;

public class MiscelaniaContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final transient Objeto objeto;
	private final Tipo tipo;

	public MiscelaniaContainer(Janela janela, Objeto objeto, Tipo tipo) {
		this.objeto = objeto;
		montarLayout(tipo);
		toolbar.ini(janela);
		this.tipo = tipo;
	}

	private void montarLayout(Tipo tipo) {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);

		StringBuilder builder = new StringBuilder();

		if (Tipo.CHAVE_SEQUENCIA.equals(tipo)) {
			chave(builder);

		} else if (Tipo.MAPEAMENTO.equals(tipo)) {
			mapa(builder);

		} else if (Tipo.LINK_AUTO.equals(tipo)) {
			linkAuto(builder);
		}

		textArea.setText(builder.toString().trim());
	}

	private void chave(StringBuilder builder) {
		Map<String, List<String>> campoNomes = ObjetoUtil.criarMapaCampoNomes(!Util.estaVazio(objeto.getChaveamento())
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
		Map<String, String> campoChave = ObjetoUtil.criarMapaCampoChave(!Util.estaVazio(objeto.getMapeamento())
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

	private void linkAuto(StringBuilder builder) {
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
		CHAVE_SEQUENCIA, LINK_AUTO, MAPEAMENTO
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

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR, APLICAR, COPIAR, COLAR);
		}

		@Override
		protected void aplicar() {
			try {
				if (Tipo.LINK_AUTO.equals(tipo)) {
					String string = Util.normalizar(textArea.getText(), false);
					LinkAutoUtil.listaGrupoLinkAuto(objeto, string);
					objeto.setLinkAutomatico(string);

				} else if (Tipo.CHAVE_SEQUENCIA.equals(tipo)) {
					objeto.setChaveamento(Util.normalizar(textArea.getText(), false));

				} else if (Tipo.MAPEAMENTO.equals(tipo)) {
					objeto.setMapeamento(Util.normalizar(textArea.getText(), false));
				}

				fechar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("CONFIG OBJETO", ex, MiscelaniaContainer.this);
			}
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar() {
			Util.getContentTransfered(textArea.getTextAreaInner());
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}
	}
}