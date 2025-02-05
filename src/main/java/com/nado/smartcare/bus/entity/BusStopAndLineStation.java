package com.nado.smartcare.bus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "LINE_BUSSTOP_LIST")
public class BusStopAndLineStation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "line_busstop_list_seq_generator")
	@SequenceGenerator(name = "line_busstop_list_seq_generator", sequenceName = "line_busstop_list_seq", allocationSize = 1)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "LINE_ID")
	private LineStation lineStation;
	
	@ManyToOne
	@JoinColumn(name = "BUSSTOP_ID")
	private BusStop busStop;
	
}
