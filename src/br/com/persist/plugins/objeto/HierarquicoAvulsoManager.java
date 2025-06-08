package br.com.persist.plugins.objeto;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.metadado.MetadadoConstantes;
import br.com.persist.plugins.metadado.MetadadoEvento;

public class HierarquicoAvulsoManager {
	final ObjetoSuperficie superficie;

	HierarquicoAvulsoManager(ObjetoSuperficie superficie) {
		this.superficie = superficie;
	}

	public void adicionarHierarquicoAvulsoAcima(Conexao conexao, Objeto objeto) throws AssistenciaException {
		Map<String, Object> args = new HashMap<>();
		args.put(MetadadoEvento.GET_METADADO_OBJETO, objeto.getTabela());
		superficie.getFormulario().processar(args);
		Metadado metadado = (Metadado) args.get(MetadadoConstantes.METADADO);
		if (metadado == null) {
			superficie.msgInexistenteMetadado(objeto);
		} else {
			if (conexao == null) {
				conexao = superficie.container.getConexaoPadrao();
			}
			criarObjetoHierarquicoAvulsoAcima(conexao, objeto, metadado);
		}
	}

	public void adicionarHierarquicoAvulsoAbaixo(Conexao conexao, Objeto objeto) throws AssistenciaException {
		Map<String, Object> args = new HashMap<>();
		args.put(MetadadoEvento.GET_METADADO_OBJETO, objeto.getTabela());
		superficie.getFormulario().processar(args);
		Metadado metadado = (Metadado) args.get(MetadadoConstantes.METADADO);
		if (metadado == null) {
			superficie.msgInexistenteMetadado(objeto);
		} else {
			if (conexao == null) {
				conexao = superficie.container.getConexaoPadrao();
			}
			criarObjetoHierarquicoAvulsoAbaixo(conexao, objeto, metadado);
		}
	}

	private void criarObjetoHierarquicoAvulsoAcima(Conexao conexao, Objeto principal, Metadado tabela)
			throws AssistenciaException {
		Exportacao exportacao = new Exportacao(superficie, principal, null, tabela.getPai());
		Metadado tabelaAvulsa = exportacao.getTabelaAvulsa();
		if (tabelaAvulsa != null) {
			exportacao.adicionarObjetoAvulso(tabelaAvulsa);
			exportacao.localizarObjetoAcima();
			superficie.destacar(conexao, ObjetoConstantes.TIPO_CONTAINER_PROPRIO, null);
		}
	}

	private void criarObjetoHierarquicoAvulsoAbaixo(Conexao conexao, Objeto principal, Metadado tabela)
			throws AssistenciaException {
		Exportacao exportacao = new Exportacao(superficie, principal, null, tabela.getPai());
		Metadado tabelaAvulsa = exportacao.getTabelaAvulsa();
		if (tabelaAvulsa != null) {
			exportacao.adicionarObjetoAvulso(tabelaAvulsa);
			exportacao.localizarObjetoAbaixo();
			superficie.destacar(conexao, ObjetoConstantes.TIPO_CONTAINER_PROPRIO, null);
		}
	}
}