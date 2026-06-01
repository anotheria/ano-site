package net.anotheria.anosite.action;

import net.anotheria.anosite.gen.ascustomaction.data.ActionMappingDef;

import java.util.List;

/**
 * Mapping submitted to the action command and defined by an editor/developer in the web interface.
 * @author lrosenberg
 *
 */
public class ActionMapping {
	/**
	 * Mapping definition from the cms.
	 */
	private ActionMappingDef def;
	/**
	 * Predefined command which is defined in the cms. The action is allowed to return a custom command instead, but if you want to achieve the behaviour defined by the editor you just return this command.
	 */
	private ActionCommand predefinedCommand;
	/**
	 * Creates a new ActionMapping
	 * @param aDef  TODO dummy comment for javadoc.
	 */
	public ActionMapping(ActionMappingDef aDef){
		def = aDef;
		predefinedCommand = new ActionCommand(def);
	}
	
	/**
	 * Returns the predefinedCommand.
	 * @return  TODO dummy comment for javadoc.
	 */
	public ActionCommand getPredefinedCommand(){
		return predefinedCommand;
	}

    /**
     * Returns LocalizationBundle ids, that are connected to this ActionMapping.
     * @return  TODO dummy comment for javadoc.
     */
    public List<String> getLocalizationBundleIds(){
        return def.getLocalizationBundles();
    }

    
    public String toString(){
    	return def.toString();
	}
}
