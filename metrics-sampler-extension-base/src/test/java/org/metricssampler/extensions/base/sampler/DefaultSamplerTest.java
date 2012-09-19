package org.metricssampler.extensions.base.sampler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.config.Placeholder;
import org.metricssampler.extensions.base.sampler.DefaultSampler;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.writer.MetricsWriter;

public class DefaultSamplerTest {
	private DefaultSampler testee;
	private BulkMetricsReader bulkReader;
	private MetricsWriter writer1;
	private MetricsWriter writer2;
	private MetricsSelector transformer1;
	private MetricsSelector transformer2;
	
	@Before
	public void setup() {
		bulkReader = mock(BulkMetricsReader.class);
		
		writer1 = mock(MetricsWriter.class);
		writer2 = mock(MetricsWriter.class);
		
		transformer1 = mock(MetricsSelector.class);
		transformer2 = mock(MetricsSelector.class);
		
		testee = new DefaultSampler(bulkReader, new LinkedList<Placeholder>());
		testee.addWriter(writer1);
		testee.addWriter(writer2);
		testee.addSelector(transformer1);
		testee.addSelector(transformer2);
	}
	
	@Test
	public void sampleBulk() {
		testee.sample();
		verify(bulkReader, times(1)).open();
		verify(bulkReader, times(1)).close();

		verify(transformer1, times(1)).readMetrics(bulkReader);
		verify(transformer2, times(1)).readMetrics(bulkReader);
		
		verify(writer1, times(1)).open();
		verify(writer1, times(1)).close();
		
		verify(writer2, times(1)).open();
		verify(writer2, times(1)).close();
	}
}