package org.metricssampler.extensions.modqos;

import java.net.URL;

import org.metricssampler.config.InputConfig;

public class ModQosInputConfig extends InputConfig {
	private final URL url;
	private final String username;
	private final String password;
	private final AuthenticationType authType;

	public ModQosInputConfig(final String name, final URL url, final String username, final String password, final AuthenticationType authType) {
		super(name);
		this.url = url;
		this.username = username;
		this.password = password;
		this.authType = authType;
	}

	public URL getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public AuthenticationType getAuthType() {
		return authType;
	}

	public enum AuthenticationType {
		NONE,
		BASIC
	}
}
