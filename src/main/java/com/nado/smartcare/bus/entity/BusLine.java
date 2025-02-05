package com.nado.smartcare.bus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "BUS_LINE_LIST")
public class BusLine {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bus_line_list_seq_generator")
	@SequenceGenerator(name = "bus_line_list_seq_generator", sequenceName = "bus_line_list_seq", allocationSize = 1)
	private Long id;
	
	@Column(name = "LINE_ID")
	private int lineId;
	
	@Column(name = "LINE_NAME")
	private String lineName;
	
	@Column(name = "DIR_UP_NAME")
	private String dirUpName;
	
	@Column(name = "DIR_DOWN_NAME")
	private String dirDownName;
	
	@Column(name = "FIRST_RUN_TIME")
	private String firstRunTime;
	
	@Column(name = "LAST_RUN_TIME")
	private String lastRunTime;
	
	@Column(name = "RUN_INTERVAL")
	private int runInterval;
	
	@Column(name = "LINE_KIND")
	private int lineKind;
}
