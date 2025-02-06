package kr.co.clipsoft.repository.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ClipRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return "mainDataSource";
	}

}
