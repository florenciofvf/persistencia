package br.com.persist.plugins.objeto;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JInternalFrame;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.internal.InternalFormulario;
import br.com.persist.plugins.objeto.vinculo.ArquivoVinculo;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;

public class ObjetoSuperficieUtil {
	private ObjetoSuperficieUtil() {
	}

	public static ArquivoVinculo criarArquivoVinculo(ObjetoSuperficie superficie) {
		return new ArquivoVinculo(superficie.arquivoVinculo);
	}

	public static void atualizarComplemento(ObjetoSuperficie superficie, Objeto objeto) {
		InternalFormulario interno = getInternalFormulario(superficie, objeto);
		if (interno != null) {
			interno.atualizarComplemento(objeto);
		}
	}

	public static void configuracaoDinamica(ObjetoSuperficie superficie, Component componente, Objeto objeto) {
		InternalFormulario interno = getInternalFormulario(superficie, objeto);
		if (interno == null) {
			Util.mensagem(componente, ObjetoMensagens.getString("msg.sem_form_seq_chave_mapa", objeto.getId()));
		} else {
			interno.configuracaoDinamica(objeto);
		}
	}

	public static void pontoOrigem(ObjetoSuperficie superficie, boolean b) {
		for (Relacao relacao : superficie.relacoes) {
			relacao.setPontoOrigem(b);
		}
		superficie.repaint();
	}

	public static void pontoDestino(ObjetoSuperficie superficie, boolean b) {
		for (Relacao relacao : superficie.relacoes) {
			relacao.setPontoDestino(b);
		}
		superficie.repaint();
	}

	public static boolean contem(ObjetoSuperficie superficie, Objeto obj) {
		return getIndice(superficie, obj) >= 0;
	}

	public static boolean contem(ObjetoSuperficie superficie, Relacao obj) {
		return getIndice(superficie, obj) >= 0;
	}

	public static void deselRelacoes(ObjetoSuperficie superficie) {
		for (Relacao relacao : superficie.relacoes) {
			relacao.setSelecionado(false);
		}
	}

	public static void mover(ObjetoSuperficie superficie, char c) {
		for (Objeto objeto : superficie.objetos) {
			if (objeto.isSelecionado()) {
				if (c == 'L') {
					objeto.x -= 5;
				} else if (c == 'R') {
					objeto.x += 5;
				} else if (c == 'U') {
					objeto.y -= 5;
				} else if (c == 'D') {
					objeto.y += 5;
				}
			}
		}
		superficie.repaint();
	}

	public static void excluirSemTabela(ObjetoSuperficie superficie) {
		boolean contem = false;
		for (Objeto objeto : superficie.objetos) {
			if (Util.estaVazio(objeto.getTabela())) {
				contem = true;
				break;
			}
		}
		if (!contem) {
			Util.mensagem(superficie, ObjetoMensagens.getString("msg.nenhum_objeto_sem_tabela"));
			return;
		}
		if (Util.confirmaExclusao(superficie, true)) {
			for (Objeto objeto : superficie.objetos) {
				if (Util.estaVazio(objeto.getTabela())) {
					superficie.excluir(objeto);
				}
			}
			for (Objeto objeto : superficie.objetos) {
				objeto.associado = null;
			}
		}
		superficie.repaint();
	}

	public static int preTotalRecente(ObjetoSuperficie superficie, Label label) {
		int total = 0;
		for (Objeto objeto : superficie.objetos) {
			if (!Util.estaVazio(objeto.getTabela())) {
				objeto.criarMemento();
				objeto.setCorFonte(ObjetoPreferencia.getCorAntesTotalRecente());
				total++;
			}
		}
		label.limpar();
		superficie.repaint();
		return total;
	}

