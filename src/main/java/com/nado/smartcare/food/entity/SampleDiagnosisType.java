package com.nado.smartcare.food.entity;

public enum SampleDiagnosisType {
	SMAPLE_INTERNAL_MEDICINE("내과"),
	SMAPLE_SURGERY("외과"),
	SMAPLE_PSYCHIATRY("정신과");
	
	private final String SampleName;
	
	SampleDiagnosisType(String SampleName) {
		this.SampleName = SampleName;
	}
	
	public String getSampleName() {
		return SampleName;
	}
}
