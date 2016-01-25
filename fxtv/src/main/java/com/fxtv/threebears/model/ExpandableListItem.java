package com.fxtv.threebears.model;

/*  author yukun
 date:2015/3/23*/
public class ExpandableListItem {
	private String ID;
	private String gameName;

	private String gameIntro;
	private String imageUrl;

	 
	public String getgameName() {
		return gameName;
	}
	public String getgameIntro() {
		return gameIntro;
	}
	public String getimageUrl() {
		return imageUrl;
	}
	 
	 
	//set
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	
	public void setGameIntro(String gameIntro) {
		this.gameIntro = gameIntro;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	 
}
