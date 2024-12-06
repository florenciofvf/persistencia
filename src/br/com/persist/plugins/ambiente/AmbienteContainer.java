package br.com.persist.plugins.ambiente;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BACKUP;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.CLONAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class AmbienteContainer extends AbstratoContainer {
	private final TextEditor textArea = new TextEditor();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private AmbienteFormulario ambienteFormulario;
	private final transient Ambiente ambiente;
	private AmbienteDialogo ambienteDialogo;
	private final File fileParent;
	private final File file;
	private File backup;

	public AmbienteContainer(Janela janela, Formulario formulario, String conteudo, Ambiente ambiente) {
		super(formulario);
		file = new File(AmbienteConstantes.AMBIENTES + Constantes.SEPARADOR + ambiente.chave);
		fileParent = new File(AmbienteConstantes.AMBIENTES);
		this.ambiente = ambiente;
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo);
	}

	public Ambiente getAmbiente() {
		return ambiente;
	}

	public AmbienteDialogo getAmbienteDialogo() {
		return ambienteDialogo;
	}

	public void setAmbienteDialogo(AmbienteDialogo ambienteDialogo) {
		this.ambienteDialogo = ambienteDialogo;
		if (ambienteDialogo != null) {
			ambienteFormulario = null;
		}
	}

	public AmbienteFormulario getAmbienteFormulario() {
		return ambienteFormulario;
	}

	public void setAmbienteFormulario(AmbienteFormulario ambienteFormulario) {
		this.ambienteFormulario = ambienteFormulario;
		if (ambienteFormulario != null) {
			ambienteDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		ScrollPane scrollPane = new ScrollPane(textArea);
		scrollPane.setRowHeaderView(new TextEditorLine(textArea));
		add(BorderLayout.CENTER, scrollPane);
	}

	public String getConteudo() {
		return textArea.getText();
	}

	private void abrir(String conteudo) {
		if (!Util.isEmpty(conteudo)) {
			textArea.setText(conteudo);
			return;
		}
		abrirArquivo(file);
		backup = null;
	}

	private void abrirArquivo(File file) {
		toolbar.limparNomeBackup();
		textArea.limpar();
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					textArea.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(AmbienteConstantes.PAINEL_AMBIENTE, ex, AmbienteContainer.this);
			}
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action pesquisaGeralAcao = Action.acaoIcon(AmbienteMensagens.getString("label.pesquisa_geral"),
				Icones.BOLA_AMARELA);
		private final CheckBox chkPesquisaLocal = new CheckBox(true);
		private final TextField txtPesquisa = new TextField(35);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR, BACKUP);
			chkPesquisaLocal.setToolTipText(Mensagens.getString("label.pesquisa_local"));
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(chkPesquisaLocal);
			add(label);
			addButton(pesquisaGeralAcao);
			pesquisaGeralAcao.setActionListener(e -> pesquisarGeral());
		}

		private void pesquisarGeral() {
			Object resp = Util.getValorInputDialog(AmbienteContainer.this, "label.atencao",
					AmbienteMensagens.getString("label.pesquisa_geral"), null);
			if (resp != null && !Util.isEmpty(resp.toString())) {
				pesquisaGeral(resp.toString());
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(AmbienteContainer.this)) {
				AmbienteFormulario.criar(formulario, AmbienteContainer.this);
			} else if (ambienteDialogo != null) {
				ambienteDialogo.excluirContainer();
				AmbienteFormulario.criar(formulario, AmbienteContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (ambienteFormulario != null) {
				ambienteFormulario.excluirContainer();
				formulario.adicionarPagina(AmbienteContainer.this);
			} else if (ambienteDialogo != null) {
				ambienteDialogo.excluirContainer();
				formulario.adicionarPagina(AmbienteContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (ambienteDialogo != null) {
				ambienteDialogo.excluirContainer();
			}
			AmbienteFormulario.criar(formulario, getConteudo(), ambiente);
		}

		@Override
		protected void abrirEmFormulario() {
			if (ambienteDialogo != null) {
				ambienteDialogo.excluirContainer();
			}
			AmbienteFormulario.criar(formulario, null, ambiente);
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

		@Override
		protected void baixar() {
			abrir(null);
			selecao = null;
			label.limpar();
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(AmbienteContainer.this, Constantes.TRES)) {
				salvarArquivo(backup != null ? backup : file);
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(AmbienteConstantes.PAINEL_AMBIENTE, ex, AmbienteContainer.this);
			}
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textArea, numeros, letras);
		}

		@Override
		protected void criarBackup() {
			if (Util.confirmar(AmbienteContainer.this, "label.confirma_criar_backup")) {
				String nome = Util.gerarNomeBackup(fileParent, ambiente.chave);
				salvarArquivo(new File(fileParent, nome));
			}
		}

		@Override
		protected void abrirBackup() {
			List<String> arquivos = Util.listarNomeBackup(fileParent, ambiente.chave);
			if (arquivos.isEmpty()) {
				Util.mensagem(AmbienteContainer.this, Mensagens.getString("msg.sem_arq_backup"));
				return;
			}
			Coletor coletor = new Coletor();
			SetLista.view(AmbienteConstantes.AMBIENTES, arquivos, coletor, AmbienteContainer.this,
					new SetLista.Config(true, true));
			if (coletor.size() == 1) {
				File arq = new File(fileParent, coletor.get(0));
				abrirArquivo(arq);
				selecao = null;
				backup = arq;
				setNomeBackup(coletor.get(0));
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPesquisaLocal.isSelected()) {
					selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
					selecao.selecionar(label);
					return;
				}
				List<String> arquivos = Util.listarNomeBackup(fileParent, ambiente.chave);
				StringBuilder sb = new StringBuilder();
				for (String arquivo : arquivos) {
					String resultado = Util.pesquisar(new File(fileParent, arquivo), txtPesquisa.getText());
					if (!Util.isEmpty(resultado)) {
						if (sb.length() > 0) {
							sb.append(Constantes.QL);
						}
						sb.append(arquivo + Constantes.QL);
						sb.append(resultado);
					}
				}
				textArea.setText(sb.toString());
				selecao = null;
			} else {
				label.limpar();
			}
		}

		private void pesquisaGeral(String pesquisado) {
			File[] arquivos = fileParent.listFiles();
			if (arquivos == null || Util.isEmpty(pesquisado)) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (File arquivo : arquivos) {
				String resultado = Util.pesquisar(arquivo, pesquisado);
				if (!Util.isEmpty(resultado)) {
					if (sb.length() > 0) {
						sb.append(Constantes.QL);
					}
					sb.append(arquivo.getName() + Constantes.QL);
					sb.append(resultado);
				}
			}
			textArea.setText(sb.toString());
			selecao = null;
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
		return ambiente.chave;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return AmbienteFabrica.class;
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
				return ambiente.tituloMin;
			}

			@Override
			public String getTitulo() {
				return ambiente.titulo;
			}

			@Override
			public String getHint() {
				return ambiente.titulo;
			}

			@Override
			public Icon getIcone() {
				return Icones.BOLA_VERDE;
			}
		};
	}
}