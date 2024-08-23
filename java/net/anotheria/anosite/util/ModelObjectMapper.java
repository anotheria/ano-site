package net.anotheria.anosite.util;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.validation.ValidationError;
import net.anotheria.maf.validation.Validator;
import net.anotheria.maf.validation.annotations.ValidateCustom;
import net.anotheria.maf.validation.annotations.ValidateNotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

/**
 * Model Object Mapper.
 */
@Deprecated
public final class ModelObjectMapper {

	/**
	 * Default constructor.
	 */
	private ModelObjectMapper() {
	}

	/**
	 * Mapper log.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelObjectMapper.class);

	/**
	 * @param req  http request
	 * @param bean backing bean
	 */
	@Deprecated
	public static void map(final HttpServletRequest req, final Object bean) {
		//FormObjectMapper.map(req, bean);
	}

	/**
	 * Validate mapped bean according defined preconditions.
	 *
	 * @param req  http request
	 * @param bean backing bean
	 */
	@Deprecated
	public static void validate(final HttpServletRequest req, final Object bean) {
		final Class beanClass = bean.getClass();
		final Field[] fields = beanClass.getDeclaredFields();
		for (Field field : fields) {
			try {
				final ValidateNotEmpty validateNotEmpty = field.getAnnotation(ValidateNotEmpty.class);
				if (validateNotEmpty != null) {
					field.setAccessible(true);
					Object value = field.get(bean);
					if (value == null
							|| String.valueOf(value).isEmpty()) {
						APICallContext.getCallContext().addValidationError(new ValidationError(field.getName(), validateNotEmpty.key(), validateNotEmpty.message()));
					}
				}
				final ValidateCustom validateCustom = field.getAnnotation(ValidateCustom.class);
				if (validateCustom != null) {
					field.setAccessible(true);
					Object value = field.get(bean);
					

					Validator validator = validateCustom.validator().newInstance();
					//noinspection unchecked
					if (!validator.validate(value)) {
						APICallContext.getCallContext().addValidationError(new ValidationError(field.getName(), validateCustom.key(), validateCustom.message()));
					}
				}
			} catch (IllegalAccessException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (InstantiationException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
