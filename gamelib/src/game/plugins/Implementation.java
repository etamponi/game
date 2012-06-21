package game.plugins;

public class Implementation<T> implements Comparable<Implementation<T>> {
	
	public T content;
	
	public Implementation(T content) {
		this.content = content;
	}
	
	public T getContent() {
		return content;
	}
	
	@Override
	public String toString() {
		if (content == null)
			return "<null>";
		else
			return content.getClass().getSimpleName();
	}

	@Override
	public int compareTo(Implementation o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Implementation) {
			Implementation other = (Implementation)obj;
			if (content == other.content)
				return true;
			else if (content == null || other.content == null)
				return false;
			else
				return content.getClass().equals(other.content.getClass());
		} else
			return false;
	}
	
}