package br.com.persist.conexao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import br.com.persist.chave_valor.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.xml.XML;
import br.com.persist.xml.XMLUtil;

public class ConexaoModelo extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final String[] COLUNAS = { "STATUS", "NOME", "DRIVER", "URL", "LOGIN", "SENHA", "INI-COMPLEMENTO",
			"FIM-COMPLEMENTO", "ESQUEMA", "CATALOGO" };
	private static final File file = new File("conexoes/conexoes.xml");
	private static final Logger LOG = Logger.getGlobal();
	private final transient List<Conexao> conexoes;

	public ConexaoModelo() {
		conexoes = new ArrayList<>();
	}

	public List<Conexao> getConexoes() {
		return conexoes;
	}

	public Conexao getConexao(int i) {
		return conexoes.get(i);
	}

	public ChaveValor getChaveValor(int i) {
		Conexao conexao = getConexao(i);
		return new URLChaveValor(conexao);
	}

	public class URLChaveValor extends ChaveValor {
		private final Conexao conexao;

		public URLChaveValor(Conexao conexao) {
			super(conexao.getNome(), conexao.getUrlBanco());
			this.conexao = conexao;
		}

		@Override
		public String getValor() {
			return conexao.getUrlBanco();
		}

		@Override
		public void setValor(String valor) {
			conexao.setUrlBanco(valor);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}
	}

	public void adicionar(Conexao c) {
		if (c != null) {
			conexoes.add(c);
		}
	}

	public int primeiro(int indice) {
		if (indice > 0 && indice < getRowCount()) {
			Conexao conexao = conexoes.remove(indice);
			conexoes.add(0, conexao);

			return 0;
		}

		return -1;
	}

	public int anterior(int indice) {
		if (indice > 0 && indice < getRowCount()) {
			Conexao conexao = conexoes.remove(indice);
			indice--;
			conexoes.add(indice, conexao);

			return indice;
		}

		return -1;
	}

	public int proximo(int indice) {
		if (indice + 1 < getRowCount()) {
			Conexao conexao = conexoes.remove(indice);
			indice++;
			conexoes.add(indice, conexao);

			return indice;
		}

		return -1;
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
		return Object.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Conexao conexao = conexoes.get(rowIndex);

		switch (columnIndex) {
		case 1:
			return conexao.getNome();
		case 2:
			return conexao.getDriver();
		case 3:
			return conexao.getUrlBanco();
		case 4:
			return conexao.getUsuario();
		case 5:
			return conexao.getSenha();
		case 6:
			return conexao.getInicioComplemento();
		case 7:
			return conexao.getFinalComplemento();
		case 8:
			return conexao.getEsquema();
		case 9:
			return conexao.getCatalogo();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Conexao conexao = conexoes.get(rowIndex);
		String valor = aValue == null ? Constantes.VAZIO : aValue.toString();

		switch (columnIndex) {
		case 1:
			conexao.setNome(valor);
			break;
		case 2:
			conexao.setDriver(valor);
			break;
		case 3:
			conexao.setUrlBanco(valor);
			conexao.fechar();
			fireTableDataChanged();
			break;
		case 4:
			conexao.setUsuario(valor);
			conexao.fechar();
			fireTableDataChanged();
			break;
		case 5:
			conexao.setSenha(valor);
			conexao.fechar();
			fireTableDataChanged();
			break;
		case 6:
			conexao.setInicioComplemento(valor);
			break;
		case 7:
			conexao.setFinalComplemento(valor);
			break;
		case 8:
			conexao.setEsquema(valor);
			break;
		case 9:
			conexao.setCatalogo(valor);
			break;
		default:
		}
	}

	public void novo() {
		conexoes.add(new Conexao());
		fireTableDataChanged();
	}

	public void salvar() {
		try {
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
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public void abrir() {
		conexoes.clear();

		try {
			ConexaoColetor coletor = new ConexaoColetor();

			if (file.exists() && file.canRead()) {
				XML.processar(file, new ConexaoHandler(coletor));
				fireTableDataChanged();
			}

			conexoes.addAll(coletor.getConexoes());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}
}