package com.nado.smartcare.food.entity;

public enum SampleDiagnosisType {
	SMAPLE_INTERNAL_MEDICINE("INTERNAL_MEDICINE"),
	SMAPLE_SURGERY("SURGERY"),
	SMAPLE_PSYCHIATRY("PSYCHIATRY");
	
	private final String SampleName;
	
	SampleDiagnosisType(String SampleName) {
		this.SampleName = SampleName;
	}
	
	public String getSampleName() {
		return SampleName;
	}
}
