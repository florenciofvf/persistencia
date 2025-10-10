package br.com.persist.plugins.quebra_log;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginBasico;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class QuebraLogContainer extends AbstratoContainer implements PluginBasico {
	private PanelQuebraLog panel = new PanelQuebraLog();
	private QuebraLogFormulario quebraLogFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private QuebraLogDialogo quebraLogDialogo;

	public QuebraLogContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public QuebraLogDialogo getQuebraLogDialogo() {
		return quebraLogDialogo;
	}

	public void setQuebraLogDialogo(QuebraLogDialogo quebraLogDialogo) {
		this.quebraLogDialogo = quebraLogDialogo;
		if (quebraLogDialogo != null) {
			quebraLogFormulario = null;
		}
	}

	public QuebraLogFormulario getQuebraLogFormulario() {
		return quebraLogFormulario;
	}

	public void setQuebraLogFormulario(QuebraLogFormulario quebraLogFormulario) {
		this.quebraLogFormulario = quebraLogFormulario;
		if (quebraLogFormulario != null) {
			quebraLogDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, panel);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(QuebraLogContainer.this)) {
				QuebraLogFormulario.criar(formulario, QuebraLogContainer.this);
			} else if (quebraLogDialogo != null) {
				quebraLogDialogo.excluirContainer();
				QuebraLogFormulario.criar(formulario, QuebraLogContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (quebraLogFormulario != null) {
				quebraLogFormulario.excluirContainer();
				formulario.adicionarPagina(QuebraLogContainer.this);
			} else if (quebraLogDialogo != null) {
				quebraLogDialogo.excluirContainer();
				formulario.adicionarPagina(QuebraLogContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (quebraLogDialogo != null) {
				quebraLogDialogo.excluirContainer();
			}
			QuebraLogFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
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
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return QuebraLogFabrica.class;
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
				return QuebraLogMensagens.getString(QuebraLogConstantes.LABEL_QUEBRA_LOG_MIN);
			}

			@Override
			public String getTitulo() {
				return QuebraLogMensagens.getString(QuebraLogConstantes.LABEL_QUEBRA_LOG);
			}

			@Override
			public String getHint() {
				return QuebraLogMensagens.getString(QuebraLogConstantes.LABEL_QUEBRA_LOG);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL2;
			}
		};
	}
}

class PanelQuebraLog extends Panel {
	private static final long serialVersionUID = 1L;
	private Button btnDestino = new Button(QuebraLogMensagens.getString("label.diretorio_destino"), false);
	private Button btnOrigem = new Button(QuebraLogMensagens.getString("label.arquivo_origem"), false);
	private JTable table = new JTable(new QuebraLogModelo());
	private Button btnLimpar = new Button("label.limpar");
	private Button btnCriar = new Button("label.criar");
	private TextField txtDestino = new TextField();
	private TextField txtOrigem = new TextField();
	private TextField txtTotal = new TextField(5);
	private final Label labelStatus = new Label();

	PanelQuebraLog() {
		montarLayout();
		inicializar();
		configurar();
	}

	private void montarLayout() {
		Panel panelNorte = new Panel(new GridLayout(3, 0));

		Panel panelOrigem = new Panel();
		panelOrigem.add(BorderLayout.CENTER, txtOrigem);
		panelOrigem.add(BorderLayout.EAST, btnOrigem);

		Panel panelDestino = new Panel();
		panelDestino.add(BorderLayout.CENTER, txtDestino);
		panelDestino.add(BorderLayout.EAST, btnDestino);

		Panel panelControle = new Panel(new FlowLayout());
		panelControle.add(new Label(QuebraLogMensagens.getString("label.total_arquivos"), false));
		panelControle.add(txtTotal);
		panelControle.add(btnCriar);
		panelControle.add(btnLimpar);

		panelNorte.add(panelOrigem);
		panelNorte.add(panelDestino);
		panelNorte.add(panelControle);

		add(BorderLayout.NORTH, panelNorte);
		add(BorderLayout.CENTER, new JScrollPane(table));
		add(BorderLayout.SOUTH, labelStatus);
		labelStatus.setForeground(Color.BLUE);
		labelStatus.modoCopiar();
	}

	private void configurar() {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int i = table.getSelectedRow();
					if (i != -1) {
						QuebraLogModelo modelo = (QuebraLogModelo) table.getModel();
						modelo.getQuebraLog(i).abrir(PanelQuebraLog.this);
					}
				}
			}
		});
		btnLimpar.addActionListener(e -> inicializar());
		btnOrigem.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			int i = fileChooser.showOpenDialog(PanelQuebraLog.this);
			if (i == JFileChooser.APPROVE_OPTION) {
				File sel = fileChooser.getSelectedFile();
				txtOrigem.setText(sel.getAbsolutePath());
				info(sel);
			}
		});
		btnDestino.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int i = fileChooser.showOpenDialog(PanelQuebraLog.this);
			if (i == JFileChooser.APPROVE_OPTION) {
				File sel = fileChooser.getSelectedFile();
				txtDestino.setText(sel.getAbsolutePath());
			}
		});
		btnCriar.addActionListener(e -> {
			try {
				validar();
			} catch (Exception ex) {
				Util.mensagem(PanelQuebraLog.this, ex.getMessage());
				return;
			}
			processar();
		});
	}

	private void validar() throws QuebraLogException {
		String origem = txtOrigem.getText();
		if (Util.isEmpty(origem) || !new File(origem).isFile()) {
			txtOrigem.requestFocus();
			throw new QuebraLogException("err.arquivo_origem");
		}

		String destino = txtDestino.getText();
		if (Util.isEmpty(destino) || !new File(destino).isDirectory()) {
			txtDestino.requestFocus();
			throw new QuebraLogException("err.arquivo_destino");
		}

		String total = txtTotal.getText();
		if (Util.isEmpty(total) || totalInvalido(total)) {
			txtTotal.requestFocus();
			throw new QuebraLogException("err.total");
		}
	}

	private boolean totalInvalido(String string) {
		for (char c : string.toCharArray()) {
			if (!numero(c)) {
				return true;
			}
		}
		return false;
	}

	private boolean numero(char c) {
		return c >= '0' && c <= '9';
	}

	private void processar() {
		int totalBlocos = Integer.parseInt(txtTotal.getText());
		File destino = new File(txtDestino.getText());
		File origem = new File(txtOrigem.getText());
		long tamanho = origem.length();
		long tamanhoBloco = tamanho / totalBlocos;
		String sufixo = getSufixo(origem);
		int contador = 0;
		int indice = 0;

		QuebraLogModelo modelo = new QuebraLogModelo();

		while (contador < totalBlocos) {
			File bloco = new File(destino, origem.getName() + "_" + contador + "_" + sufixo);
			QuebraLog qlog = new QuebraLog(origem, bloco, indice, tamanhoBloco);
			qlog.setRow(contador);
			modelo.adicionar(qlog);
			indice += tamanhoBloco;
			contador++;
		}

		table.setModel(modelo);
		modelo.fragmentarArquivo(table);
	}

	private String getSufixo(File f) {
		String nome = f.getName();
		int pos = nome.lastIndexOf('.');
		if (pos != -1) {
			return nome.substring(pos);
		}
		return "";
	}

	private void inicializar() {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setModel(new QuebraLogModelo());
		labelStatus.limpar();
		txtDestino.limpar();
		txtOrigem.limpar();
	}

	private void info(File file) {
		if (file == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("EXISTS?=" + file.exists());
		sb.append(", FILE?=" + file.isFile());
		sb.append(", DIRECTORY?=" + file.isDirectory());
		sb.append(", BYTES=" + file.length());
		sb.append(", HIDDEN?=" + file.isHidden());
		sb.append(", CAN READ?=" + file.canRead());
		sb.append(", CAN WRITE?=" + file.canWrite());
		sb.append(", LAST MODIFIED=" + file.lastModified());
		sb.append(", SIZE=" + QuebraLog.atualizarTamanho(file));
		labelStatus.setText(sb.toString());

		if (Util.isEmpty(txtDestino.getText())) {
			txtDestino.setText(file.getParent());
		}
	}
}