	public static InternalFormulario getInternalFormulario(ObjetoSuperficie superficie, Objeto objeto) {
		for (JInternalFrame frame : superficie.getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (interno.ehObjeto(objeto) && interno.ehTabela(objeto)) {
					return interno;
				}
			}
		}
		return null;
	}

	public static void selecionarConexao(ObjetoSuperficie superficie, Conexao conexao) {
		for (JInternalFrame frame : superficie.getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				interno.selecionarConexao(conexao);
			}
		}
	}

	public static List<Objeto> objetosComTabela(ObjetoSuperficie superficie) {
		List<Objeto> resp = new ArrayList<>();
		for (Objeto objeto : superficie.objetos) {
			if (!Util.estaVazio(objeto.getTabela())) {
				resp.add(objeto);
			}
		}
		return resp;
	}

	public static List<String> getListaStringIds(ObjetoSuperficie superficie) {
		List<String> resp = new ArrayList<>();
		for (Objeto objeto : superficie.objetos) {
			resp.add(objeto.getId());
		}
		return resp;
	}

	public static List<Objeto> getSelecionados(ObjetoSuperficie superficie) {
		List<Objeto> resp = new ArrayList<>();
		for (Objeto objeto : superficie.objetos) {
			if (objeto.isSelecionado()) {
				resp.add(objeto);
			}
		}
		return resp;
	}

	public static void desativarObjetos(ObjetoSuperficie superficie) {
		for (Objeto objeto : superficie.objetos) {
			objeto.desativar();
		}
		for (Relacao relacao : superficie.relacoes) {
			relacao.desativar();
		}
		superficie.repaint();
	}

	public static void limparSelecao(ObjetoSuperficie superficie) {
		for (Objeto objeto : superficie.objetos) {
			objeto.setSelecionado(false);
		}
	}

	public static Objeto getPrimeiroObjetoSelecionado(ObjetoSuperficie superficie) {
		for (Objeto objeto : superficie.objetos) {
			if (objeto.isSelecionado()) {
				return objeto;
			}
		}
		return null;
	}

	public static Relacao getPrimeiroRelacaoSelecionado(ObjetoSuperficie superficie) {
		for (Relacao relacao : superficie.relacoes) {
			if (relacao.isSelecionado()) {
				return relacao;
			}
		}
		return null;
	}

	public static Relacao getRelacao(ObjetoSuperficie superficie, Objeto obj) {
		if (obj != null) {
			for (Relacao relacao : superficie.relacoes) {
				if (relacao.contem(obj)) {
					return relacao;
				}
			}
		}
		return null;
	}

	public static Set<String> getIdOrigens(ObjetoSuperficie superficie) {
		Set<String> set = new HashSet<>();
		for (Relacao relacao : superficie.relacoes) {
			set.add(relacao.getOrigem().getId());
		}
		return set;
	}

	public static List<Relacao> getRelacoes(ObjetoSuperficie superficie, Objeto obj) {
		List<Relacao> lista = new ArrayList<>();
		if (obj != null) {
			for (Relacao relacao : superficie.relacoes) {
				if (relacao.contem(obj)) {
					lista.add(relacao);
				}
			}
		}
		return lista;
	}

	public static Relacao getRelacao(ObjetoSuperficie superficie, Objeto obj1, Objeto obj2) {
		if (obj1 != null && obj2 != null) {
			Relacao temp = new Relacao(obj1, obj2);
			for (Relacao relacao : superficie.relacoes) {
				if (relacao.equals(temp)) {
					return relacao;
				}
			}
		}
		return null;
	}

	public static boolean contemId(ObjetoSuperficie superficie, Objeto obj) {
		for (int i = 0; i < superficie.objetos.length; i++) {
			Objeto objeto = superficie.objetos[i];
			if (objeto != obj && objeto.equalsId(obj)) {
				return true;
			}
		}
		return false;
	}

	public static Objeto getObjeto(ObjetoSuperficie superficie, String id) {
		for (int i = 0; i < superficie.objetos.length; i++) {
			if (superficie.objetos[i].getId().equals(id)) {
				return superficie.objetos[i];
			}
		}
		return null;
	}

	public static void desenharDesc(ObjetoSuperficie superficie, boolean b) {
		for (Relacao relacao : superficie.relacoes) {
			relacao.setDesenharDescricao(b);
		}
		superficie.repaint();
	}

	public static void selecaoGeral(ObjetoSuperficie superficie, boolean b) {
		for (Objeto objeto : superficie.objetos) {
			objeto.setSelecionado(b);
		}
		superficie.repaint();
	}

	public static void desenharIds(ObjetoSuperficie superficie, boolean b) {
		for (Objeto objeto : superficie.objetos) {
			objeto.setDesenharId(b);
		}
		superficie.repaint();
	}

	public static void transparente(ObjetoSuperficie superficie, boolean b) {
		for (Objeto objeto : superficie.objetos) {
			objeto.setTransparente(b);
		}
		superficie.repaint();
	}

	public static int getIndice(ObjetoSuperficie superficie, Objeto obj) {
		if (obj != null) {
			for (int i = 0; i < superficie.objetos.length; i++) {
				if (superficie.objetos[i] == obj || superficie.objetos[i].equals(obj)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int getIndice(ObjetoSuperficie superficie, Relacao obj) {
		if (obj != null) {
			for (int i = 0; i < superficie.relacoes.length; i++) {
				if (superficie.relacoes[i] == obj || superficie.relacoes[i].equals(obj)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static boolean contemObjetoComTabela(ObjetoSuperficie superficie, String nomeTabela) {
		for (Objeto objeto : superficie.objetos) {
			if (objeto.getTabela().equalsIgnoreCase(nomeTabela)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contemReferencia(ObjetoSuperficie superficie, Objeto objeto) {
		for (Objeto obj : superficie.objetos) {
			if (obj == objeto) {
				return true;
			}
		}
		return false;
	}

	public static void salvarVinculacao(ObjetoSuperficie superficie, Vinculacao vinculacao) {
		vinculacao.salvar(criarArquivoVinculo(superficie), superficie);
	}

	public static void processar(ObjetoSuperficie superficie) {
		for (Objeto objeto : superficie.objetos) {
			if (objeto.isSelecionado()) {
				objeto.setProcessar(true);
				objeto.ativar();
			}
		}
		for (Relacao relacao : superficie.relacoes) {
			if (relacao.isSelecionado()) {
				relacao.setProcessar(true);
				relacao.ativar();
			}
		}
		superficie.repaint();
	}

	public static void ativarObjetos(ObjetoSuperficie superficie, String string) {
		if (Util.estaVazio(string)) {
			return;
		}
		string = string.trim().toUpperCase();
		for (Objeto objeto : superficie.objetos) {
			if (objeto.getId().toUpperCase().indexOf(string) != -1
					|| objeto.getTabela().toUpperCase().indexOf(string) != -1) {
				objeto.setProcessar(true);
				objeto.ativar();
			}
		}
		superficie.repaint();
	}

	public static void desativar(ObjetoSuperficie superficie) {
		for (Objeto objeto : superficie.objetos) {
			if (objeto.isSelecionado()) {
				objeto.desativar();
			}
		}
		for (Relacao relacao : superficie.relacoes) {
			if (relacao.isSelecionado()) {
				relacao.desativar();
			}
		}
		superficie.repaint();
	}

	public static void prefixoNomeTabela(ObjetoSuperficie superficie, String prefixoNomeTabela) {
		for (Objeto objeto : superficie.objetos) {
			objeto.setPrefixoNomeTabela(prefixoNomeTabela);
		}
	}

	public static boolean getContinua(List<Objeto> lista) {
		for (Objeto objeto : lista) {
			if (!Util.estaVazio(objeto.getTabela())) {
				return true;
			}
		}
		return false;
	}
}