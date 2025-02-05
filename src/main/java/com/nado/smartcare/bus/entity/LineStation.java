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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "LINE_STATION_LIST")
public class LineStation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "line_station_list_seq_generator")
	@SequenceGenerator(name = "line_station_list_seq_generator", sequenceName = "line_station_list_seq", allocationSize = 1)
	private Long id;
	
	@Column(name = "LINE_ID")
	private int lineId;
	
	@Column(name = "LINE_NAME")
	private String lineName;
	
	@Column(name = "BUSSTOP_ID")
	private int busStopId;
	
	@Column(name = "BUSSTOP_NAME")
	private String busStopName;
	
	@Column(name = "RETURN_FLAG")
	private int returnFlag;
	
	@OneToMany(mappedBy = "lineStation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<BusStopAndLineStation> busStopAndLineStations = new ArrayList<>();
	
}
