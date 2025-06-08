package br.com.persist.plugins.objeto;

import java.awt.Point;
import java.util.List;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.SetLista.Coletor;

public class HierarquicoVisivelManager {
	final ObjetoSuperficie superficie;

	HierarquicoVisivelManager(ObjetoSuperficie superficie) {
		this.superficie = superficie;
	}

	public Coletor getColetorFormsInvisiveis() {
		Coletor coletor = new Coletor();
		List<String> lista = ObjetoSuperficieUtil.getListaFormulariosInvisiveis(superficie);
		if (lista.isEmpty()) {
			Util.mensagem(superficie.getFormulario(), ObjetoMensagens.getString("msg.nenhum_form_invisivel"));
			return coletor;
		}
		SetLista.view(ObjetoMensagens.getString("label.forms_invisiveis"), lista, coletor, superficie,
				new SetLista.Config(true, true));
		return coletor;
	}

	public void adicionarHierarquicoInvisivelAbaixo(Point point) {
		if (point != null) {
			point.y += Constantes.TRINTA;
		}
		Coletor coletor = getColetorFormsInvisiveis();
		if (coletor.size() == 1) {
			tornarVisivel(coletor.get(0), point);
		}
	}

	public void adicionarHierarquicoInvisivelAcima(Point point) {
		if (point != null) {
			point.y -= Constantes.TRINTA;
		}
		Coletor coletor = getColetorFormsInvisiveis();
		if (coletor.size() == 1) {
			tornarVisivel(coletor.get(0), point);
		}
	}

	void tornarVisivel(String grupoTabela, Point point) {
		ObjetoSuperficieUtil.tornarVisivel(superficie, grupoTabela, point);
	}
}