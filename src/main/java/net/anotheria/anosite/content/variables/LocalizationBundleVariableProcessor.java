package net.anotheria.anosite.content.variables;

/**
 * Replace text of variable by specific text.
 *
 * @author ykalapusha
 */
public interface LocalizationBundleVariableProcessor {
    /**
     * Replace method.
     *
     * @param variable      processor variable for replacing
     * @return              text value instead variable
     */
    String replace(String variable);
}
