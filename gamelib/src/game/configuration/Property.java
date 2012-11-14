package game.configuration;

import java.util.Set;



public class Property {
	
	public static final String ANY = "*";

	private final IObject root;
	
	private final String path;

	public Property(IObject root, String path) {
		this.root = root;
		this.path = path;
	}
	
	public Object getContent() {
		if (path.isEmpty())
			return root;
		else
			return root.getContent(path);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getContent(Class<T> contentType) {
		return (T)getContent();
	}
	
	@SuppressWarnings("unchecked")
	public void setContent(Object content) {
		if (path.contains(ANY)) {
			int indexPre = path.indexOf('*');
			int indexAfter = indexPre + 2;
			IList<? extends IObject> list;
			if (indexPre > 0) {
				indexPre -= 1;
				list = root.getContent(path.substring(0, indexPre), IList.class); 
			} else {
				list = (IList<? extends IObject>)root;
			}
			for(int i = 0; i < list.size(); i++)
				list.get(i).setContent(path.substring(indexAfter), content);
		} else {
			root.setContent(path, content);
		}
	}
	
	public IObject getRoot() {
		return root;
	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Property) {
			Property other = (Property)o;
			return this.root == other.root && this.path.equals(other.path);
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "<"+root+">" + (path.isEmpty() ? "" : "." + path);
	}
	
	public boolean isParent(Property complete) {
		return isPrefix(complete, true);
	}
	
	public boolean isPrefix(Property complete, boolean parentOnly) {
		if (this.root != complete.root)
			return false;

		String[] prefixTokens = this.path.split("\\.");
		String[] completeTokens = complete.path.split("\\.");
		if (!this.path.isEmpty()) {
			if (prefixTokens.length > completeTokens.length)
				return false;
			
			for(int i = 0; i < prefixTokens.length; i++) {
				if (completeTokens[i].equals(ANY))
					continue;
				if (!prefixTokens[i].equals(completeTokens[i]))
					return false;
			}
			
			return parentOnly ? prefixTokens.length == completeTokens.length-1 : true;
		} else {
			return parentOnly ? completeTokens[0].length() != 0 && completeTokens.length == 1 : true;
		}
	}
	
	public boolean includes(Property other) {
		return other.isPrefix(this, false) && this.path.split("\\.").length == other.path.split("\\.").length;
	}
	
	public IObject getParent() {
		if (!path.contains("."))
			return root;
		
		String parentPath = path.substring(0, path.lastIndexOf('.'));
		return root.getContent(parentPath, IObject.class);
	}
	
	public Property getLocalProperty() {
		if (!path.contains("."))
			return this;
		
		return new Property(getParent(), path.substring(path.lastIndexOf('.')+1));
	}
	
	public Class<?> getContentType(boolean runtime) {
		if (runtime) {
			Object content = getContent();
			return content == null ? null : content.getClass();
		} else {
			try {
				Property local = getLocalProperty();
				return local.root.getClass().getField(local.path).getType();
			} catch (NoSuchFieldException | SecurityException e) {
				return null;
			}
		}
	}

	@Override
	public int hashCode() {
		return root.hashCode() + path.hashCode();
	}

	public Set<Class> getCompatibleContentTypes() {
		return getParent().getCompatibleContentTypes(getLocalProperty().getPath());
	}
	
	public boolean isBound() {
		return getParent().getBoundProperties().contains(getLocalProperty());
	}
	
	public boolean isUnbound() {
		return !isBound();
	}
	
}
