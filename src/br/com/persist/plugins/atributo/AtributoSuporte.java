package br.com.persist.plugins.atributo;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.atributo.aux.Tipo;

public class AtributoSuporte {
	private TextField view;
	private TextField filterJS;
	private TextField controller;
	private TextField serviceJS;
	private TextField dto;
	private TextField filter;
	private TextField rest;
	private TextField service;
	private TextField bean;
	private TextField dao;
	private TextField daoImpl;
	private TextField test;

	public void setView(TextField view) {
		this.view = view;
	}

	public void setFilterJS(TextField filterJS) {
		this.filterJS = filterJS;
	}

	public void setController(TextField controller) {
		this.controller = controller;
	}

	public void setServiceJS(TextField serviceJS) {
		this.serviceJS = serviceJS;
	}

	public void setDto(TextField dto) {
		this.dto = dto;
	}

	public void setFilter(TextField filter) {
		this.filter = filter;
	}

	public void setRest(TextField rest) {
		this.rest = rest;
	}

	public void setService(TextField service) {
		this.service = service;
	}

	public void setBean(TextField bean) {
		this.bean = bean;
	}

	public void setDao(TextField dao) {
		this.dao = dao;
	}

	public void setDaoImpl(TextField daoImpl) {
		this.daoImpl = daoImpl;
	}

	public void setTest(TextField test) {
		this.test = test;
	}

	public String pesquisarViewFilter() {
		return pesquisarView() + "(filter)";
	}

	public String pesquisarView() {
		return "pesquisar" + Util.capitalize(getView());
	}

	public String exportarView() {
		return "exportar" + Util.capitalize(getView());
	}

	public String getViewDecap() {
		return Util.decapitalize(getView());
	}

	public String getView() {
		return view.getText();
	}

	public String limparFiltro() {
		return "limpar" + Util.capitalize(getFilterJS());
	}

	public String getFilterJS() {
		return filterJS.getText();
	}

	public String getController() {
		return controller.getText();
	}

	public String getServiceJS() {
		return serviceJS.getText();
	}

	public String getListDto() {
		return "List<" + getDto() + ">";
	}

	public String getDto() {
		return dto.getText();
	}

	public Tipo getTipoFilter() {
		return new Tipo(getFilter(), "filter");
	}

	public String getFilter() {
		return filter.getText();
	}

	public String getRest() {
		return rest.getText();
	}

	public Tipo getTipoService() {
		return new Tipo(getService(), "service");
	}

	public String getService() {
		return service.getText();
	}

	public String getBean() {
		return bean.getText();
	}

	public Tipo getTipoDAO() {
		return new Tipo(getDao(), "dao");
	}

	public String getDao() {
		return dao.getText();
	}

	public String getDaoImpl() {
		return daoImpl.getText();
	}

	public String getTest() {
		return test.getText();
	}
}