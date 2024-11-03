package br.com.persist.plugins.ouvinte;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.TextArea;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class OuvinteContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private OuvinteFormulario ouvinteFormulario;
	private TextArea textArea = new TextArea();
	private File arquivo;

	public OuvinteContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public OuvinteFormulario getOuvinteFormulario() {
		return ouvinteFormulario;
	}

	public void setOuvinteFormulario(OuvinteFormulario ouvinteFormulario) {
		this.ouvinteFormulario = ouvinteFormulario;
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		String string = (String) args.get(OuvinteEvento.GET_STRING);
		if (!Util.isEmpty(string)) {
			textArea.setText(string);
			args.put(OuvinteEvento.GET_RESULT, OuvinteEvento.GET_RESULT);
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, LIMPAR, SALVAR);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(OuvinteContainer.this)) {
				OuvinteFormulario.criar(formulario, OuvinteContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (ouvinteFormulario != null) {
				ouvinteFormulario.excluirContainer();
				formulario.adicionarPagina(OuvinteContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			OuvinteFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		@Override
		protected void salvar() {
			JFileChooser fileChooser = Util.criarFileChooser(arquivo, false);
			int opcao = fileChooser.showSaveDialog(formulario);
			if (opcao == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					salvar(file);
					arquivo = file;
					setTitulo();
				}
			}
		}

		private void salvar(File file) {
			try (PrintWriter pw = new PrintWriter(file)) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception e) {
				Util.mensagem(OuvinteContainer.this, e.getMessage());
			}
		}

		private void setTitulo() {
			if (ouvinteFormulario == null) {
				int indice = formulario.getIndicePagina(OuvinteContainer.this);
				if (indice != -1) {
					formulario.setHintTitlePagina(indice, arquivo.getAbsolutePath(), arquivo.getName());
				}
			} else {
				ouvinteFormulario.setTitle(arquivo.getName());
			}
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
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return OuvinteFabrica.class;
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
				return OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE_MIN);
			}

			@Override
			public String getTitulo() {
				return OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE);
			}

			@Override
			public String getHint() {
				return OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL;
			}
		};
	}
}