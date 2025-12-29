package br.com.persist.plugins.objeto;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JInternalFrame;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Objeto.Estado;
import br.com.persist.plugins.objeto.internal.Argumento;
import br.com.persist.plugins.objeto.internal.ArgumentoArray;
import br.com.persist.plugins.objeto.internal.ArgumentoString;
import br.com.persist.plugins.objeto.internal.ExternalFormulario;
import br.com.persist.plugins.objeto.internal.InternalContainer;
import br.com.persist.plugins.objeto.internal.InternalForm;
import br.com.persist.plugins.objeto.internal.InternalFormulario;
import br.com.persist.plugins.objeto.vinculo.ArquivoVinculo;
import br.com.persist.plugins.objeto.vinculo.ParaTabela;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.RelacaoVinculo;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;

public class ObjetoSuperficieUtil {
	private ObjetoSuperficieUtil() {
	}

	public static void preencherVinculacao(ObjetoSuperficie superficie, Vinculacao vinculacao) throws XMLException {
		vinculacao.abrir(ObjetoSuperficieUtil.criarArquivoVinculo(superficie), superficie);
	}

	public static Vinculacao getVinculacao(ObjetoSuperficie superficie) throws XMLException, ObjetoException {
		return getVinculacao(superficie, ObjetoSuperficieUtil.criarArquivoVinculo(superficie), false);
	}

	public static Vinculacao getVinculacao(ObjetoSuperficie superficie, ArquivoVinculo av, boolean criarSeInexistente)
			throws XMLException, ObjetoException {
		return ObjetoUtil.getVinculacao(superficie, av, criarSeInexistente);
	}

	public static void salvar(ObjetoSuperficie superficie, File file, Conexao conexao) throws XMLException {
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		salvarAtributos(superficie, conexao, util);
		util.fecharTag();
		salvarObjetos(superficie, util);
		salvarRelacoes(superficie, util);
		salvarForms(superficie, util);
		util.finalizarTag("fvf");
		util.close();
	}

	private static void salvarAtributos(ObjetoSuperficie superficie, Conexao conexao, XMLUtil util) {
		util.abrirTag("fvf");
		util.atributo("ajusteAutoForm", superficie.isAjusteAutoEmpilhaForm());
		util.atributo("ajusteLarguraForm", superficie.isAjusteAutoLarguraForm());
		util.atributo("compararRegistros", superficie.container.isCompararRegistros());
		util.atributoCheck("processar", superficie.getThreadManager().processar);
		util.atributo("largura", superficie.getWidth());
		util.atributo("altura", superficie.getHeight());
		util.atributo("arquivoVinculo", superficie.getArquivoVinculo());
		if (conexao != null) {
			util.atributo("conexao", conexao.getNome());
		}
	}

	private static void salvarObjetos(ObjetoSuperficie superficie, XMLUtil util) {
		for (Objeto item : superficie.getObjetos()) {
			item.salvar(util);
		}
		if (superficie.getObjetos().length > 0) {
			util.ql();
		}
	}

	private static void salvarRelacoes(ObjetoSuperficie superficie, XMLUtil util) {
		for (Relacao item : superficie.getRelacoes()) {
			item.salvar(util);
		}
		if (superficie.getRelacoes().length > 0) {
			util.ql();
		}
	}

	private static void salvarForms(ObjetoSuperficie superficie, XMLUtil util) {
		JInternalFrame[] frames = superficie.getAllFrames();
		if (frames.length > 0) {
			Arrays.sort(frames, (o1, o2) -> o1.getY() - o2.getY());
			for (JInternalFrame item : frames) {
				InternalFormulario interno = (InternalFormulario) item;
				InternalForm form = new InternalForm();
				form.copiar(interno);
				form.salvar(util);
			}
		}
	}

	public static void criarExternalFormulario(ObjetoSuperficie superficie, Conexao conexao, Objeto objeto) {
		Desktop.setComplemento(conexao, objeto);
		ExternalFormulario.criar(superficie.getFormulario(), conexao, objeto);
	}

