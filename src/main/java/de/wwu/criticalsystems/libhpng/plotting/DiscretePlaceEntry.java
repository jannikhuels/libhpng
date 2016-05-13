package de.wwu.criticalsystems.libhpng.plotting;

public class DiscretePlaceEntry extends PlotEntry{

	public DiscretePlaceEntry(Double time, Integer numberOfTokens) {
		super(time);
		NumberOfTokens = numberOfTokens;
	}

	public Integer getNumberOfTokens() {
		return NumberOfTokens;
	}

	public void setNumberOfTokens(Integer numberOfTokens) {
		NumberOfTokens = numberOfTokens;
	}

	private Integer NumberOfTokens;
}
