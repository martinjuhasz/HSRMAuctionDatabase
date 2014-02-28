package model;

public class Auction {

	private long id;
	private String title;
	private String startTime;
	private String endTime;
	private float directBuyPrice;
	private String category;
	private float minPrice;
	
	public Auction(long id, String title, String startTime, String endTime, float directlyBuyPrice, String category, float minPrice) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.directBuyPrice = directlyBuyPrice;
		this.category = category;
		this.minPrice = minPrice;
	}

	public long getId() {
		return id;
	}


	public String getTitle() {
		return title;
	}


	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public float getDirectBuyPrice() {
		return directBuyPrice;
	}

	public String getCategory() {
		return category;
	}

	public float getMinPrice() {
		return minPrice;
	}

	
}
