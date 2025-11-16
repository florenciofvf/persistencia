package br.com.persist.plugins.persistencia;

import java.awt.Component;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoProvedor;

public class PersistenciaModelo implements TableModel {
	private static final String MSG_AREA_TRANS = "msg.area_trans_tabela_registros";
	private static final Logger LOG = Logger.getGlobal();
	private final List<List<Object>> registros;
	private final List<Coluna> colunas;
	private String prefixoNomeTabela;
	private Component componente;
	private final String tabela;
	private Conexao conexao;

	public PersistenciaModelo(List<Coluna> colunas, List<List<Object>> registros, String tabela, Conexao conexao) {
		this.registros = registros;
		this.conexao = conexao;
		this.colunas = colunas;
		this.tabela = tabela;
	}

	private boolean contemChaves() {
		for (Coluna c : colunas) {
			if (c.isChave()) {
				return true;
			}
		}
		return false;
	}

	public static PersistenciaModelo criarVazio() {
		List<Coluna> colunas = new ArrayList<>();
		List<List<Object>> registros = new ArrayList<>();
		return new PersistenciaModelo(colunas, registros, null, null);
	}

	public void atualizarSequencias(Map<String, String> mapaSequencia) {
		for (Coluna c : colunas) {
			String nomeSequencia = mapaSequencia.get(c.getNome());
			c.setSequencia(nomeSequencia);
		}
	}

	public static class Parametros {
		private Map<String, String> mapaSequencia;
		private Map<String, String> mapaFuncoes;
		private final Conexao conexao;
		private final String consulta;
		private String[] colunasChave;
		private boolean comColunaInfo;
		private final Connection conn;
		private String tabela;

		public Parametros(Connection conn, Conexao conexao, String consulta) {
			this.consulta = consulta;
			this.conexao = conexao;
			this.conn = conn;
		}

		public Map<String, String> getMapaFuncoes() {
			return mapaFuncoes;
		}

		public void setMapaFuncoes(Map<String, String> mapaFuncoes) {
			this.mapaFuncoes = mapaFuncoes;
		}

		public Map<String, String> getMapaSequencia() {
			return mapaSequencia;
		}

		public void setMapaSequencia(Map<String, String> mapaSequencia) {
			this.mapaSequencia = mapaSequencia;
		}

		public String[] getColunasChave() {
			return colunasChave;
		}

		public void setColunasChave(String[] colunasChave) {
			this.colunasChave = colunasChave;
		}

		public boolean isComColunaInfo() {
			return comColunaInfo;
		}

		public void setComColunaInfo(boolean comColunaInfo) {
			this.comColunaInfo = comColunaInfo;
		}

		public Connection getConn() {
			return conn;
		}

		public String getTabela() {
			return tabela;
		}

		public void setTabela(String tabela) {
			this.tabela = tabela;
		}

		public Conexao getConexao() {
			return conexao;
		}

		public String getConsulta() {
			return consulta;
		}
	}

	@Override
	public int getRowCount() {
		return registros.size();
	}

	@Override
	public int getColumnCount() {
		return colunas.size();
	}

	public List<Coluna> getColunas() {
		return colunas;
	}

	public List<String> getListaNomeColunasPreenchidas(boolean comChaves, int indice) {
		List<Object> registro = registros.get(indice);
		return getListaNomeColunasPreenchidas(comChaves, registro);
	}

	public boolean contemCampoVazio(boolean comChaves, int indice) {
		List<Object> registro = registros.get(indice);
		return contemCampoVazio(comChaves, registro);
	}

	public List<String> getListaNomeColunas(boolean comChaves) {
		List<String> lista = new ArrayList<>();
		for (Coluna c : colunas) {
			if (!c.isColunaInfo() && !c.isInativoTemp()) {
				if (c.isChave()) {
					if (comChaves) {
						lista.add(c.getNome());
					}
				} else {
					lista.add(c.getNome());
				}
			}
		}
		return lista;
	}

	public String detalhesExcecao() {
		StringBuilder sb = new StringBuilder();
		for (Coluna c : colunas) {
			if (c.getStringComparaRegistro() != null) {
				if (sb.length() > 0) {
					sb.append(Constantes.QL);
				}
				sb.append(c.getStringComparaRegistro());
			}
		}
		return sb.toString();
	}

	public List<Coluna> listarColunasTabela() {
		List<Coluna> lista = new ArrayList<>();
		for (Coluna c : colunas) {
			if (!c.isColunaInfo()) {
				lista.add(c);
			}
		}
		return lista;
	}

