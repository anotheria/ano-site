package net.anotheria.anosite.access.constraint;

import net.anotheria.access.impl.Constraint;

/**
 * Constraint with loaded parameters from CMS.
 * 
 * @author Alexandr Bolbat
 */
public abstract class ParametrizedConstraint implements Constraint {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 3485629270759746152L;

	/**
	 * Parameter 1.
	 */
	private String parameter1;

	/**
	 * Parameter 2.
	 */
	private String parameter2;

	/**
	 * Parameter 3.
	 */
	private String parameter3;

	/**
	 * Parameter 4.
	 */
	private String parameter4;

	/**
	 * Parameter 5.
	 */
	private String parameter5;

	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(final String aParameter1) {
		this.parameter1 = aParameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(final String aParameter2) {
		this.parameter2 = aParameter2;
	}

	public String getParameter3() {
		return parameter3;
	}

	public void setParameter3(final String aParameter3) {
		this.parameter3 = aParameter3;
	}

	public String getParameter4() {
		return parameter4;
	}

	public void setParameter4(final String aParameter4) {
		this.parameter4 = aParameter4;
	}

	public String getParameter5() {
		return parameter5;
	}

	public void setParameter5(final String aParameter5) {
		this.parameter5 = aParameter5;
	}

	@Override
	public String toString() {
		return "ParametrizedConstraint [parameter1=" + parameter1 + ", parameter2=" + parameter2 + ", parameter3=" + parameter3 + ", parameter4=" + parameter4
				+ ", parameter5=" + parameter5 + "]";
	}

}
