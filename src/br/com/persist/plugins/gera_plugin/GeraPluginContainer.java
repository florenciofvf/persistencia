package br.com.persist.plugins.gera_plugin;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Muro;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class GeraPluginContainer extends AbstratoContainer {
	private JComboBox<Icone> cmbIconePlugin = new JComboBox<>(arrayObjetoIcone());
	private CheckBox chkComConfiguracao = criarCheckBox("label.com_configuracao");
	private CheckBox chkComClasseUtil = criarCheckBox("label.com_classe_util");
	private CheckBox chkComException = criarCheckBox("label.com_exception");
	private CheckBox chkComProvedor = criarCheckBox("label.com_provedor");
	private CheckBox chkComListener = criarCheckBox("label.com_listener");
	private CheckBox chkComFichario = criarCheckBox("label.com_fichario");
	private CheckBox chkComDialogo = criarCheckBox("label.com_dialogo");
	private CheckBox chkComHandler = criarCheckBox("label.com_handler");
	private CheckBox chkComModelo = criarCheckBox("label.com_modelo");
	private TextField txtDiretorioRecursos = new TextField();
	private TextField txtDiretorioDestino = new TextField();
	private Button buttonGerar = new Button("label.gerar");
	private TextField txtPacotePlugin = new TextField();
	private TextField txtMinimPlugin = new TextField();
	private GeraPluginFormulario geraPluginFormulario;
	private TextField txtNomePlugin = new TextField();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private GeraPluginDialogo geraPluginDialogo;
	private TextArea textArea = new TextArea();
	private transient Config config;

	public GeraPluginContainer(Janela janela, Formulario formulario) {
		super(formulario);
		cmbIconePlugin.setRenderer(new ItemRenderer());
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public GeraPluginDialogo getGeraPluginDialogo() {
		return geraPluginDialogo;
	}

	public void setGeraPluginDialogo(GeraPluginDialogo geraPluginDialogo) {
		this.geraPluginDialogo = geraPluginDialogo;
		if (geraPluginDialogo != null) {
			geraPluginFormulario = null;
		}
	}

	public GeraPluginFormulario getGeraPluginFormulario() {
		return geraPluginFormulario;
	}

	public void setGeraPluginFormulario(GeraPluginFormulario geraPluginFormulario) {
		this.geraPluginFormulario = geraPluginFormulario;
		if (geraPluginFormulario != null) {
			geraPluginDialogo = null;
		}
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(GeraPluginMensagens.getString(chaveRotulo), false);
	}

	static Label criarLabel(String chaveRotulo) {
		Label label = new Label(GeraPluginMensagens.getString(chaveRotulo), false);
		label.setPreferredSize(new Dimension(150, 0));
		return label;
	}

	private Icone[] arrayObjetoIcone() {
		List<Icone> resp = new ArrayList<>();
		Class<?> klass = Icones.class;
		Field[] fields = klass.getDeclaredFields();
		for (Field field : fields) {
			Class<?> tipo = field.getType();
			if (tipo.equals(Icon.class)) {
				try {
					Icon icon = (Icon) field.get(Icones.class);
					resp.add(new Icone(field.getName(), icon));
				} catch (Exception e) {
					//
				}
			}
		}
		return resp.toArray(new Icone[0]);
	}

	class Icone {
		final String string;
		final Icon icon;

		public Icone(String string, Icon icon) {
			this.string = string;
			this.icon = icon;
		}

		public String getString() {
			return string;
		}

		public Icon getIcon() {
			return icon;
		}
	}

	class ItemRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			Icone icone = (Icone) value;
			setText(icone.string);
			setIcon(icone.icon);
			return this;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);

		Muro muro = new Muro();
		muro.camada(Muro.panelGrid(labelTextField("label.diretorio_destino", txtDiretorioDestino, criarButtonDir())));
		muro.camada(Muro.panelGrid(labelTextField("label.nome_plugin", txtNomePlugin)));
		muro.camada(Muro.panelGrid(labelTextField("label.nome_min_plugin", txtMinimPlugin)));
		muro.camada(Muro.panelGrid(labelTextField("label.pacote_plugin", txtPacotePlugin)));
		muro.camada(Muro.panelGrid(labelTextField("label.diretorio_recursos", txtDiretorioRecursos)));
		muro.camada(Muro.panelGrid(labelComboBox("label.icone_plugin", cmbIconePlugin)));
		muro.camada(Muro.panelGrid(chkComConfiguracao));
		muro.camada(Muro.panelGrid(chkComClasseUtil));
		muro.camada(Muro.panelGrid(chkComException));
		muro.camada(Muro.panelGrid(chkComProvedor));
		muro.camada(Muro.panelGrid(chkComListener));
		muro.camada(Muro.panelGrid(chkComFichario));
		muro.camada(Muro.panelGrid(chkComDialogo));
		muro.camada(Muro.panelGrid(chkComHandler));
		muro.camada(Muro.panelGrid(chkComModelo));
		muro.camada(buttonGerar);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, muro, textArea);
		SwingUtilities.invokeLater(() -> split.setDividerLocation(.77));
		add(BorderLayout.CENTER, split);

		buttonGerar.setIcon(Icones.EXECUTAR);
		buttonGerar.addActionListener(e -> gerarArquivos());
	}

	private void configurar() {
		txtNomePlugin.somenteLetras();
		txtMinimPlugin.somenteLetrasLower();
		txtDiretorioRecursos.somenteLetrasUpper();
	}

	private Panel labelTextField(String chaveRotulo, TextField textField) {
		return labelTextField(chaveRotulo, textField, null);
	}

	private Panel labelTextField(String chaveRotulo, TextField textField, JComponent comp) {
		Panel panel = new Panel();
		panel.add(BorderLayout.WEST, criarLabel(chaveRotulo));
		panel.add(BorderLayout.CENTER, textField);
		if (comp != null) {
			panel.add(BorderLayout.EAST, comp);
		}
		return panel;
	}

	private Panel labelComboBox(String chaveRotulo, JComboBox<?> combo) {
		Panel panel = new Panel();
		panel.add(BorderLayout.WEST, criarLabel(chaveRotulo));
		panel.add(BorderLayout.CENTER, combo);
		return panel;
	}

	private Button criarButtonDir() {
		Button button = new Button("label.diretorio");
		button.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int i = fileChooser.showOpenDialog(GeraPluginContainer.this);
			if (i == JFileChooser.APPROVE_OPTION) {
				File sel = fileChooser.getSelectedFile();
				txtDiretorioDestino.setText(sel.getAbsolutePath());
			}
		});
		return button;
	}

	private String mensagemObrigatoria(String key) {
		return GeraPluginMensagens.getString("erro.campo_obrigatorio", GeraPluginMensagens.getString(key));
	}

	private List<String> validar() {
		List<String> resp = new ArrayList<>();

		if (Util.isEmpty(txtDiretorioDestino.getText())) {
			resp.add(mensagemObrigatoria("label.diretorio_destino"));
		} else if (!new File(txtDiretorioDestino.getText()).isDirectory()) {
			resp.add(GeraPluginMensagens.getString("erro.diretorio_destino_invalido"));
		}

		if (Util.isEmpty(txtNomePlugin.getText())) {
			resp.add(mensagemObrigatoria("label.nome_plugin"));
		} else if (caracterInvalido(txtNomePlugin.getText())) {
			resp.add(GeraPluginMensagens.getString("erro.nome_plugin"));
		} else if (txtNomePlugin.getText().length() < 2) {
			resp.add(GeraPluginMensagens.getString("erro.nome_plugin_curto"));
		}

		if (Util.isEmpty(txtMinimPlugin.getText())) {
			resp.add(mensagemObrigatoria("label.nome_min_plugin"));
		} else if (caracterInvalido(txtMinimPlugin.getText())) {
			resp.add(GeraPluginMensagens.getString("erro.minim_plugin"));
		}

		if (Util.isEmpty(txtPacotePlugin.getText())) {
			resp.add(mensagemObrigatoria("label.pacote_plugin"));
		} else if (caracterInvalidoPacote(txtPacotePlugin.getText())) {
			resp.add(GeraPluginMensagens.getString("erro.pacote_plugin"));
		}

		if (!Util.isEmpty(txtDiretorioRecursos.getText()) && caracterInvalido(txtDiretorioRecursos.getText())) {
			resp.add(GeraPluginMensagens.getString("erro.diretorio_recursos"));
		}

		return resp;
	}

	private boolean caracterInvalido(String string) {
		for (char c : string.toCharArray()) {
			if (!caracter(c)) {
				return true;
			}
		}
		return false;
	}

	private boolean caracterInvalidoPacote(String string) {
		for (char c : string.toCharArray()) {
			if (!caracter2(c)) {
				return true;
			}
		}
		return false;
	}

	private boolean caracter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean caracter2(char c) {
		return caracter(c) || c == '.' || c == '_';
	}

	private void gerarArquivos() {
		List<String> erros = validar();
		textArea.limpar();
		if (erros.isEmpty()) {
			gerar();
		} else {
			for (String string : erros) {
				textArea.append(string + Constantes.QL);
			}
		}
	}

	private void gerar() {
		criarConfig();
		try {
			GeraPluginUtil.mensagensProp(config);
			GeraPluginUtil.constantes(config);
			GeraPluginUtil.formulario(config);
			GeraPluginUtil.mensagens(config);
			GeraPluginUtil.objeto(config);
			if (chkComDialogo.isSelected()) {
				GeraPluginUtil.containerDialogo(config);
				GeraPluginUtil.fabricaDialogo(config);
				GeraPluginUtil.dialogo(config);
			} else {
				GeraPluginUtil.container(config);
				GeraPluginUtil.fabrica(config);
			}
			if (chkComFichario.isSelected()) {
				GeraPluginUtil.fichario(config);
			}
			if (config.configuracao) {
				GeraPluginUtil.preferencias(config);
			}
			if (chkComHandler.isSelected()) {
				GeraPluginUtil.handler(config);
			}
			if (chkComProvedor.isSelected()) {
				GeraPluginUtil.provedor(config);
			}
			if (chkComModelo.isSelected()) {
				GeraPluginUtil.modelo(config);
			}
			if (chkComClasseUtil.isSelected()) {
				GeraPluginUtil.util(config);
			}
			if (chkComException.isSelected()) {
				GeraPluginUtil.exception(config);
			}
			if (chkComListener.isSelected()) {
				GeraPluginUtil.listener(config);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage(GeraPluginConstantes.PAINEL_GERA_PLUGIN, ex, GeraPluginContainer.this);
		}
	}

	private void criarConfig() {
		config = new Config();
		config.destino = new File(txtDiretorioDestino.getText());
		String nome = txtNomePlugin.getText();
		config.nomeMin = txtMinimPlugin.getText();
		config.pacote = trimP(txtPacotePlugin.getText());
		config.recurso = txtDiretorioRecursos.getText();
		config.nomeCapitalizado = Util.capitalize(nome);
		config.nomeDecapitalizado = nome.substring(0, 1).toLowerCase() + nome.substring(1);
		config.nomeCaixaAlta = nome.toUpperCase();
		config.nomeCaixaBaixa = nome.toLowerCase();
		Icone icone = (Icone) cmbIconePlugin.getSelectedItem();
		config.icone = icone.string;
		config.configuracao = chkComConfiguracao.isSelected();
		if (!Util.isEmpty(config.recurso) && !config.recurso.equals(config.nomeCaixaAlta)) {
			config.meta = "\tpublic static final String " + config.recurso + " = \"" + config.recurso.toLowerCase()
					+ "\";";
		}
	}

	private static String trimP(String string) {
		return Util.trim(Util.trim(string, '.', true), '.', false);
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
			if (formulario.excluirPagina(GeraPluginContainer.this)) {
				GeraPluginFormulario.criar(formulario, GeraPluginContainer.this);
			} else if (geraPluginDialogo != null) {
				geraPluginDialogo.excluirContainer();
				GeraPluginFormulario.criar(formulario, GeraPluginContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (geraPluginFormulario != null) {
				geraPluginFormulario.excluirContainer();
				formulario.adicionarPagina(GeraPluginContainer.this);
			} else if (geraPluginDialogo != null) {
				geraPluginDialogo.excluirContainer();
				formulario.adicionarPagina(GeraPluginContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (geraPluginDialogo != null) {
				geraPluginDialogo.excluirContainer();
			}
			GeraPluginFormulario.criar(formulario);
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
		return GeraPluginFabrica.class;
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
				return GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN_MIN);
			}

			@Override
			public String getTitulo() {
				return GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN);
			}

			@Override
			public String getHint() {
				return GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN);
			}

			@Override
			public Icon getIcone() {
				return Icones.CRIAR2;
			}
		};
	}
}