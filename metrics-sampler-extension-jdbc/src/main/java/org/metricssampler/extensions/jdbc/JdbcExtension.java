package org.metricssampler.extensions.jdbc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.service.Extension;
import org.metricssampler.service.LocalObjectFactory;

public class JdbcExtension implements Extension {

	@Override
	public String getName() {
		return "jdbc";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(JdbcInputXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new JdbcObjectFactory();
	}

}
