package br.com.persist.plugins.atributo;

import br.com.persist.componente.TextField;

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

	public String getView() {
		return view.getText();
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

	public String getDto() {
		return dto.getText();
	}

	public String getFilter() {
		return filter.getText();
	}

	public String getRest() {
		return rest.getText();
	}

	public String getService() {
		return service.getText();
	}

	public String getBean() {
		return bean.getText();
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