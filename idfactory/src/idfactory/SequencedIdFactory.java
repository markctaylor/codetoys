package idfactory;

/**
 * A lightweight convenience mechanism for constructing multiple unique IDs containing some 
 * fixed values as well as a dynamic unique long. When the prefix/suffix values are chosen well, 
 * this can create a sufficiently global unique ID.
 * 
 * Produces an output ID string that includes an ever-increasing long value as part of the
 * string. 
 */
public class SequencedIdFactory {

	private String prefix;
	private String suffix;

	/**
	 * Unique ID strings will be generated with the concatenation of the provided prefix,
	 * a generated unique long, and the provided suffix. Either parameter can be an empty string or null.
	 * These values and structure are immutable once the class is initialized.
	 * @param prefix string or null
	 * @param suffix string or null
	 */
	public SequencedIdFactory(String prefix, String suffix) {
		this.prefix = prefix==null ? "" : prefix;
		this.suffix = suffix==null ? "" : suffix;
	}
	
	public String newId() {
		return new StringBuilder()
				.append(prefix)
				.append(UniqueTimestampGenerator.get())
				.append(suffix)
				.toString();
	}

}