	public List<Coluna> getColunas(List<String> nomes) {
		List<Coluna> lista = new ArrayList<>();
		for (Coluna c : colunas) {
			if (nomes.contains(c.getNome())) {
				lista.add(c);
			}
		}
		return lista;
	}

	public List<String> getListaNomeColunasObrigatorias() {
		List<String> lista = new ArrayList<>();
		for (Coluna c : colunas) {
			if (!c.isColunaInfo() && !c.isNulavel()) {
				lista.add(c.getNome());
			}
		}
		return lista;
	}

	public Conexao getConexao() {
		return conexao;
	}

	public void setConexao(Conexao conexao) {
		this.conexao = conexao;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colunas.get(columnIndex).getNome();
	}

	public Coluna getColuna(int indice) {
		return colunas.get(indice);
	}

	public Coluna getColuna(String nome) {
		for (Coluna c : colunas) {
			if (c.getNome().equalsIgnoreCase(nome)) {
				return c;
			}
		}
		return null;
	}

	public int getIndice(String nome) {
		for (int i = 0; i < colunas.size(); i++) {
			Coluna c = colunas.get(i);
			if (c.getNome().equalsIgnoreCase(nome)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		Coluna coluna = colunas.get(columnIndex);
		return !coluna.isChave() && !coluna.isBlob() && !coluna.isColunaInfo();
	}

	public List<Object> getRegistro(int rowIndex) {
		return registros.get(rowIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		List<Object> registro = registros.get(rowIndex);
		return registro.get(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		List<Object> registro = registros.get(rowIndex);
		if (contemChaves()) {
			try {
				Coluna coluna = colunas.get(columnIndex);
				String update = gerarUpdate(registro, new Coluna[] { coluna }, new Object[] { aValue },
						getPrefixoNomeTabela(), new Coletor(coluna.getNome()), true, conexao);
				Persistencia.executar(ConexaoProvedor.getConnection(conexao), update);
				registro.set(columnIndex, aValue);
				if (Util.confirmar(componente, PersistenciaMensagens.getString(MSG_AREA_TRANS), false)) {
					Util.setContentTransfered(update);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("UPDATE", ex, componente);
			}
		} else {
			registro.set(columnIndex, aValue);
		}
	}

	public void getDados(int rowIndex, StringBuilder sb, Coletor coletor, Conexao conexao) {
		List<Object> registro = registros.get(rowIndex);
		List<Object> valores = new ArrayList<>();
		for (Coluna coluna : colunas) {
			valores.add(registro.get(coluna.getIndice()));
		}
		getDado(colunas.toArray(new Coluna[0]), valores.toArray(new Object[0]), sb, coletor, conexao);
	}

	public Map<String, Object> getMap(int rowIndex, Coletor coletor) {
		List<Object> registro = registros.get(rowIndex);
		List<Object> valores = new ArrayList<>();
		for (Coluna coluna : colunas) {
			valores.add(registro.get(coluna.getIndice()));
		}
		return getMap(colunas.toArray(new Coluna[0]), valores.toArray(new Object[0]), coletor);
	}

	public String getUpdate(int rowIndex, String prefixoNomeTabela, Coletor coletor, boolean comWhere, Conexao conexao)
			throws PersistenciaException {
		List<Object> registro = registros.get(rowIndex);
		if (contemChaves()) {
			List<Object> valores = new ArrayList<>();
			List<Coluna> naoChaves = getNaoChaves();
			if (naoChaves.isEmpty()) {
				return null;
			}
			for (Coluna coluna : naoChaves) {
				valores.add(registro.get(coluna.getIndice()));
			}
			return gerarUpdate(registro, naoChaves.toArray(new Coluna[0]), valores.toArray(new Object[0]),
					prefixoNomeTabela, coletor, comWhere, conexao);
		}
		return null;
	}

	public String getUpdate(String prefixoNomeTabela, Coletor coletor, boolean comWhere, Conexao conexao)
			throws PersistenciaException {
		if (contemChaves()) {
			List<Object> valores = new ArrayList<>();
			List<Coluna> naoChaves = getNaoChaves();
			if (naoChaves.isEmpty()) {
				return null;
			}
			for (Coluna coluna : naoChaves) {
				valores.add(coluna.getNome());
			}
			return gerarUpdate(null, naoChaves.toArray(new Coluna[0]), valores.toArray(new Object[0]),
					prefixoNomeTabela, coletor, comWhere, conexao);
		}
		return null;
	}

	public String getDelete(int rowIndex, String prefixoNomeTabela, boolean comWhere, Conexao conexao)
			throws PersistenciaException {
		List<Object> registro = registros.get(rowIndex);
		if (contemChaves()) {
			return gerarDelete(registro, prefixoNomeTabela, comWhere, conexao);
		}
		return null;
	}

	public String getDelete(String prefixoNomeTabela, boolean comWhere, Conexao conexao) throws PersistenciaException {
		if (contemChaves()) {
			return gerarDelete(null, prefixoNomeTabela, comWhere, conexao);
		}
		return null;
	}

	public String getInsert(int rowIndex, String prefixoNomeTabela, Coletor coletor) {
		List<Object> registro = registros.get(rowIndex);
		return gerarInsert(registro, prefixoNomeTabela, coletor);
	}

	public String getInsert(String prefixoNomeTabela, Coletor coletor) {
		return gerarInsert(null, prefixoNomeTabela, coletor);
	}

	public int excluir(int rowIndex, String prefixoNomeTabela, boolean comWhere, Conexao conexao,
			AtomicBoolean confirmarCadaItem) {
		List<Object> registro = registros.get(rowIndex);
		if (contemChaves()) {
			try {
				String delete = gerarDelete(registro, prefixoNomeTabela, comWhere, conexao);
				int i = Persistencia.executar(ConexaoProvedor.getConnection(conexao), delete);
				if (confirmarCadaItem != null) {
					if (confirmarCadaItem.get() && Util.confirmarComMemorizacao(componente,
							PersistenciaMensagens.getString(MSG_AREA_TRANS), confirmarCadaItem)) {
						Util.setContentTransfered(delete);
					}
				} else {
					if (Util.confirmar(componente, PersistenciaMensagens.getString(MSG_AREA_TRANS), false)) {
						Util.setContentTransfered(delete);
					}
				}
				return i;
			} catch (Exception ex) {
				Util.stackTraceAndMessage("DELETE", ex, componente);
				return -1;
			}
		}
		return -1;
	}

	public List<IndiceValor> getValoresChaves(int rowIndex) {
		List<Object> registro = registros.get(rowIndex);
		List<IndiceValor> resp = new ArrayList<>();
		for (Coluna coluna : getChaves()) {
			IndiceValor obj = new IndiceValor(coluna.getIndice(), registro.get(coluna.getIndice()));
			resp.add(obj);
		}
		return resp;
	}

	public Map<String, String> getMapaChaves(int rowIndex, Conexao conexao) {
		List<Object> registro = registros.get(rowIndex);
		Map<String, String> resp = new HashMap<>();
		for (Coluna coluna : getChaves()) {
			Object valor = registro.get(coluna.getIndice());
			resp.put(coluna.getNome(), coluna.get(valor.toString(), conexao));
		}
		return resp;
	}

	public Registro criarRegistro(List<Coluna> chaves, int rowIndex, Conexao conexao) {
		List<Object> registro = registros.get(rowIndex);
		Registro resp = new Registro();
		for (Coluna chave : chaves) {
			String nome = chave.getNome();
			Object valor = registro.get(chave.getIndice());
			valor = chave.get(valor.toString(), conexao);
			resp.add(new ChaveValor(nome, valor));
		}
		return resp;
	}

	public void excluirValoresChaves(List<IndiceValor> lista) {
		int indice = getIndice(lista);
		if (indice != -1) {
			registros.remove(indice);
		}
	}

	private int getIndice(List<IndiceValor> valores) {
		for (int i = 0; i < registros.size(); i++) {
			List<Object> registro = registros.get(i);
			if (ehLinhaValida(registro, valores)) {
				return i;
			}
		}
		return -1;
	}

	private boolean ehLinhaValida(List<Object> registro, List<IndiceValor> valores) {
		for (IndiceValor obj : valores) {
			if (!obj.igual(registro)) {
				return false;
			}
		}
		return true;
	}

	private void getDado(Coluna[] colunas, Object[] valores, StringBuilder sb, Coletor coletor, Conexao conexao) {
		for (int i = 0; i < colunas.length; i++) {
			Coluna coluna = colunas[i];
			if (coletor.contem(coluna.getNome())) {
				sb.append(Constantes.QL + coluna.getNome() + " = " + coluna.get(valores[i], conexao));
			}
		}
	}

	private Map<String, Object> getMap(Coluna[] colunas, Object[] valores, Coletor coletor) {
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < colunas.length; i++) {
			Coluna coluna = colunas[i];
			if (coletor.contem(coluna.getNome())) {
				map.put(coluna.getNome(), valores[i].toString());
			}
		}
		return map;
	}

	private String gerarUpdate(List<Object> registro, Coluna[] colunas, Object[] valores, String prefixoNomeTabela,
			Coletor coletor, boolean comWhere, Conexao conexao) throws PersistenciaException {
		StringBuilder resposta = new StringBuilder(
				"UPDATE " + prefixarEsquema(conexao, prefixoNomeTabela, tabela, Constantes.VAZIO) + " SET ");
		int i = 0;
		Coluna coluna = null;
		for (; i < colunas.length; i++) {
			coluna = colunas[i];
			if (coletor.contem(coluna.getNome())) {
				resposta.append(Constantes.QL + "  " + coluna.getNome() + " = " + coluna.get(valores[i], conexao));
				i++;
				break;
			}
		}
		for (; i < colunas.length; i++) {
			coluna = colunas[i];
			if (coluna.isColunaInfo()) {
				continue;
			}
			if (coletor.contem(coluna.getNome())) {
				resposta.append(Constantes.QL + ", " + coluna.getNome() + " = " + coluna.get(valores[i], conexao));
			}
		}
		if (comWhere) {
			resposta.append(montarWhere(registro, conexao));
		}
		return resposta.toString();
	}

	public List<String> getListaNomeColunasPreenchidas(boolean comChaves, List<Object> registro) {
		List<String> lista = new ArrayList<>();
		for (Coluna c : colunas) {
			if (!c.isColunaInfo() && !c.isInativoTemp()) {
				if (c.isChave()) {
					if (comChaves) {
						appendNomeColunaPreenchida(c, registro, lista);
					}
				} else {
					appendNomeColunaPreenchida(c, registro, lista);
				}
			}
		}
		return lista;
	}

	public boolean contemCampoVazio(boolean comChaves, List<Object> registro) {
		AtomicBoolean atomic = new AtomicBoolean(false);
		for (Coluna c : colunas) {
			if (!c.isColunaInfo() && !c.isInativoTemp()) {
				if (c.isChave()) {
					if (comChaves) {
						contemCampoVazio(c, registro, atomic);
					}
				} else {
					contemCampoVazio(c, registro, atomic);
				}
			}
		}
		return atomic.get();
	}

	private String gerarInsert(List<Object> registro, String prefixoNomeTabela, Coletor coletor) {
		if (colunas.isEmpty()) {
			return null;
		}
		StringBuilder resposta = new StringBuilder("INSERT INTO "
				+ prefixarEsquema(conexao, prefixoNomeTabela, tabela, Constantes.VAZIO) + " (" + Constantes.QL);
		StringBuilder campo = new StringBuilder();
		StringBuilder valor = new StringBuilder("VALUES (" + Constantes.QL);
		int i = 0;
		Coluna coluna = null;
		for (; i < colunas.size(); i++) {
			coluna = colunas.get(i);
			if (coletor.contem(coluna.getNome())) {
				appendCampoValor(Constantes.VAZIO, campo, valor, coluna, registro, prefixoNomeTabela, conexao);
				i++;
				break;
			}
		}
		for (; i < colunas.size(); i++) {
			coluna = colunas.get(i);
			if (coluna.isColunaInfo()) {
				continue;
			}
			if (coletor.contem(coluna.getNome())) {
				appendCampoValor(", ", campo, valor, coluna, registro, prefixoNomeTabela, conexao);
			}
		}
		campo.append(")" + Constantes.QL);
		valor.append(")" + Constantes.QL);
		return resposta.append(campo).append(valor).toString();
	}

	private void appendCampoValor(String string, StringBuilder campo, StringBuilder valor, Coluna coluna,
			List<Object> registro, String prefixoNomeTabela, Conexao conexao) {
		campo.append(Constantes.TAB + string + coluna.getNome() + Constantes.QL);
		if (Util.isEmpty(coluna.getSequencia())) {
			if (registro != null) {
				Object valoR = registro.get(coluna.getIndice());
				valor.append(Constantes.TAB + string + getValor(coluna, coluna.get(valoR, conexao)) + Constantes.QL);
			} else {
				valor.append(Constantes.TAB + string + coluna.get(coluna.getNome(), conexao) + Constantes.QL);
			}
		} else {
			valor.append(Constantes.TAB + string
					+ prefixarEsquema(conexao, prefixoNomeTabela, coluna.getSequencia(), Constantes.VAZIO)
					+ Constantes.QL);
		}
	}

	private void appendNomeColunaPreenchida(Coluna coluna, List<Object> registro, List<String> nomeColunas) {
		Object valor = registro.get(coluna.getIndice());
		if (valor != null && !Util.isEmpty(valor.toString())) {
			nomeColunas.add(coluna.getNome());
		}
	}

	private void contemCampoVazio(Coluna coluna, List<Object> registro, AtomicBoolean atomic) {
		Object valor = registro.get(coluna.getIndice());
		if (valor == null || Util.isEmpty(valor.toString())) {
			atomic.set(true);
		}
	}

	private String getValor(Coluna coluna, String valor) {
		return Util.isEmpty(coluna.getValorAlternativoInsert()) ? valor : coluna.getValorAlternativoInsert();
	}

	private String gerarDelete(List<Object> registro, String prefixoNomeTabela, boolean comWhere, Conexao conexao)
			throws PersistenciaException {
		StringBuilder resposta = new StringBuilder(
				"DELETE FROM " + prefixarEsquema(conexao, prefixoNomeTabela, tabela, Constantes.VAZIO));
		if (comWhere) {
			resposta.append(montarWhere(registro, conexao));
		}
		return resposta.toString();
	}

	private String montarWhere(List<Object> registro, Conexao conexao) throws PersistenciaException {
		List<Coluna> colunasChave = getChaves();
		if (colunasChave.isEmpty()) {
			throw new PersistenciaException("Sem coluna(s) chave(s).");
		}
		StringBuilder resposta = new StringBuilder(Constantes.QL + " WHERE ");
		Coluna coluna = colunasChave.get(0);
		if (registro != null) {
			Object valor = registro.get(coluna.getIndice());
			resposta.append(coluna.getNome() + " = " + coluna.get(valor, conexao));
		} else {
			resposta.append(coluna.getNome() + " = " + coluna.get(coluna.getNome(), conexao));
		}
		for (int i = 1; i < colunasChave.size(); i++) {
			coluna = colunasChave.get(i);
			if (registro != null) {
				Object valor = registro.get(coluna.getIndice());
				resposta.append(" AND " + coluna.getNome() + " = " + coluna.get(valor, conexao));
			} else {
				resposta.append(" AND " + coluna.getNome() + " = " + coluna.get(coluna.getNome(), conexao));
			}
		}
		return resposta.toString();
	}

	public static String prefixarEsquema(Conexao conexao, String prefixoNomeTabela, String tabela, String apelido) {
		String esquema = conexao == null ? Constantes.VAZIO : conexao.getEsquema();
		String prefixoTabela = prefixoNomeTabela == null ? Constantes.VAZIO : prefixoNomeTabela;
		String resp = (Util.isEmpty(esquema) ? Constantes.VAZIO : esquema + ".") + prefixoTabela + tabela;
		if (!Util.isEmpty(apelido)) {
			resp += " " + apelido;
		}
		return resp;
	}

	public static boolean usarTabelaAlternativa(Conexao conexao, String esquemaAlternativo, String tabelaAlternativa) {
		if (Util.isEmpty(esquemaAlternativo) || Util.isEmpty(tabelaAlternativa)) {
			return false;
		}
		return esquemaAlternativo.equalsIgnoreCase(conexao.getEsquema());
	}

	List<Coluna> getChaves() {
		return colunas.stream().filter(Coluna::isChave).collect(Collectors.toList());
	}

	private List<Coluna> getNaoChaves() {
		return colunas.stream().filter(Coluna::isNaoChave).collect(Collectors.toList());
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		LOG.log(Level.FINEST, "addTableModelListener");
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		LOG.log(Level.FINEST, "removeTableModelListener");
	}

	public String getPrefixoNomeTabela() {
		if (Util.isEmpty(prefixoNomeTabela)) {
			prefixoNomeTabela = Constantes.VAZIO;
		}
		return prefixoNomeTabela;
	}

	public void setPrefixoNomeTabela(String prefixoNomeTabela) {
		this.prefixoNomeTabela = prefixoNomeTabela;
	}

	public Component getComponente() {
		return componente;
	}

	public void setComponente(Component componente) {
		this.componente = componente;
	}
}