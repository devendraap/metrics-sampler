package org.metricssampler.extensions.graphite;

import static org.metricssampler.util.Preconditions.checkArgument;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

import org.metricssampler.config.OutputConfig;

public class GraphiteOutputConfig extends OutputConfig {
	private final String host;
	private final int port;
	private final String prefix;

	public GraphiteOutputConfig(final String name, final String host, final int port, final String prefix) {
		super(name);
		checkArgumentNotNullNorEmpty(host, "host");
		checkArgument(port > 0 && port < 65536, "port must be in range [1,65535]");
		this.host = host;
		this.port = port;
		this.prefix = prefix;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPrefix() {
		return prefix;
	}
}
