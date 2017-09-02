package model;

public class Entity {
	private int count;
	private String name;
	private double relevance;
	private String type;
	private double tf_idf;
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Entity() {
		this.count = 1;
		this.name = "";
		this.relevance = 0.0;
		this.type= "";
		this.tf_idf =  0.0;
	}

	public String getType() {
		return type;
	}



	public double getTf_idf() {
		return tf_idf;
	}

	public void setTf_idf(double tf_idf) {
		this.tf_idf = tf_idf;
	}



	@Override
	public String toString() {
		return "{count=" + count + ", name=" + name + ", relevance=" + relevance + ", type=" + type + ", tf_idf="
				+ tf_idf + ", date=" + date + "}";
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRelevance() {
		return relevance;
	}

	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	public Entity(int count, String name, double relevance, String type) {
		super();
		this.count = count;
		this.name = name;
		this.relevance = relevance;
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}




	
	

}
