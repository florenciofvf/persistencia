package br.com.persist.plugins.conexao;

import java.util.Set;

import javax.swing.table.AbstractTableModel;

import br.com.persist.assistencia.BuscaConteudo;
import br.com.persist.assistencia.Constantes;

public class ConexaoModelo extends AbstractTableModel implements BuscaConteudo {
	private static final String[] COLUNAS = { "STATUS", "NOME", "DRIVER", "URL", "LOGIN", "SENHA", "SELECT CONSTRAINT",
			"FILTRO", "ESQUEMA", "CATALOGO", "TIPO=FUNCAO;TIPO=FUNCAO", "GRUPO", "LIMIT" };
	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		return ConexaoProvedor.getSize();
	}

	@Override
	public int getColumnCount() {
		return COLUNAS.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUNAS[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Object.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Conexao c = ConexaoProvedor.getConexao(rowIndex);
		switch (columnIndex) {
		case 1:
			return c.getNome();
		case 2:
			return c.getDriver();
		case 3:
			return c.getUrlBanco();
		case 4:
			return c.getUsuario();
		case 5:
			return c.getSenha();
		case 6:
			return c.getConstraint();
		case 7:
			return c.getFiltro();
		case 8:
			return c.getEsquema();
		case 9:
			return c.getCatalogo();
		case 10:
			return c.getTiposFuncoes();
		case 11:
			return c.getGrupo();
		case 12:
			return c.getLimit();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		String valor = aValue == null ? Constantes.VAZIO : aValue.toString();
		Conexao c = ConexaoProvedor.getConexao(rowIndex);
		switch (columnIndex) {
		case 2:
			c.setDriver(valor);
			break;
		case 3:
			c.setUrlBanco(valor);
			ConexaoProvedor.fechar(c);
			fireTableDataChanged();
			break;
		case 4:
			c.setUsuario(valor);
			ConexaoProvedor.fechar(c);
			fireTableDataChanged();
			break;
		case 5:
			c.setSenha(valor);
			ConexaoProvedor.fechar(c);
			fireTableDataChanged();
			break;
		case 6:
			c.setConstraint(valor);
			break;
		case 7:
			c.setFiltro(valor);
			break;
		case 8:
			c.setEsquema(valor);
			break;
		case 9:
			c.setCatalogo(valor);
			break;
		case 10:
			c.setTiposFuncoes(valor);
			break;
		case 11:
			c.setGrupo(valor);
			break;
		case 12:
			c.setLimit(valor);
			break;
		default:
		}
	}

	@Override
	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		ConexaoProvedor.contemConteudo(set, string, porParte);
	}
}