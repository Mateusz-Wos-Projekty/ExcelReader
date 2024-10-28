package config.BeanDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import javax.validation.*;
import java.io.IOException;
import java.util.Set;


public class BeanDeserializerWithValidation extends BeanDeserializer {
    private static final Validator VALIDATOR =
            Validation.byDefaultProvider()
                    .configure()
                    .messageInterpolator(new ParameterMessageInterpolator())
                    .buildValidatorFactory()
                    .getValidator();

    protected BeanDeserializerWithValidation(BeanDeserializerBase src) {
        super(src);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object instance = super.deserialize(p, ctxt);
        validate(instance);
        return instance;
    }

    private void validate(Object instance) {
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(instance);
        if (violations.size() > 0) {
            StringBuilder msg = new StringBuilder();
            msg.append("JSON object is not valid. Reasons (").append(violations.size()).append("): ");
            for (ConstraintViolation<Object> violation : violations) {
                msg.append(violation.getMessage()).append(", ");
            }
            throw new ConstraintViolationException(msg.toString(), violations);
        }
    }
}