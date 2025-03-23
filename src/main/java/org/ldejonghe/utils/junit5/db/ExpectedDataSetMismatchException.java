package org.ldejonghe.utils.junit5.db;


public class ExpectedDataSetMismatchException extends AssertionError {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExpectedDataSetMismatchException(String message) {
        super(message);
    }
}
