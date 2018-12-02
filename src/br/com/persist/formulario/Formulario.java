package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.dialogo.ConexaoDialogo;
import br.com.persist.dialogo.ConfigDialogo;
import br.com.persist.dialogo.FragmentoDialogo;
import br.com.persist.modelo.ConexaoModelo;
import br.com.persist.modelo.FragmentoModelo;
import br.com.persist.util.Acao;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private final Vector<Conexao> conexoes = new Vector<>();
	private final List<Objeto> copiados = new ArrayList<>();
	private final Fichario fichario = new Fichario();
	private File arquivo;
	private Color cor;

	public Formulario() {
		super(Mensagens.getString("label.persistencia"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		FormularioUtil.configMAC(this);
		setLayout(new BorderLayout());
		setJMenuBar(menuPrincipal);
		setSize(1000, 600);
		montarLayout();
		configurar();
	}

	public Color getCor() {
		return cor;
	}

	public void setCor(Color cor) {
		this.cor = cor;
	}

	public void destacar(Conexao conexao, Superficie superficie, boolean formDesktop) {
		fichario.destacar(this, conexao, superficie.getSelecionados(), formDesktop);
	}

	public void copiar(Superficie superficie) {
		copiados.clear();

		for (Objeto objeto : superficie.getSelecionados()) {
			copiados.add(objeto.clonar());
		}
	}

	public void colar(Superficie superficie, boolean b, int x, int y) {
		for (Objeto objeto : copiados) {
			Objeto clone = get(objeto, superficie);
			superficie.addObjeto(clone);

			if (b) {
				clone.x = x;
				clone.y = y;
			}
		}
	}

	private Objeto get(Objeto objeto, Superficie superficie) {
		Objeto o = objeto.clonar();
		o.x += Objeto.diametro;
		o.y += Objeto.diametro;
		o.setId(objeto.getId() + "-" + Objeto.getID());

		boolean contem = superficie.contem(o);

		while (contem) {
			o.setId(objeto.getId() + "-" + Objeto.novoID());
			contem = superficie.contem(o);
		}

		return o;
	}

	public Vector<Conexao> getConexoes() {
		return conexoes;
	}

	public Fichario getFichario() {
		return fichario;
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, fichario);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				FormularioUtil.aparenciaPadrao(menuPrincipal.menuLAF, "Nimbus");
				atualizarFragmentos();
				atualizarConexoes();
			}

			public void windowClosing(WindowEvent e) {
				FormularioUtil.fechar(Formulario.this);
			};
		});
	}

	public void atualizarFragmentos() {
		try {
			FragmentoModelo.inicializar();
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ATUALIZAR FRAGMENTOS", ex, this);
		}
	}

	public void atualizarConexoes() {
		ConexaoModelo modelo = new ConexaoModelo();
		conexoes.clear();

		try {
			modelo.abrir();
			for (Conexao conexao : modelo.getConexoes()) {
				conexoes.add(conexao);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ATUALIZAR CONEXOES", ex, this);
		}
	}

	private class MenuPrincipal extends JMenuBar {
		private static final long serialVersionUID = 1L;
		final Menu menuArquivo = new Menu("label.arquivo");
		final Menu menuLAF = new Menu("label.aparencia");

		MenuPrincipal() {
			FormularioUtil.menuAparencia(Formulario.this, menuLAF);
			menuArquivo.add(new MenuItem(new NovoAcao()));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new DesktopAcao()));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new FormularioAcao()));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new AbrirFormularioAcao(true)));
			menuArquivo.add(new MenuItem(new AbrirFicharioAcao(true)));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new ConexaoAcao(true)));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new FragmentoAcao(true)));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new ConfigAcao()));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new FecharAcao(true)));
			add(menuArquivo);
			add(menuLAF);
		}

		private class NovoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public NovoAcao() {
				super(true, "label.novo", Icones.CUBO);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				fichario.novo(Formulario.this);
			}
		}

		private class DesktopAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public DesktopAcao() {
				super(true, "label.desktop", Icones.CUBO2);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				fichario.novoDesktop(Formulario.this);
			}
		}

		private class FormularioAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public FormularioAcao() {
				super(true, "label.formulario", Icones.PANEL);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				new FormularioDesktop(Formulario.this);
			}
		}

		private class ConexaoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public ConexaoAcao(boolean menu) {
				super(menu, "label.conexao", Icones.BANCO);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				new ConexaoDialogo(Formulario.this);
			}
		}

		private class FragmentoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public FragmentoAcao(boolean menu) {
				super(menu, "label.fragmento", Icones.FRAGMENTO);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				FragmentoDialogo dialogo = new FragmentoDialogo(Formulario.this, null);
				dialogo.setVisible(true);
			}
		}

		private class ConfigAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public ConfigAcao() {
				super(true, "label.configuracoes", Icones.CONFIG);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigDialogo(Formulario.this);
			}
		}

		private class FecharAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public FecharAcao(boolean menu) {
				super(menu, "label.fechar", Icones.SAIR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				FormularioUtil.fechar(Formulario.this);
				System.exit(0);
			}
		}

		private class AbrirFormularioAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public AbrirFormularioAcao(boolean menu) {
				super(menu, "label.abrir_formulario", Icones.ABRIR);
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(".");
				if (arquivo != null) {
					fileChooser.setCurrentDirectory(arquivo);
				}
				fileChooser.setMultiSelectionEnabled(true);
				int opcao = fileChooser.showOpenDialog(Formulario.this);

				if (opcao == JFileChooser.APPROVE_OPTION) {
					File files[] = fileChooser.getSelectedFiles();

					if (files != null) {
						for (File file : files) {
							try {
								arquivo = file.getParentFile();
								List<Relacao> relacoes = new ArrayList<>();
								List<Objeto> objetos = new ArrayList<>();
								List<Form> forms = new ArrayList<>();
								Dimension d = XML.processar(file, objetos, relacoes, forms);
								fichario.abrirFormulario(Formulario.this, file, objetos, relacoes, forms, d);
							} catch (Exception ex) {
								Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, Formulario.this);
							}
						}
					}
				}
			}
		}

		private class AbrirFicharioAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public AbrirFicharioAcao(boolean menu) {
				super(menu, "label.abrir_fichario", Icones.ABRIR);
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(".");
				if (arquivo != null) {
					fileChooser.setCurrentDirectory(arquivo);
				}
				fileChooser.setMultiSelectionEnabled(true);
				int opcao = fileChooser.showOpenDialog(Formulario.this);

				if (opcao == JFileChooser.APPROVE_OPTION) {
					File files[] = fileChooser.getSelectedFiles();

					if (files != null) {
						for (File file : files) {
							try {
								arquivo = file.getParentFile();
								List<Relacao> relacoes = new ArrayList<>();
								List<Objeto> objetos = new ArrayList<>();
								List<Form> forms = new ArrayList<>();
								Dimension d = XML.processar(file, objetos, relacoes, forms);
								fichario.abrir(Formulario.this, file, objetos, relacoes, forms, d);
							} catch (Exception ex) {
								Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, Formulario.this);
							}
						}
					}
				}
			}
		}
	}
}