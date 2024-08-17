package br.com.persist.plugins.ambiente;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class AmbienteFabrica extends AbstratoFabricaContainer {
	private static final Logger LOG = Logger.getGlobal();
	private AmbienteCache cache = new AmbienteCache();
	private String arquivoProperties;

	@Override
	public void inicializar() {
		Util.criarDiretorio(AmbienteConstantes.AMBIENTES);
		String arquivo = AmbienteConstantes.AMBIENTES + Constantes.SEPARADOR + AmbienteConstantes.AMBIENTES
				+ ".properties";
		File file = new File(arquivo);
		if (!file.exists()) {
			try {
				if (file.createNewFile()) {
					arquivoProperties = arquivo;
				}
			} catch (IOException e) {
				arquivoProperties = null;
			}
		} else {
			arquivoProperties = arquivo;
		}
		try {
			cache.inicializar(arquivoProperties);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new AmbientePaginaServico();
	}

	private class AmbientePaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			try {
				Ambiente ambiente = cache.get(stringPersistencia);
				return new AmbienteContainer(null, formulario, null, ambiente);
			} catch (ArgumentoException ex) {
				Util.mensagem(formulario, ex.getMessage());
				return null;
			}
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		for (Ambiente ambiente : cache.getAmbientes()) {
			lista.add(new MenuAmbiente(formulario, ambiente));
		}
		return lista;
	}

	private class MenuAmbiente extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAmbiente(Formulario formulario, Ambiente ambiente) {
			super(Constantes.LABEL_VAZIO, null);
			setText(ambiente.titulo);
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new AmbienteContainer(null, formulario, null, ambiente)));
			formularioAcao.setActionListener(e -> AmbienteFormulario.criar(formulario, null, ambiente));
			dialogoAcao.setActionListener(e -> AmbienteDialogo.criar(formulario, ambiente));
		}
	}
}