	public static void paraFrente(ObjetoSuperficie superficie, Relacao obj) {
		if (superficie.excluir(obj)) {
			superficie.addRelacao(obj);
		}
	}

	public static void processarRelacaoVinculo(ObjetoSuperficie superficie, Vinculacao vinculacao,
			ObjetoColetor coletor) throws ObjetoException {
		for (RelacaoVinculo item : vinculacao.getRelacoes()) {
			Relacao relacao = item.criarRelacao(coletor);
			if (relacao == null) {
				continue;
			}
			Relacao equivalente = getRelacao(superficie, relacao.getOrigem(), relacao.getDestino());
			if (equivalente != null) {
				equivalente.copiarProps(relacao);
			} else {
				superficie.addRelacao(relacao);
			}
		}
	}

	public static void checagemId(ObjetoSuperficie superficie, Objeto objeto, String id, String sep) {
		boolean contem = ObjetoSuperficieUtil.contemId(superficie, objeto);
		while (contem) {
			objeto.setId(id + sep + Objeto.novaSequencia());
			contem = ObjetoSuperficieUtil.contemId(superficie, objeto);
		}
	}

	private static String getGrupoTabela(Objeto objeto) {
		return objeto.getGrupo() + " - " + objeto.getTabela();
	}

	public static List<String> getListaFormulariosInvisiveis(ObjetoSuperficie superficie) {
		List<String> lista = new ArrayList<>();
		for (JInternalFrame item : superficie.getAllFrames()) {
			if (!item.isVisible() && item instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) item;
				Objeto objeto = interno.getInternalContainer().getObjeto();
				lista.add(getGrupoTabela(objeto));
			}
		}
		return lista;
	}

	public static void tornarVisivel(ObjetoSuperficie superficie, String grupoTabela, Point point) {
		for (JInternalFrame item : superficie.getAllFrames()) {
			if (!item.isVisible() && item instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) item;
				Objeto objeto = interno.getInternalContainer().getObjeto();
				String grupoT = getGrupoTabela(objeto);
				if (grupoT.equals(grupoTabela)) {
					objeto.setVisivel(true);
					interno.setVisible(true);
					if (point != null) {
						interno.setLocation(interno.getX(), point.y);
					}
					interno.checarRedimensionamento();
					superficie.checarLargura(interno.getInternalContainer());
				}
			}
		}
	}

	public static void excluirSelecionados(ObjetoSuperficie superficie) {
		Objeto objeto = ObjetoSuperficieUtil.getPrimeiroObjetoSelecionado(superficie);
		boolean confirmado = false;
		if (objeto != null) {
			if (Util.confirmaExclusao(superficie, true)) {
				confirmado = true;
			} else {
				return;
			}
		}
		while (objeto != null) {
			superficie.excluir(objeto);
			objeto = ObjetoSuperficieUtil.getPrimeiroObjetoSelecionado(superficie);
		}
		Relacao relacao = ObjetoSuperficieUtil.getPrimeiroRelacaoSelecionado(superficie);
		if (relacao != null && !confirmado && !Util.confirmaExclusao(superficie, true)) {
			return;
		}
		while (relacao != null) {
			superficie.excluir(relacao);
			relacao = ObjetoSuperficieUtil.getPrimeiroRelacaoSelecionado(superficie);
		}
		superficie.repaint();
	}

	public static void todosIconesParaArquivoVinculado(ObjetoSuperficie superficie) throws ObjetoException {
		if (Util.isEmpty(superficie.getArquivoVinculo())) {
			Util.mensagem(superficie, ObjetoMensagens.getString("msg.todos_icones_arquivo_vinculado_inexistente"));
			return;
		}
		List<Objeto> objetosTabelaIcone = getObjetosTabelaIcone(superficie);
		if (objetosTabelaIcone.isEmpty()) {
			Util.mensagem(superficie, ObjetoMensagens.getString("msg.todos_icones_arquivo_vinculado_sem_objetos"));
			return;
		}
		if (!Util.confirmar(superficie, ObjetoMensagens.getString("msg.confirmar_todos_icones_arquivo_vinculado"),
				false)) {
			return;
		}
		Vinculacao vinculacao = new Vinculacao();
		try {
			ObjetoSuperficieUtil.preencherVinculacao(superficie, vinculacao);
		} catch (Exception ex) {
			Util.stackTraceAndMessage("TODOS_ICONES_PARA_ARQUIVO_VINCULADO", ex, superficie);
			return;
		}
		for (Objeto item : objetosTabelaIcone) {
			String tabela = item.getTabela().trim();
			ParaTabela para = vinculacao.getParaTabela(tabela);
			if (para == null) {
				para = new ParaTabela(tabela);
				vinculacao.putParaTabela(para);
			}
			para.setIcone(item.getIcone());
		}
		ObjetoSuperficieUtil.salvarVinculacao(superficie, vinculacao);
		Util.mensagem(superficie, "SUCESSO");
	}

	public static List<Objeto> getObjetosTabelaIcone(ObjetoSuperficie superficie) {
		List<Objeto> resp = new ArrayList<>();
		for (Objeto item : superficie.getObjetos()) {
			if (!Util.isEmpty(item.getTabela()) && !Util.isEmpty(item.getIcone())) {
				resp.add(item);
			}
		}
		return resp;
	}

	public static JComboBox<Objeto> criarComboObjetosSel(ObjetoSuperficie superficie) {
		return new JComboBox<>(new ObjetoComboModelo(ObjetoSuperficieUtil.getSelecionados(superficie)));
	}

	public static ArquivoVinculo criarArquivoVinculo(ObjetoSuperficie superficie) {
		return new ArquivoVinculo(superficie.getArquivoVinculo());
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
		for (Relacao item : superficie.getRelacoes()) {
			item.setPontoOrigem(b);
		}
		superficie.repaint();
	}

	public static void pontoDestino(ObjetoSuperficie superficie, boolean b) {
		for (Relacao item : superficie.getRelacoes()) {
			item.setPontoDestino(b);
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
		for (Relacao item : superficie.getRelacoes()) {
			item.setSelecionado(false);
			item.setObjetoTemp(null);
		}
	}

	public static void mover(ObjetoSuperficie superficie, char c) {
		for (Objeto item : superficie.getObjetos()) {
			if (item.isSelecionado()) {
				if (c == 'L') {
					item.x -= 5;
				} else if (c == 'R') {
					item.x += 5;
				} else if (c == 'U') {
					item.y -= 5;
				} else if (c == 'D') {
					item.y += 5;
				}
			}
		}
		superficie.repaint();
	}

	public static void excluirSemTabela(ObjetoSuperficie superficie) {
		boolean contem = false;
		for (Objeto item : superficie.getObjetos()) {
			if (Util.isEmpty(item.getTabela())) {
				contem = true;
				break;
			}
		}
		if (!contem) {
			Util.mensagem(superficie, ObjetoMensagens.getString("msg.nenhum_objeto_sem_tabela"));
			return;
		}
		if (Util.confirmaExclusao(superficie, true)) {
			for (Objeto item : superficie.getObjetos()) {
				if (Util.isEmpty(item.getTabela())) {
					superficie.excluir(item);
				}
			}
			for (Objeto objeto : superficie.getObjetos()) {
				objeto.associado = null;
			}
		}
		superficie.repaint();
	}

	public static void excluirTabelaCriterioTR(ObjetoSuperficie superficie, Label lblStatus1, Label lblStatus2) {
		List<Objeto> lista = new ArrayList<>();
		for (Objeto item : superficie.getObjetos()) {
			if (!Util.isEmpty(item.getTabela())) {
				lista.add(item);
			}
		}
		if (lista.isEmpty()) {
			Util.mensagem(superficie, ObjetoMensagens.getString("msg.nenhum_objeto_sem_tabela"));
			return;
		}
		FiltroTotalRegistro filtro = getFiltro(superficie);
		if (filtro == null) {
			return;
		}
		int total = 0;
		try {
			total = getTotal(superficie);
		} catch (ObjetoException ex) {
			return;
		}
		filtrar(lista, filtro, total);
		excluir(superficie, lista);
		superficie.repaint();
		lblStatus1.limpar();
		lblStatus2.limpar();
	}

	private static FiltroTotalRegistro getFiltro(ObjetoSuperficie superficie) {
		FiltroTotalRegistro[] array = new FiltroTotalRegistro[6];
		array[0] = new Menor();
		array[1] = new MenorIgual();
		array[2] = new Maior();
		array[3] = new MaiorIgual();
		array[4] = new Igual();
		array[5] = new Diff();
		return (FiltroTotalRegistro) Util.getValorInputDialogSelect(superficie, array);
	}

	private static int getTotal(ObjetoSuperficie superficie) throws ObjetoException {
		Object resp = Util.getValorInputDialog(superficie, "label.total", "Total", "0");
		if (resp != null && !Util.isEmpty(resp.toString())) {
			try {
				return Integer.parseInt(resp.toString().trim());
			} catch (Exception ex) {
				throw new ObjetoException("INVALIDO");
			}
		} else {
			throw new ObjetoException("ABORTADO");
		}
	}

	private static void filtrar(List<Objeto> objetos, FiltroTotalRegistro filtro, int total) {
		Iterator<Objeto> it = objetos.iterator();
		while (it.hasNext()) {
			if (!filtro.valido(it.next(), total)) {
				it.remove();
			}
		}
	}

	private static void excluir(ObjetoSuperficie superficie, List<Objeto> objetos) {
		for (Objeto item : objetos) {
			superficie.excluir(item.associado);
		}
		for (Objeto item : objetos) {
			superficie.excluir(item);
		}
	}

	private abstract static class FiltroTotalRegistro {
		private final String descricao;

		FiltroTotalRegistro(String descricao) {
			this.descricao = descricao;
		}

		abstract boolean valido(Objeto obj, int total);

		@Override
		public String toString() {
			return descricao;
		}
	}

	private static class Menor extends FiltroTotalRegistro {
		Menor() {
			super("<");
		}

		@Override
		public boolean valido(Objeto obj, int total) {
			return obj.getTotalRegistros() < total;
		}
	}

	private static class MenorIgual extends FiltroTotalRegistro {
		MenorIgual() {
			super("<=");
		}

		@Override
		public boolean valido(Objeto obj, int total) {
			return obj.getTotalRegistros() <= total;
		}
	}

	private static class Maior extends FiltroTotalRegistro {
		Maior() {
			super(">");
		}

		@Override
		public boolean valido(Objeto obj, int total) {
			return obj.getTotalRegistros() > total;
		}
	}

	private static class MaiorIgual extends FiltroTotalRegistro {
		MaiorIgual() {
			super(">=");
		}

		@Override
		public boolean valido(Objeto obj, int total) {
			return obj.getTotalRegistros() >= total;
		}
	}

	private static class Igual extends FiltroTotalRegistro {
		Igual() {
			super("==");
		}

		@Override
		public boolean valido(Objeto obj, int total) {
			return obj.getTotalRegistros() == total;
		}
	}

	private static class Diff extends FiltroTotalRegistro {
		Diff() {
			super("!=");
		}

		@Override
		public boolean valido(Objeto obj, int total) {
			return obj.getTotalRegistros() != total;
		}
	}

	public static int preTotalRecente(ObjetoSuperficie superficie, Label label) {
		int total = 0;
		for (Objeto item : superficie.getObjetos()) {
			if (!Util.isEmpty(item.getTabela())) {
				item.criarMemento();
				item.setCorFonte(ObjetoPreferencia.getCorAntesTotalRecente());
				total++;
			}
		}
		label.limpar();
		superficie.repaint();
		return total;
	}

	public static InternalFormulario getInternalFormularioIntersecao(ObjetoSuperficie superficie, Objeto objeto) {
		for (JInternalFrame item : superficie.getAllFrames()) {
			if (item instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) item;
				Rectangle boundsInterno = interno.getBounds();
				Rectangle boundsObjeto = objeto.getBounds();
				if (boundsInterno.intersects(boundsObjeto) && boundsInterno.y < boundsObjeto.y) {
					return interno;
				}
			}
		}
		return null;
	}

	public static InternalFormulario getInternalFormulario(ObjetoSuperficie superficie, Objeto objeto) {
		for (JInternalFrame item : superficie.getAllFrames()) {
			if (item instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) item;
				if (interno.ehObjeto(objeto) && interno.ehTabela(objeto)) {
					return interno;
				}
			}
		}
		return null;
	}

	public static void setTransparenciaInternalFormulario(ObjetoSuperficie superficie, float nivel) {
		for (JInternalFrame item : superficie.getAllFrames()) {
			if (item instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) item;
				interno.setNivelTransparencia(nivel);
			}
		}
		superficie.repaint();
	}

	public static void selecionarConexao(ObjetoSuperficie superficie, Conexao conexao) {
		for (JInternalFrame item : superficie.getAllFrames()) {
			if (item instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) item;
				interno.selecionarConexao(conexao);
			}
		}
	}

	public static Objeto getObjeto(ObjetoSuperficie superficie, Referencia ref) {
		if (ref == null) {
			return null;
		}
		for (Objeto item : superficie.getObjetos()) {
			if (ref.igual(item)) {
				return item;
			}
		}
		return null;
	}

	public static List<Objeto> objetosComTabela(ObjetoSuperficie superficie, Estado estado) {
		List<Objeto> resp = new ArrayList<>();
		for (Objeto item : superficie.getObjetos()) {
			if (!Util.isEmpty(item.getTabela()) && validoIncluir(item, estado)) {
				resp.add(item);
			}
		}
		return resp;
	}

	private static boolean validoIncluir(Objeto objeto, Estado estado) {
		return (estado == null || estado == Objeto.Estado.INDIFERENTE)
				|| (estado == Objeto.Estado.SELECIONADO && objeto.isSelecionado())
				|| (estado == Objeto.Estado.NAO_SELECIONADO && !objeto.isSelecionado());
	}

	public static List<String> getListaStringIds(ObjetoSuperficie superficie) {
		List<String> resp = new ArrayList<>();
		for (Objeto item : superficie.getObjetos()) {
			resp.add(item.getId());
		}
		return resp;
	}

	public static List<Objeto> getSelecionados(ObjetoSuperficie superficie) {
		List<Objeto> resp = new ArrayList<>();
		for (Objeto item : superficie.getObjetos()) {
			if (item.isSelecionado()) {
				resp.add(item);
			}
		}
		return resp;
	}

	public static List<Objeto> getIgnorados(ObjetoSuperficie superficie) {
		List<Objeto> resp = new ArrayList<>();
		for (Objeto item : superficie.getObjetos()) {
			if (item.isIgnorar()) {
				resp.add(item);
			}
		}
		return resp;
	}

	public static void desativarObjetos(ObjetoSuperficie superficie) {
		for (Objeto item : superficie.getObjetos()) {
			item.desativar();
		}
		for (Relacao item : superficie.getRelacoes()) {
			item.desativar();
		}
		superficie.repaint();
	}

	public static void limparSelecao(ObjetoSuperficie superficie) {
		for (Objeto item : superficie.getObjetos()) {
			item.setSelecionado(false);
		}
	}

	public static Objeto getPrimeiroObjetoSelecionado(ObjetoSuperficie superficie) {
		for (Objeto item : superficie.getObjetos()) {
			if (item.isSelecionado()) {
				return item;
			}
		}
		return null;
	}

	public static Relacao getPrimeiroRelacaoSelecionado(ObjetoSuperficie superficie) {
		for (Relacao item : superficie.getRelacoes()) {
			if (item.isSelecionado()) {
				return item;
			}
		}
		return null;
	}

	public static Relacao getRelacao(ObjetoSuperficie superficie, Objeto obj) {
		if (obj != null) {
			for (Relacao item : superficie.getRelacoes()) {
				if (item.contem(obj)) {
					return item;
				}
			}
		}
		return null;
	}

	public static Set<String> getIdOrigens(ObjetoSuperficie superficie) {
		Set<String> set = new HashSet<>();
		for (Relacao item : superficie.getRelacoes()) {
			set.add(item.getOrigem().getId());
		}
		return set;
	}

	public static List<Relacao> getRelacoes(ObjetoSuperficie superficie, Objeto obj) {
		List<Relacao> lista = new ArrayList<>();
		if (obj != null) {
			for (Relacao item : superficie.getRelacoes()) {
				if (item.contem(obj)) {
					lista.add(item);
				}
			}
		}
		return lista;
	}

	public static Relacao getRelacao(ObjetoSuperficie superficie, Objeto obj1, Objeto obj2) throws ObjetoException {
		if (obj1 != null && obj2 != null) {
			Relacao temp = new Relacao(obj1, obj2);
			for (Relacao item : superficie.getRelacoes()) {
				if (item.equals(temp)) {
					return item;
				}
			}
		}
		return null;
	}

	public static boolean contemId(ObjetoSuperficie superficie, Objeto obj, String id) {
		for (int i = 0; i < superficie.getObjetos().length; i++) {
			Objeto item = superficie.getObjetos()[i];
			if (item != obj && item.idEquals(id)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contemId(ObjetoSuperficie superficie, Objeto obj) {
		for (int i = 0; i < superficie.getObjetos().length; i++) {
			Objeto item = superficie.getObjetos()[i];
			if (item != obj && item.equalsId(obj)) {
				return true;
			}
		}
		return false;
	}

	public static Objeto getObjeto(ObjetoSuperficie superficie, String id) {
		for (int i = 0; i < superficie.getObjetos().length; i++) {
			if (superficie.getObjetos()[i].getId().equals(id)) {
				return superficie.getObjetos()[i];
			}
		}
		return null;
	}

	public static void desenharDesc(ObjetoSuperficie superficie, boolean b) {
		for (Relacao item : superficie.getRelacoes()) {
			item.setDesenharDescricao(b);
		}
		superficie.repaint();
	}

	public static void selecaoGeral(ObjetoSuperficie superficie, boolean b) {
		for (Objeto item : superficie.getObjetos()) {
			item.setSelecionado(b);
		}
		superficie.repaint();
	}

	public static void compararRegistro(ObjetoSuperficie superficie, boolean b) {
		for (Objeto item : superficie.getObjetos()) {
			item.setCompararRegistro(b);
		}
		superficie.repaint();
	}

	public static void desenharIds(ObjetoSuperficie superficie, boolean b) {
		for (Objeto item : superficie.getObjetos()) {
			item.setDesenharId(b);
		}
		superficie.repaint();
	}

	public static void transparente(ObjetoSuperficie superficie, boolean b) {
		for (Objeto item : superficie.getObjetos()) {
			item.setTransparente(b);
		}
		superficie.repaint();
	}

	public static void ignorar(ObjetoSuperficie superficie, boolean b) {
		for (Objeto item : superficie.getObjetos()) {
			item.setIgnorar(b);
		}
		superficie.repaint();
	}

	public static int getIndiceId(ObjetoSuperficie superficie, Objeto obj) {
		if (obj != null) {
			for (int i = 0; i < superficie.getObjetos().length; i++) {
				if (superficie.getObjetos()[i].equalsId(obj)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int getIndice(ObjetoSuperficie superficie, Objeto obj) {
		if (obj != null) {
			for (int i = 0; i < superficie.getObjetos().length; i++) {
				if (superficie.getObjetos()[i] == obj || superficie.getObjetos()[i].equals(obj)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int getIndice(ObjetoSuperficie superficie, Relacao obj) {
		if (obj != null) {
			for (int i = 0; i < superficie.getRelacoes().length; i++) {
				if (superficie.getRelacoes()[i] == obj || superficie.getRelacoes()[i].equals(obj)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static boolean contemObjetoComTabela(ObjetoSuperficie superficie, String nomeTabela) {
		for (Objeto item : superficie.getObjetos()) {
			if (item.getTabela().equalsIgnoreCase(nomeTabela)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contemReferencia(ObjetoSuperficie superficie, Objeto objeto) {
		for (Objeto item : superficie.getObjetos()) {
			if (item == objeto) {
				return true;
			}
		}
		return false;
	}

	public static void salvarVinculacao(ObjetoSuperficie superficie, Vinculacao vinculacao) {
		vinculacao.salvar(criarArquivoVinculo(superficie), superficie);
	}

	public static void processar(ObjetoSuperficie superficie) {
		for (Objeto item : superficie.getObjetos()) {
			if (item.isSelecionado()) {
				item.setProcessar(true);
				item.ativar();
			}
		}
		for (Relacao item : superficie.getRelacoes()) {
			if (item.isSelecionado()) {
				item.setProcessar(true);
				item.ativar();
			}
		}
		superficie.repaint();
	}

	public static int ativarObjetos(ObjetoSuperficie superficie, String string) {
		int total = 0;
		if (Util.isEmpty(string)) {
			return total;
		}
		string = string.trim().toUpperCase();
		for (Objeto item : superficie.getObjetos()) {
			if (item.getId().toUpperCase().indexOf(string) != -1
					|| item.getTabela().toUpperCase().indexOf(string) != -1) {
				item.setProcessar(true);
				item.ativar();
				total++;
			}
		}
		superficie.repaint();
		return total;
	}

	public static void processarTurma(ObjetoSuperficie superficie, String string) {
		for (Objeto item : superficie.getObjetos()) {
			boolean visivel = contem(item.getTurma().toUpperCase().split(","), string);
			item.setVisivel(visivel);
			InternalFormulario interno = getInternalFormulario(superficie, item);
			if (interno != null) {
				interno.setVisible(visivel);
			}
		}
		superficie.repaint();
	}

	private static boolean contem(String[] array, String string) {
		for (String item : array) {
			if (item.trim().equals(string)) {
				return true;
			}
		}
		return false;
	}

	public static void desativar(ObjetoSuperficie superficie) {
		for (Objeto item : superficie.getObjetos()) {
			if (item.isSelecionado()) {
				item.desativar();
			}
		}
		for (Relacao item : superficie.getRelacoes()) {
			if (item.isSelecionado()) {
				item.desativar();
			}
		}
		superficie.repaint();
	}

	public static void prefixoNomeTabela(ObjetoSuperficie superficie, String prefixoNomeTabela) {
		for (Objeto item : superficie.getObjetos()) {
			item.setPrefixoNomeTabela(prefixoNomeTabela);
		}
	}

	public static boolean getContinua(List<Objeto> lista) {
		for (Objeto item : lista) {
			if (!Util.isEmpty(item.getTabela())) {
				return true;
			}
		}
		return false;
	}

	public static void pesquisarReferencia(ObjetoSuperficie superficie, Conexao conexao, Pesquisa pesquisa,
			Referencia referencia, Argumento argumento, Objeto objeto) throws AssistenciaException {
		String string = null;
		if (argumento instanceof ArgumentoString) {
			string = objeto.comApelido("AND", referencia.getCampo()) + " IN ("
					+ ((ArgumentoString) argumento).getString() + ")"
					+ referencia.getConcatenar(pesquisa.getCloneParams());
		} else if (argumento instanceof ArgumentoArray) {
			ArgumentoArray argumentoArray = (ArgumentoArray) argumento;
			String[] chavesReferencia = referencia.getChavesArray();
			if (chavesReferencia.length != argumentoArray.getQtdChaves()) {
				return;
			}
			String filtro = InternalContainer.montarFiltro(objeto, argumentoArray, chavesReferencia);
			string = filtro + referencia.getConcatenar(pesquisa.getCloneParams());
		}
		objeto.setComplemento(string);
		objeto.setReferenciaPesquisa(referencia);
		if (ObjetoPreferencia.isAbrirAutoDestacado()) {
			criarExternalFormulario(superficie, conexao != null ? conexao : superficie.container.getConexaoPadrao(),
					objeto.clonar());
		} else {
			objeto.setSelecionado(true);
		}
		referencia.setProcessado(true);
	}
}