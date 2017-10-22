package model;

import java.util.ArrayList;
import java.util.List;

public class User {
	private String user_id;
	private List<String> similarityList = new ArrayList<>();
	private List<Entity> topics = new ArrayList<>();
	

	public List<Entity> getTopics() {
		return topics;
	}

	public void setTopics(List<Entity> topics) {
		this.topics = topics;
	}

	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public User(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public List<String> getSimilarity() {
		return similarityList;
	}

	public void setSimilarity(List<String> similarity) {
		this.similarityList = similarity;
	}

	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", similarityList=" + similarityList + ", topics=" + topics + "]";
	}
	
	
	
	

}
