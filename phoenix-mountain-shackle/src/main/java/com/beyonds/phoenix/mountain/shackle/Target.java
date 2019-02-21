/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

/**
 * @Author: Daniel.Cao
 * @Date: 2019年1月3日
 * @Time: 下午7:43:03
 *
 */
public interface Target<T> {
	/* The type of the interface this target applies to. ex. {@code Route53}. */
	Class<T> type();

	/*
	 * configuration key associated with this target. For example, {@code route53}.
	 */
	String name();
	
	public Domain apply(DomainTemplate input);

	public static class HardCodedTarget<T> implements Target<T> {

		private final Class<T> type;
		private final String name;
		
		public HardCodedTarget(Class<T> type, String name) {
			this.type = type;
			this.name = name;
		}

		@Override
		public Class<T> type() {
			return type;
		}

		@Override
		public String name() {
			return name;
		}
		
		/* no authentication or other special activity. just insert the url. */
		@Override
		public Domain apply(DomainTemplate input) {
			return input.domain();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof HardCodedTarget) {
				HardCodedTarget<?> other = (HardCodedTarget<?>) obj;
				return type.equals(other.type) && name.equals(other.name);
			}
			return false;
		}

		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + type.hashCode();
			result = 31 * result + name.hashCode();
			
			return result;
		}

		@Override
		public String toString() {
			return "HardCodedTarget(type=" + type.getSimpleName() + ", name=" + name + ")";
		}
	}
}
