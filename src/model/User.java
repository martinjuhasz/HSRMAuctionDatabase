package model;

public class User {
	
	private String username;
	private String firstName;
	private String surName;
	private String email;
	private String street;
	private String houseNumber;
	private String postalCode;
	private String city;
	
	public User(String username, String firstName, String surName, String email, String street, String houseNumber, String postalCode, String city) {
		this.username = username;
		this.firstName = firstName;
		this.surName = surName;
		this.email = email;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postalCode = postalCode;
		this.city = city;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getSurName() {
		return surName;
	}

	public String getEmail() {
		return email;
	}

	public String getStreet() {
		return street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCity() {
		return city;
	}
	
	
	
}