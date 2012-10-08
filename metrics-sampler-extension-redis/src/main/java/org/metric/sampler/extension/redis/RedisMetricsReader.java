package org.metric.sampler.extension.redis;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.OpenMetricsReaderException;
import org.metricssampler.reader.SimpleMetricName;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisMetricsReader extends AbstractMetricsReader<RedisInputConfig> implements BulkMetricsReader {
	private Jedis jedis = null;
	
	public RedisMetricsReader(final RedisInputConfig config) {
		super(config);
	}

	@Override
	public void open() throws MetricReadException {
		 reconnectIfNecessary();
	}

	private void reconnectIfNecessary() {
		if (jedis == null) {
			jedis = new Jedis(config.getHost(), config.getPort());
			if (config.hasPassword()) {
				final String response = jedis.auth(config.getPassword());
				if (!"OK".equals(response)) {
					throw new OpenMetricsReaderException("Incorrect password: " + response);
				}
			}
			 try {
				 jedis.connect();
			 } catch (final JedisConnectionException e) {
				 jedis = null;
				 throw new OpenMetricsReaderException(e);
			 }
		}
	}

	private void disconnect() {
		if (jedis != null) {
			try {
				jedis.disconnect();
			} catch (final JedisConnectionException e) {
				// ignore
			}
			jedis = null;
		}
	}
	@Override
	public void close() {
		// we keep the connection and never close it
	}

	@Override
	public Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException {
		reconnectIfNecessary();
		try {
			final long timestamp = System.currentTimeMillis();
			final Map<MetricName, MetricValue> result = new HashMap<MetricName, MetricValue>();
			final String info = jedis.info();
			for (final LineIterator lines = IOUtils.lineIterator(new StringReader(info)); lines.hasNext(); ) {
				final String line = lines.next();
				final String[] cols = line.split(":", 2);
				result.put(new SimpleMetricName(cols[0], ""), new MetricValue(timestamp, cols[1]));
			}
			return result;
		} catch (final JedisConnectionException e) {
			disconnect();
			throw new MetricReadException(e);
		}
	}

	@Override
	public Iterable<MetricName> readNames() {
		return readAllMetrics().keySet();
	}

}