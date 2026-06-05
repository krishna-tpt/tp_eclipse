package com.movies.db;

public class Movie {
	static int idGenerater = 1;
	int id;
	String name;
	String subLink;
	String category;
	String link;
	String pageurl;

	public Movie(String name, String subLink, String category, String link, String pageurl) {
		this.id = idGenerater;
		this.name = name;
		this.subLink = subLink;
		this.category = category;
		this.link = link;
		this.pageurl = pageurl;
		idGenerater++;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPageurl() {
		return pageurl;
	}

	public void setPageurl(String pageurl) {
		this.pageurl = pageurl;
	}

	public static int getIdGenerater() {
		return idGenerater;
	}

	public static void setIdGenerater(int idGenerater) {
		Movie.idGenerater = idGenerater;
	}

	public String getSubLink() {
		return subLink;
	}

	public void setSubLink(String subLink) {
		this.subLink = subLink;
	}

}
