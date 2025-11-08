package br.com.persist.plugins.objeto;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.internal.InternalContainer;
import br.com.persist.plugins.objeto.internal.InternalTransferidor;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class ObjetoSuperficieDestacar {
	private static final Logger LOG = Logger.getGlobal();

	private ObjetoSuperficieDestacar() {
	}

	public static void destacarDesktopFormulario(List<Objeto> objetos, Conexao conexao, InternalConfig config,
			Formulario formulario) {
		DesktopFormulario form = DesktopFormulario.criar(formulario);
		int x = 10;
		int y = 10;
		for (Objeto objeto : objetos) {
			if (!Util.isEmpty(objeto.getTabela())) {
				if (objeto.getReferenciaPesquisa() != null) {
					objeto.getReferenciaPesquisa().setValidoInvisibilidade(true);
				}
				Object[] array = InternalTransferidor.criarArray(conexao, objeto);
				form.getDesktop().montarEAdicionarInternalFormulario(array, new Point(x, y), false, config);
				x += 25;
				y += 25;
			}
		}
		form.setVisible(!Util.isMac());
		form.setVisible(true);
	}

	public static void destacarDesktopDialogo(List<Objeto> objetos, Conexao conexao, InternalConfig config,
			Formulario formulario) {
		DesktopDialogo form = DesktopDialogo.criar(formulario);
		int x = 10;
		int y = 10;
		for (Objeto objeto : objetos) {
			if (!Util.isEmpty(objeto.getTabela())) {
				if (objeto.getReferenciaPesquisa() != null) {
					objeto.getReferenciaPesquisa().setValidoInvisibilidade(true);
				}
				Object[] array = InternalTransferidor.criarArray(conexao, objeto);
				form.getDesktop().montarEAdicionarInternalFormulario(array, new Point(x, y), false, config);
				x += 25;
				y += 25;
			}
		}
		form.setVisible(true);
	}

	public static void destacarDeskopPagina(List<Objeto> objetos, Conexao conexao, InternalConfig config,
			Formulario formulario) {
		Desktop desktop = new Desktop(formulario, false);
		int x = 10;
		int y = 10;
		for (Objeto objeto : objetos) {
			if (!Util.isEmpty(objeto.getTabela())) {
				if (objeto.getReferenciaPesquisa() != null) {
					objeto.getReferenciaPesquisa().setValidoInvisibilidade(true);
				}
				Object[] array = InternalTransferidor.criarArray(conexao, objeto);
				desktop.montarEAdicionarInternalFormulario(array, new Point(x, y), false, config);
				x += 25;
				y += 25;
			}
		}
		formulario.adicionarPagina(desktop);
	}

	public static void destacarObjetoPagina(List<Objeto> listaObjetos, Conexao conexao, Formulario formulario) {
		for (Objeto objeto : listaObjetos) {
			if (!Util.isEmpty(objeto.getTabela())) {
				Desktop.setComplemento(conexao, objeto);
				formulario.adicionarPagina(new InternalContainer(null, conexao, objeto, false));
				objeto.processarTemp();
			}
		}
	}

	public static void destacarPropriaSuperficie(ObjetoSuperficie superficie, List<Objeto> objetos, Conexao conexao,
			InternalConfig config) {
		Variavel variavelDeltaX = VariavelProvedor.getVariavel(ObjetoConstantes.DELTA_X_AJUSTE_FORM_OBJETO);
		Variavel variavelDeltaY = VariavelProvedor.getVariavel(ObjetoConstantes.DELTA_Y_AJUSTE_FORM_OBJETO);
		boolean salvar = false;
		try {
			if (variavelDeltaX == null) {
				variavelDeltaX = new Variavel(ObjetoConstantes.DELTA_X_AJUSTE_FORM_OBJETO,
						Constantes.VAZIO + Constantes.TRINTA);
				VariavelProvedor.adicionar(variavelDeltaX);
				salvar = true;
			}
			if (variavelDeltaY == null) {
				variavelDeltaY = new Variavel(ObjetoConstantes.DELTA_Y_AJUSTE_FORM_OBJETO,
						Constantes.VAZIO + Constantes.TRINTA);
				VariavelProvedor.adicionar(variavelDeltaY);
				salvar = true;
			}
		} catch (ArgumentoException ex) {
			Util.mensagem(superficie, ex.getMessage());
			return;
		}
		checarAtualizarVariavelProvedorSuperficie(salvar);
		int x = variavelDeltaX.getInteiro(Constantes.TRINTA);
		int y = variavelDeltaY.getInteiro(Constantes.TRINTA);
		processarInternalFormulario(superficie, objetos, conexao, config, x, y);
		superficie.repaint();
	}

	private static void processarInternalFormulario(ObjetoSuperficie superficie, List<Objeto> objetos, Conexao conexao,
			InternalConfig config, int x, int y) {
		Variavel variavelLargura = VariavelProvedor.getVariavel(ObjetoConstantes.DESTACAR_PROPRIO_LARGURA_INTERNAL);
		Variavel variavelAltura = VariavelProvedor.getVariavel(ObjetoConstantes.DESTACAR_PROPRIO_ALTURA_INTERNAL);
		boolean salvar = false;
		try {
			if (variavelLargura == null) {
				variavelLargura = new Variavel(ObjetoConstantes.DESTACAR_PROPRIO_LARGURA_INTERNAL,
						Constantes.VAZIO + Constantes.QUATROCENTOS);
				VariavelProvedor.adicionar(variavelLargura);
				salvar = true;
			}
			if (variavelAltura == null) {
				variavelAltura = new Variavel(ObjetoConstantes.DESTACAR_PROPRIO_ALTURA_INTERNAL,
						Constantes.VAZIO + Constantes.DUZENTOS);
				VariavelProvedor.adicionar(variavelAltura);
				salvar = true;
			}
		} catch (ArgumentoException ex) {
			Util.mensagem(superficie, ex.getMessage());
			return;
		}
		checarAtualizarVariavelProvedorSuperficie(salvar);
		int largura = variavelLargura.getInteiro(Constantes.QUATROCENTOS);
		int altura = variavelAltura.getInteiro(Constantes.DUZENTOS);
		Dimension dimension = new Dimension(largura, altura);
		for (Objeto objeto : objetos) {
			if (!Util.isEmpty(objeto.getTabela())) {
				Object[] array = InternalTransferidor.criarArray(conexao, objeto, dimension);
				superficie.montarEAdicionarInternalFormulario(array, new Point(objeto.getX() + x, objeto.getY() + y),
						false, config);
			}
		}
	}

	private static void checarAtualizarVariavelProvedorSuperficie(boolean salvar) {
		if (salvar) {
			try {
				VariavelProvedor.salvar();
				VariavelProvedor.inicializar();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
	}
}