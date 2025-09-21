package br.com.persist.plugins.objeto.config;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoMensagens;
import br.com.persist.plugins.objeto.ObjetoUtil;
import br.com.persist.plugins.objeto.vinculo.Filtro;
import br.com.persist.plugins.objeto.vinculo.Instrucao;

public class MiscelaniaContainer extends Panel {
	private final TextEditor textEditor = new TextEditor();
	private static final long serialVersionUID = 1L;
	private transient MiscelaniaListener listener;
	private final Toolbar toolbar = new Toolbar();
	public static final String ITENS = "itens";
	private final transient Objeto objeto;
	private final Tipo tipo;

	public MiscelaniaContainer(Janela janela, Objeto objeto, Tipo tipo) throws XMLException {
		this.objeto = objeto;
		montarLayout(tipo);
		toolbar.ini(janela);
		this.tipo = tipo;
	}

	private void montarLayout(Tipo tipo) throws XMLException {
		ScrollPane scrollPane = new ScrollPane(textEditor);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
		add(BorderLayout.NORTH, toolbar);
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		StringBuilder builder = new StringBuilder();
		if (Tipo.CHAVEAMENTO.equals(tipo)) {
			chave(builder);
		} else if (Tipo.MAPEAMENTO.equals(tipo)) {
			mapa(builder);
		} else if (Tipo.SEQUENCIA.equals(tipo)) {
			seque(builder);
		} else if (Tipo.COMPLEMENTO.equals(tipo)) {
			compl(builder);
		} else if (Tipo.CLASSBIBLIO.equals(tipo)) {
			classBiblio(builder);
		} else if (Tipo.DESTACAVEIS.equals(tipo)) {
			destac(builder);
		} else if (Tipo.LARCONTEUDO.equals(tipo)) {
			larConteudo(builder);
		} else if (Tipo.INSTRUCAO.equals(tipo)) {
			instrucao(builder);
		} else if (Tipo.FILTRO.equals(tipo)) {
			filtro(builder);
		}
		textEditor.setText(builder.toString().trim());
	}

	private void instrucao(StringBuilder builder) throws XMLException {
		StringWriter sw = new StringWriter();
		XMLUtil util = new XMLUtil(sw);
		util.prologo();
		util.abrirTag2(ITENS);
		boolean ql = false;
		for (Instrucao item : objeto.getInstrucoes()) {
			item.salvar(util, ql);
			ql = true;
		}
		util.finalizarTag(ITENS);
		util.close();
		builder.append(sw.toString());
	}

	private void filtro(StringBuilder builder) throws XMLException {
		StringWriter sw = new StringWriter();
		XMLUtil util = new XMLUtil(sw);
		util.prologo();
		util.abrirTag2(ITENS);
		boolean ql = false;
		for (Filtro item : objeto.getFiltros()) {
			item.salvar(util, ql);
			ql = true;
		}
		util.finalizarTag(ITENS);
		util.close();
		builder.append(sw.toString());
	}

	private void chave(StringBuilder builder) {
		Map<String, List<String>> campoNomes = ObjetoUtil
				.criarMapaCampoNomes(!Util.isEmpty(objeto.getChaveamento()) ? objeto.getChaveamento()
						: ObjetoMensagens.getString("hint.chaveamento"));
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
		Map<String, String> campoChave = ObjetoUtil
				.criarMapaCampoChave(!Util.isEmpty(objeto.getMapeamento()) ? objeto.getMapeamento()
						: ObjetoMensagens.getString("hint.mapeamento"));
		int i = 0;
		for (Map.Entry<String, String> entry : campoChave.entrySet()) {
			String chave = entry.getKey();
			String valor = entry.getValue();
			builder.append(chave + "=" + valor);
			if (i + 1 < campoChave.size()) {
				builder.append(Constantes.QL);
				builder.append(";");
			}
			builder.append(Constantes.QL);
			i++;
		}
	}

	private void seque(StringBuilder builder) {
		String[] sequencias = !Util.isEmpty(objeto.getSequencias()) ? objeto.getSequencias().split(";")
				: ObjetoMensagens.getString("hint.sequencias").split(";");
		for (int i = 0; i < sequencias.length; i++) {
			if (i > 0) {
				builder.append(Constantes.QL);
				builder.append(";");
			}
			builder.append(Constantes.QL);
			builder.append(sequencias[i]);
		}
	}

	private void classBiblio(StringBuilder builder) {
		builder.append(objeto.getClassBiblio());
	}

	private void compl(StringBuilder builder) {
		builder.append(objeto.getComplemento());
	}

	private void destac(StringBuilder builder) {
		builder.append(objeto.getDestacaveis());
	}

	private void larConteudo(StringBuilder builder) {
		builder.append(objeto.getLarConteudo());
	}

	public enum Tipo {
		COMPLEMENTO, CLASSBIBLIO, DESTACAVEIS, LARCONTEUDO, CHAVEAMENTO, MAPEAMENTO, SEQUENCIA, INSTRUCAO, FILTRO
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

	public MiscelaniaListener getListener() {
		return listener;
	}

	public void setListener(MiscelaniaListener listener) {
		this.listener = listener;
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR, APLICAR, COPIAR, COLAR);
		}

		@Override
		protected void aplicar() {
			try {
				if (Tipo.CHAVEAMENTO.equals(tipo)) {
					objeto.setChaveamento(Util.normalizar(textEditor.getText(), false));
				} else if (Tipo.MAPEAMENTO.equals(tipo)) {
					objeto.setMapeamento(Util.normalizar(textEditor.getText(), false));
				} else if (Tipo.SEQUENCIA.equals(tipo)) {
					objeto.setSequencias(Util.normalizar(textEditor.getText(), false));
				} else if (Tipo.COMPLEMENTO.equals(tipo)) {
					objeto.setComplemento(Util.normalizar(textEditor.getText(), false));
				} else if (Tipo.CLASSBIBLIO.equals(tipo)) {
					objeto.setClassBiblio(Util.normalizar(textEditor.getText(), false));
				} else if (Tipo.DESTACAVEIS.equals(tipo)) {
					objeto.setDestacaveis(Util.normalizar(textEditor.getText(), false));
				} else if (Tipo.LARCONTEUDO.equals(tipo)) {
					objeto.setLarConteudo(Util.normalizar(textEditor.getText(), false));
				} else if (Tipo.INSTRUCAO.equals(tipo) && listener != null) {
					listener.aplicar(textEditor.getText());
				} else if (Tipo.FILTRO.equals(tipo) && listener != null) {
					listener.aplicar(textEditor.getText());
				}
				fechar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("CONFIG OBJETO", ex, MiscelaniaContainer.this);
			}
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textEditor);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textEditor.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textEditor, numeros, letras);
		}

		@Override
		protected void limpar() {
			textEditor.limpar();
		}
	}
}