package com.nado.smartcare.bus.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "BUSSTOP_LIST")
public class BusStop {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "busstop_list_seq_generator")
	@SequenceGenerator(name = "busstop_list_seq_generator", sequenceName = "busstop_list_seq", allocationSize = 1)
	private Long id;
	
	@Column(name = "BUSSTOP_ID")
	private int busStopId;
	
	@Column(name = "BUSSTOP_NAME")
	private String busStopName;
	
	@Column(name = "LONGITUDE")
	private double longitude;
	
	@Column(name = "LATITUDE")
	private double latitude;
	
	@Column(name = "NEXT_BUSSTOP")
	private String nextBusStop;
	
	@OneToMany(mappedBy = "busStop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<BusStopAndLineStation> busStopAndLineStations = new ArrayList<>();

}
