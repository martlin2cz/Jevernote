package cz.martlin.jevernote.dataobj.cmp;

public class Change<T> {

	public static enum ChangeType {
		CREATE, RENAME, DELETE, UPDATE
	}

	private final ChangeType type;
	private final T first;
	private final T second;

	protected Change(ChangeType type, T first, T second) {
		super();
		this.type = type;
		this.first = first;
		this.second = second;
	}

	///////////////////////////////////////////////////////////////////////////

	public ChangeType getType() {
		return type;
	}

	public boolean is(ChangeType type) {
		return type.equals(this.type);
	}

	public T getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
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
		Change<?> other = (Change<?>) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BaseComparement [type=" + type + ", first=" + first + ", second=" + second + "]";
	}

	///////////////////////////////////////////////////////////////////////////

	public static <T> Change<T> unary(ChangeType type, T what) {
		return new Change<T>(type, what, null);
	}

	public static <T> Change<T> binary(ChangeType type, T first, T second) {
		return new Change<T>(type, first, second);
	}

	public static <T> Change<T> create(T what) {
		return new Change<T>(ChangeType.CREATE, what, null);
	}

	public static <T> Change<T> delete(T what) {
		return new Change<T>(ChangeType.DELETE, what, null);
	}

	public static <T> Change<T> rename(T original, T renamed) {
		return new Change<T>(ChangeType.RENAME, original, renamed);
	}

	public static <T> Change<T> update(T original, T updated) {
		return new Change<T>(ChangeType.UPDATE, original, updated);
	}

}
