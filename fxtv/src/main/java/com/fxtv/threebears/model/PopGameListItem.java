package com.fxtv.threebears.model;

public class PopGameListItem {

	private String itemID;
	private String gameName;
	private String gameImageUrl;
	private boolean gameChecked;

	public boolean isGameChecked() {
		return gameChecked;
	}

	public void setGameChecked(boolean gameChecked) {
		this.gameChecked = gameChecked;
	}

	public String getGameImageUrl() {
		return gameImageUrl;
	}

	public void setGameImageUrl(String gameImageUrl) {
		this.gameImageUrl = gameImageUrl;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
}
