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

import javax.swing.Icon;
import javax.swing.JComboBox;

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
	private CheckBox chkComConfiguracao = criarCheckBox("label.com_configuracao");
	private JComboBox<Object> cmbIconePlugin = new JComboBox<>(arrayNomeIcones());
	private CheckBox chkComClasseUtil = criarCheckBox("label.com_classe_util");
	private CheckBox chkComException = criarCheckBox("label.com_exception");
	private CheckBox chkComProvedor = criarCheckBox("label.com_provedor");
	private CheckBox chkComListener = criarCheckBox("label.com_listener");
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
		toolbar.ini(janela);
		montarLayout();
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

	private Object[] arrayNomeIcones() {
		List<String> resp = new ArrayList<>();
		Class<?> klass = Icones.class;
		Field[] fields = klass.getDeclaredFields();
		for (Field field : fields) {
			Class<?> tipo = field.getType();
			if (tipo.equals(Icon.class)) {
				resp.add(field.getName());
			}
		}
		return resp.toArray();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);

		Muro muro = new Muro();
		muro.camada(textArea);
		muro.camada(Muro.panelGrid(labelTextField("label.nome_plugin", txtNomePlugin)));
		muro.camada(Muro.panelGrid(labelTextField("label.nome_min_plugin", txtMinimPlugin)));
		muro.camada(Muro.panelGrid(labelTextField("label.diretorio_destino", txtDiretorioDestino)));
		muro.camada(Muro.panelGrid(labelTextField("label.pacote_plugin", txtPacotePlugin)));
		muro.camada(Muro.panelGrid(labelTextField("label.diretorio_recursos", txtDiretorioRecursos)));
		muro.camada(Muro.panelGrid(labelComboBox("label.icone_plugin", cmbIconePlugin)));
		muro.camada(Muro.panelGrid(chkComConfiguracao));
		muro.camada(Muro.panelGrid(chkComClasseUtil));
		muro.camada(Muro.panelGrid(chkComException));
		muro.camada(Muro.panelGrid(chkComProvedor));
		muro.camada(Muro.panelGrid(chkComListener));
		muro.camada(Muro.panelGrid(chkComDialogo));
		muro.camada(Muro.panelGrid(chkComHandler));
		muro.camada(Muro.panelGrid(chkComModelo));
		muro.camada(buttonGerar);
		add(BorderLayout.CENTER, muro);

		buttonGerar.setIcon(Icones.EXECUTAR);
		buttonGerar.addActionListener(e -> gerarArquivos());
	}

	private Panel labelTextField(String chaveRotulo, TextField textField) {
		Panel panel = new Panel();
		panel.add(BorderLayout.WEST, criarLabel(chaveRotulo));
		panel.add(BorderLayout.CENTER, textField);
		return panel;
	}

	private Panel labelComboBox(String chaveRotulo, JComboBox<?> combo) {
		Panel panel = new Panel();
		panel.add(BorderLayout.WEST, criarLabel(chaveRotulo));
		panel.add(BorderLayout.CENTER, combo);
		return panel;
	}

	private List<String> validar() {
		List<String> resp = new ArrayList<>();
		if (Util.estaVazio(txtNomePlugin.getText()) || caracterInvalido(txtNomePlugin.getText())) {
			resp.add(GeraPluginMensagens.getString("erro.nome_plugin"));
		} else if (txtNomePlugin.getText().length() < 2) {
			resp.add(GeraPluginMensagens.getString("erro.nome_plugin_curto"));
		}
		if (Util.estaVazio(txtMinimPlugin.getText()) || caracterInvalido(txtMinimPlugin.getText())) {
			resp.add(GeraPluginMensagens.getString("erro.minim_plugin"));
		}
		if (Util.estaVazio(txtDiretorioDestino.getText())) {
			resp.add(GeraPluginMensagens.getString("erro.diretorio_destino_vazio"));
		} else if (!new File(txtDiretorioDestino.getText()).isDirectory()) {
			resp.add(GeraPluginMensagens.getString("erro.diretorio_destino_invalido"));
		}
		if (Util.estaVazio(txtPacotePlugin.getText()) || caracterInvalidoPacote(txtPacotePlugin.getText())) {
			resp.add(GeraPluginMensagens.getString("erro.pacote_plugin"));
		}
		if (!Util.estaVazio(txtDiretorioRecursos.getText()) && caracterInvalido(txtDiretorioRecursos.getText())) {
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
		return caracter(c) || c == '.';
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
			GeraPluginUtil.mensagens(config);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(GeraPluginConstantes.PAINEL_GERA_PLUGIN, ex, GeraPluginContainer.this);
		}
	}

	private void criarConfig() {
		config = new Config();
		config.destino = new File(txtDiretorioDestino.getText());
		String nome = txtNomePlugin.getText();
		config.nomeMin = txtMinimPlugin.getText();
		config.pacote = txtPacotePlugin.getText();
		config.recurso = txtDiretorioRecursos.getText();
		config.nomeCap = nome.substring(0, 1).toUpperCase() + nome.substring(1);
		config.nomeCapUpper = config.nomeCap.toUpperCase();
		config.nomeDecap = nome.substring(0, 1).toLowerCase() + nome.substring(1);
		config.nomeDecapLower = config.nomeDecap.toLowerCase();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		protected void ini(Janela janela) {
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

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
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