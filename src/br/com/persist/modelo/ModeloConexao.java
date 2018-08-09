package br.com.persist.modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import br.com.persist.banco.Conexao;
import br.com.persist.util.XMLUtil;
import br.com.persist.xml.XML;

public class ModeloConexao extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final String[] COLUNAS = { "NOME", "DRIVER", "URL", "LOGIN", "SENHA", "INI-COMPLEMENTO",
			"FIM-COMPLEMENTO" };
	private static final File file = new File("conexoes/conexoes.xml");
	private final List<Conexao> conexoes;

	public ModeloConexao() {
		conexoes = new ArrayList<>();
	}

	public List<Conexao> getConexoes() {
		return conexoes;
	}

	public Conexao getConexao(int i) {
		return conexoes.get(i);
	}

	public void adicionar(Conexao c) {
		if (c != null) {
			conexoes.add(c);
		}
	}

	public void primeiro(int indice) {
		if (indice > 0 && indice < getRowCount()) {
			Conexao conexao = conexoes.remove(indice);
			conexoes.add(0, conexao);
		}
	}

	@Override
	public int getRowCount() {
		return conexoes.size();
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
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Conexao conexao = conexoes.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return conexao.getNome();
		case 1:
			return conexao.getDriver();
		case 2:
			return conexao.getUrlBanco();
		case 3:
			return conexao.getUsuario();
		case 4:
			return conexao.getSenha();
		case 5:
			return conexao.getInicioComplemento();
		case 6:
			return conexao.getFinalComplemento();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Conexao conexao = conexoes.get(rowIndex);
		String valor = aValue == null ? "" : aValue.toString();

		switch (columnIndex) {
		case 0:
			conexao.setNome(valor);
			break;
		case 1:
			conexao.setDriver(valor);
			break;
		case 2:
			conexao.setUrlBanco(valor);
			break;
		case 3:
			conexao.setUsuario(valor);
			break;
		case 4:
			conexao.setSenha(valor);
			break;
		case 5:
			conexao.setInicioComplemento(valor);
			break;
		case 6:
			conexao.setFinalComplemento(valor);
			break;
		}
	}

	public void novo() {
		conexoes.add(new Conexao());
		fireTableDataChanged();
	}

	public void salvar() throws Exception {
		XMLUtil util = new XMLUtil(file);
		util.prologo();

		util.abrirTag2("conexoes");

		for (Conexao conexao : conexoes) {
			if (conexao.isValida()) {
				conexao.salvar(util);
			}
		}

		util.finalizarTag("conexoes");
		util.close();
	}

	public void abrir() throws Exception {
		conexoes.clear();

		if (file.exists() && file.canRead()) {
			XML.processarConexao(file, conexoes);
			fireTableDataChanged();
		}
	}
}