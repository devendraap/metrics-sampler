package org.metricssampler.config.loader.xbeans;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.metricssampler.config.ConfigurationException;

public class TemplatableXBeanUtils {
	public static <T extends TemplatableXBean> LinkedHashMap<String, T> sortByDependency(final List<T> list) {
		final Map<String, T> map = createMapByName(list);
		final LinkedHashMap<String, T> result = new LinkedHashMap<String, T>(list.size());
		for (final T item : list) {
			addDependency(item, result, map);
		}
		return result;
	}

	private static <T extends TemplatableXBean> void addDependency(final T item, final LinkedHashMap<String, T> result, final Map<String, T> map) {
		if (!result.containsKey(item.getName())) {
			if (item.hasTemplate()) {
				final T parent = map.get(item.getTemplate());
				if (parent == null) {
					throw new ConfigurationException("Template named \"" + item.getTemplate() + "\" not found");
				}
				addDependency(parent, result, map);
			}
			result.put(item.getName(), item);
		}
	}

	public static <T extends NamedXBean> Map<String, T> createMapByName(final List<T> list) {
		final Map<String, T> result = new HashMap<String, T>(list.size());
		for (final T item : list) {
			result.put(item.getName(), item);
		}
		return result;
	}

	public static <T extends TemplatableXBean> void applyTemplate(final T target, final LinkedHashMap<String, T> xbeans) {
		if (!target.hasTemplate()) {
			return;
		}
		try {
			final T template = xbeans.get(target.getTemplate());
			@SuppressWarnings("unchecked")
			final Map<String, Object> templateProperties = PropertyUtils.describe(template);
			templateProperties.remove("name");
			templateProperties.remove("abstract");
			
			@SuppressWarnings("unchecked")
			final Map<String, Object> targetProperties = PropertyUtils.describe(target);
			for (final Entry<String, Object> entry : targetProperties.entrySet()) {
				if (entry.getValue() == null) {
					final Object templateValue = templateProperties.get(entry.getKey());
					if (templateValue != null) {
						if (PropertyUtils.isWriteable(target, entry.getKey())) {
							PropertyUtils.setProperty(target, entry.getKey(), templateValue);
						}
					}
				}
			}
		} catch (final InvocationTargetException e) {
			throw new ConfigurationException(e);
		} catch (final IllegalAccessException e) {
			throw new ConfigurationException(e);
		} catch (final NoSuchMethodException e) {
			throw new ConfigurationException(e);
		}
	}

}
