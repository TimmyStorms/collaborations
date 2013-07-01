package com.github.timmystorms.collaborations;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.timmystorms.collaborations.entity.util.Indexes;
import com.github.timmystorms.collaborations.entity.util.Labels;
import com.github.timmystorms.collaborations.entity.util.Properties;

@Component
public class AppConfig {

	@Autowired
	private GraphDatabaseService db;

	@PostConstruct
	public void init() {
		createLabelIndexes();
		createLuceneIndexes();
		setDefaultTimeZone();
	}

	private void createLabelIndexes() {
		final Transaction tx = db.beginTx();
		if (!db.schema().getIndexes(Labels.COLLAB).iterator().hasNext()) {
			db.schema().indexFor(Labels.COLLAB).on(Properties.NAME).create();
		}
		if (!db.schema().getIndexes(Labels.TAG).iterator().hasNext()) {
			db.schema().indexFor(Labels.TAG).on(Properties.NAME).create();
		}
		tx.success();
		tx.finish();
	}

	private void createLuceneIndexes() {
		final Transaction tx = db.beginTx();
		db.index().forNodes(Indexes.NAMES, MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext"));
		tx.success();
		tx.finish();
	}

	private void setDefaultTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

}
