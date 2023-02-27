package br.com.persist.plugins.mapa.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import br.com.persist.plugins.mapa.Atributo;
import br.com.persist.plugins.mapa.Container;
import br.com.persist.plugins.mapa.Objeto;
import br.com.persist.plugins.mapa.config.Config;
import br.com.persist.plugins.mapa.config.MontaObjeto;
import br.com.persist.plugins.mapa.forma.Forma;
import br.com.persist.plugins.mapa.organiza.Organizador;
import br.com.persist.plugins.mapa.organiza.OrganizadorBola;
import br.com.persist.plugins.mapa.organiza.OrganizadorCircular;
import br.com.persist.plugins.mapa.organiza.OrganizadorRandomico;
import br.com.persist.plugins.mapa.organiza.OrganizadorSequencia;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private JCheckBoxMenuItem checkBoxMenuItemDesenharObjetoCentral = new JCheckBoxMenuItem("Desenhar Objeto do Centro",
			true);
	private JCheckBoxMenuItem checkBoxMenuItemDesenharAssociacoes = new JCheckBoxMenuItem("Desenhar Associa��es", true);
	private JCheckBoxMenuItem checkBoxMenuItemDesenharAtributos = new JCheckBoxMenuItem("Desenhar Atributos", true);
	private JCheckBoxMenuItem checkBoxMenuItemDesenharGrade = new JCheckBoxMenuItem("Desenhar Grade", true);
	private JCheckBoxMenuItem checkBoxMenuItemGirar = new JCheckBoxMenuItem("Girar");
	private JMenuItem menuItemVelocidade = new JMenuItem("Velocidade");
	private JMenuItem menuItemLocalizar = new JMenuItem("Localizar");
	private JPanel painelMenu = new JPanel(new GridLayout(0, 1));
	private JMenuItem menuItemFechar = new JMenuItem("Fechar");
	private JMenuItem menuItemAbrir = new JMenuItem("Abrir");
	private PainelRaiz painelRaiz = new PainelRaiz();
	private JMenu menuArquivo = new JMenu("Arquivo");
	private JMenuBar menuBarBarra = new JMenuBar();

	public Formulario(int largura, int altura) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(largura, altura);
		setLocationRelativeTo(null);
		montarLayout();
		montarMenu();
		registrarEvento();
	}

	private String getDirPadraoFileChooser() {
		try {
			return System.getProperty("user.home");
		} catch (Exception ex) {
			return null;
		}
	}

	private void registrarEvento() {
		menuItemFechar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		checkBoxMenuItemGirar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				painelRaiz.rotacionar = checkBoxMenuItemGirar.isSelected();
			}
		});
		checkBoxMenuItemDesenharAssociacoes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				painelRaiz.desenharAssociacoes = checkBoxMenuItemDesenharAssociacoes.isSelected();
				painelRaiz.repaint();
			}
		});
		checkBoxMenuItemDesenharGrade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				painelRaiz.desenharGrade = checkBoxMenuItemDesenharGrade.isSelected();
				painelRaiz.repaint();
			}
		});
		checkBoxMenuItemDesenharObjetoCentral.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Forma.desenharObjetoCentro = checkBoxMenuItemDesenharObjetoCentral.isSelected();
				painelRaiz.repaint();
			}
		});
		checkBoxMenuItemDesenharAtributos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Forma.desenharAtributos = checkBoxMenuItemDesenharAtributos.isSelected();
				painelRaiz.repaint();
			}
		});
		menuItemAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(getDirPadraoFileChooser());
				fileChooser.setFileFilter(new FileFilter() {
					public String getDescription() {
						return "Arquivo de mapeamento (*.xml)";
					}

					public boolean accept(File f) {
						return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
					}
				});
				int i = fileChooser.showOpenDialog(Formulario.this);
				if (i == JFileChooser.APPROVE_OPTION) {
					carregar(fileChooser.getSelectedFile());
				}
			}
		});
		menuItemVelocidade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = JOptionPane.showInputDialog(Formulario.this, "Velocidade", "" + Config.INTERVALO_ROTACAO);
				if (s != null) {
					try {
						int i = Integer.parseInt(s);
						if (i >= Config.VELOCIDADE_MINIMA_ROTACAO && i <= Config.VELOCIDADE_MAXIMA_ROTACAO) {
							// Config.INTERVALO_ROTACAO = i;
						}
					} catch (Exception ex) {
					}
				}
			}
		});
		menuItemLocalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = JOptionPane.showInputDialog(Formulario.this, "Nome");
				painelRaiz.localizar(s);
			}
		});
	}

	private void carregar(File file) {
		painelMenu.removeAll();
		try {
			Objeto raiz = MontaObjeto.montarObjeto(file);
			if (raiz != null) {

				Atributo atributo = raiz.getAtributo("diametroObjeto");
				if (atributo != null) {
					// Config.DIAMETRO_OBJETO =
					// Integer.parseInt(atributo.getValor());
				}

				atributo = raiz.getAtributo("diametroObjetoCentro");
				if (atributo != null) {
					// Config.DIAMETRO_OBJETO_CENTRO =
					// Integer.parseInt(atributo.getValor());
				}

				atributo = raiz.getAtributo("distanciaCentro");
				if (atributo != null) {
					// Config.DISTANCIA_CENTRO =
					// Integer.parseInt(atributo.getValor());
				}

				atributo = raiz.getAtributo("intervaloRotacao");
				if (atributo != null) {
					// Config.INTERVALO_ROTACAO =
					// Integer.parseInt(atributo.getValor());
				}

				raiz.resolverReferencias();

				for (Container objeto : raiz.getFilhos()) {
					Objeto obj = (Objeto) objeto;
					if (obj.getMenu() != null) {
						painelMenu.add(new LabelMenu(obj.getMenu(), obj, painelRaiz));
					}
				}

				painelRaiz.montar(raiz);
				painelRaiz.continuar();
			}
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			JScrollPane scroll = new JScrollPane(new JTextArea(sw.toString()));
			scroll.setPreferredSize(new Dimension(getWidth() - 100, getHeight() - 200));
			JOptionPane.showMessageDialog(this, scroll);
		}
	}

	private void montarMenu() {
		setJMenuBar(menuBarBarra);
		menuBarBarra.add(menuArquivo);
		menuArquivo.addSeparator();
		menuArquivo.add(checkBoxMenuItemDesenharObjetoCentral);
		menuArquivo.add(checkBoxMenuItemDesenharAssociacoes);
		menuArquivo.add(checkBoxMenuItemDesenharAtributos);
		menuArquivo.add(checkBoxMenuItemDesenharGrade);
		menuArquivo.add(checkBoxMenuItemGirar);
		menuArquivo.add(menuItemAbrir);
		menuArquivo.add(menuItemVelocidade);
		menuArquivo.add(menuItemLocalizar);
		menuArquivo.addSeparator();
		menuArquivo.add(menuItemFechar);
		menuItemLocalizar.setAccelerator(KeyStroke.getKeyStroke('F', KeyEvent.CTRL_MASK));
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelRaiz);
		add(BorderLayout.EAST, painelMenu);
	}

	private static Map<String, Organizador> organizadores = new HashMap<String, Organizador>();

	static {
		organizadores.put("sequencia", new OrganizadorSequencia());
		organizadores.put("randomico", new OrganizadorRandomico());
		organizadores.put("circular", new OrganizadorCircular());
		organizadores.put("bola", new OrganizadorBola());
	}

	protected static Organizador getOrganizador(String nome) {
		if (nome != null) {
			nome = nome.trim().toLowerCase();
		}
		return organizadores.get(nome);
	}
}