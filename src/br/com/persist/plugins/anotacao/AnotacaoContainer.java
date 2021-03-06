package br.com.persist.plugins.anotacao;

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
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.TextField;
import br.com.persist.componente.TextPane;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class AnotacaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final TextPane textArea = new TextPane();
	private final Toolbar toolbar = new Toolbar();
	private AnotacaoFormulario anotacaoFormulario;
	private AnotacaoDialogo anotacaoDialogo;
	private final File fileParent;
	private final File file;
	private File backup;

	public AnotacaoContainer(Janela janela, Formulario formulario, String conteudo) {
		super(formulario);
		file = new File(AnotacaoConstantes.ANOTACOES + Constantes.SEPARADOR + AnotacaoConstantes.ANOTACOES);
		fileParent = new File(AnotacaoConstantes.ANOTACOES);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo);
	}

	public AnotacaoDialogo getAnotacaoDialogo() {
		return anotacaoDialogo;
	}

	public void setAnotacaoDialogo(AnotacaoDialogo anotacaoDialogo) {
		this.anotacaoDialogo = anotacaoDialogo;
		if (anotacaoDialogo != null) {
			anotacaoFormulario = null;
		}
	}

	public AnotacaoFormulario getAnotacaoFormulario() {
		return anotacaoFormulario;
	}

	public void setAnotacaoFormulario(AnotacaoFormulario anotacaoFormulario) {
		this.anotacaoFormulario = anotacaoFormulario;
		if (anotacaoFormulario != null) {
			anotacaoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(textArea));
	}

	public String getConteudo() {
		return textArea.getText();
	}

	private void abrir(String conteudo) {
		if (!Util.estaVazio(conteudo)) {
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
				Util.stackTraceAndMessage(AnotacaoConstantes.PAINEL_ANOTACAO, ex, AnotacaoContainer.this);
			}
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final CheckBox chkPesquisaLocal = new CheckBox(true);
		private final TextField txtPesquisa = new TextField(35);
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
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(AnotacaoContainer.this)) {
				AnotacaoFormulario.criar(formulario, AnotacaoContainer.this);
			} else if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
				AnotacaoFormulario.criar(formulario, AnotacaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (anotacaoFormulario != null) {
				anotacaoFormulario.excluirContainer();
				formulario.adicionarPagina(AnotacaoContainer.this);
			} else if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
				formulario.adicionarPagina(AnotacaoContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
			}
			AnotacaoFormulario.criar(formulario, getConteudo());
		}

		@Override
		protected void abrirEmFormulario() {
			if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
			}
			AnotacaoFormulario.criar(formulario, Constantes.VAZIO);
		}

		void formularioVisivel() {
			buttonDestacar.estadoFormulario();
		}

		void paginaVisivel() {
			buttonDestacar.estadoFichario();
		}

		void dialogoVisivel() {
			buttonDestacar.estadoDialogo();
		}

		@Override
		protected void baixar() {
			abrir(null);
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(AnotacaoContainer.this, Constantes.TRES)) {
				salvarArquivo(backup != null ? backup : file);
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(AnotacaoConstantes.PAINEL_ANOTACAO, ex, AnotacaoContainer.this);
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
			if (Util.confirmar(AnotacaoContainer.this, "label.confirma_criar_backup")) {
				String nome = Util.gerarNomeBackup(fileParent, AnotacaoConstantes.ANOTACOES);
				salvarArquivo(new File(fileParent, nome));
			}
		}

		@Override
		protected void abrirBackup() {
			List<String> arquivos = Util.listarNomeBackup(fileParent, AnotacaoConstantes.ANOTACOES);
			if (arquivos.isEmpty()) {
				Util.mensagem(AnotacaoContainer.this, Mensagens.getString("msg.sem_arq_backup"));
				return;
			}
			Coletor coletor = new Coletor();
			SetLista.view(AnotacaoConstantes.ANOTACOES, arquivos, coletor, AnotacaoContainer.this,
					new SetLista.Config(true, true));
			if (coletor.size() == 1) {
				File arq = new File(fileParent, coletor.get(0));
				abrirArquivo(arq);
				backup = arq;
				setNomeBackup(coletor.get(0));
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.estaVazio(txtPesquisa.getText())) {
				if (chkPesquisaLocal.isSelected()) {
					selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
					selecao.selecionar(label);
					return;
				}
				List<String> arquivos = Util.listarNomeBackup(fileParent, AnotacaoConstantes.ANOTACOES);
				StringBuilder sb = new StringBuilder();
				for (String arquivo : arquivos) {
					String resultado = Util.pesquisar(new File(fileParent, arquivo), txtPesquisa.getText());
					if (!Util.estaVazio(resultado)) {
						if (sb.length() > 0) {
							sb.append(Constantes.QL);
						}
						sb.append(arquivo + Constantes.QL);
						sb.append(resultado);
					}
				}
				textArea.setText(sb.toString());
			}
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.paginaVisivel();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
	}

	public void dialogoVisivel() {
		toolbar.dialogoVisivel();
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return AnotacaoFabrica.class;
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
				return AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES_MIN);
			}

			@Override
			public String getTitulo() {
				return AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES);
			}

			@Override
			public String getHint() {
				return AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL4;
			}
		};
	}
}