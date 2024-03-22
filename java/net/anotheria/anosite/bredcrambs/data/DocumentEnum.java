package net.anotheria.anosite.bredcrambs.data;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.aslayoutdata.data.PageLayout;
import net.anotheria.anosite.gen.aslayoutdata.service.ASLayoutDataServiceException;
import net.anotheria.anosite.gen.aslayoutdata.service.IASLayoutDataService;
import net.anotheria.anosite.gen.assitedata.data.EntryPoint;
import net.anotheria.anosite.gen.assitedata.data.NaviItem;
import net.anotheria.anosite.gen.assitedata.data.PageAlias;
import net.anotheria.anosite.gen.assitedata.data.PageTemplate;
import net.anotheria.anosite.gen.assitedata.data.Site;
import net.anotheria.anosite.gen.assitedata.service.ASSiteDataServiceException;
import net.anotheria.anosite.gen.assitedata.service.IASSiteDataService;
import net.anotheria.anosite.gen.aswebdata.data.Attribute;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.gen.aswebdata.data.Pagex;
import net.anotheria.anosite.gen.aswebdata.service.ASWebDataServiceException;
import net.anotheria.anosite.gen.aswebdata.service.IASWebDataService;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ConstantNotFoundException;
import net.anotheria.util.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.*;

/**
 * Enum represents type of elements and can find usages of current element by its id.
 *
 * @author vzarva
 */
public enum DocumentEnum {

    //Content menu.

    PAGE("PAGEX") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfPage(pId);
        }
    },
    BOX("BOX") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfBox(pId);
        }
    },
    ATTRIBUTE("ATTRIBUTE") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfAttribute(pId);
        }
    },

    //Site menu.

    SITE("SITE") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfSite(pId);
        }
    },
    NAVIITEM("NAVIITEM") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfNaviItem(pId);
        }
    },
    PAGE_TEMPLATE("PAGETEMPLATE") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfPageTemplate(pId);
        }
    },
    MEDIA_LINK("MEDIALINK") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfMediaLink(pId);
        }
    },
    SCRIPT("SCRIPT") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfScript(pId);
        }
    },
    PAGE_ALIAS("PAGEALIAS") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfPageAlias(pId);
        }
    },

    //Layout menu.

    LAYOUT("PAGELAYOUT") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfLayout(pId);
        }
    },
    STYLE("PAGESTYLE") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfStyle(pId);
        }
    },

    //Definitions menu.

    GENERIC_BOX_TYPE("GENERICBOXTYPE") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfGenericType(pId);
        }
    },
    CUSTOM_BOX_TYPE("CUSTOMBOXTYPE") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfCustomType(pId);
        }
    },
    GENERIC_BOX_HANDLER("GENERICBOXHANDLERDEF") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfGenericHandler(pId);
        }
    },
    CUSTOM_BOX_HANDLER("CUSTOMBOXHANDLERDEF") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfCustomHandler(pId);
        }
    },
    GENERIC_GUARD("GENERICGUARDDEF") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfGenericGuard(pId);
        }
    },
    CUSTOM_GUARD("CUSTOMGUARDDEF") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfCustomGuard(pId);
        }
    },

    //Resources menu.

    LOCALIZATION_BUNDLE("LOCALIZATIONBUNDLE") {
        @Override
        public List<String> findReferences(String pId) {
            return findUsagesOfLocalizationBundle(pId);
        }
    };

    /**
     * Logger by default.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentEnum.class);

    /**
     * Data services.
     */
    private static IASWebDataService webDataService;
    private static IASSiteDataService siteDataService;
    private static IASLayoutDataService layoutDataService;

    static {
        try {
            webDataService = MetaFactory.get(IASWebDataService.class);
            siteDataService = MetaFactory.get(IASSiteDataService.class);
            layoutDataService = MetaFactory.get(IASLayoutDataService.class);
        } catch (MetaFactoryException e) {
            LOGGER.error(MarkerFactory.getMarker("FATAL"), "Services init failure", e);
        }
    }

    private String value;

    DocumentEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Method find usages of element by its id.
     *
     * @param pId - id of element
     * @return - list of references to the elements, where element was used
     */
    public abstract List<String> findReferences(String pId);

    public static DocumentEnum getConstantByValue(String value) throws ConstantNotFoundException {
        for (DocumentEnum e : values()) {
            if (e.getValue().equalsIgnoreCase(value)) {
                return e;
            }
        }
        throw new ConstantNotFoundException("DocumentEnum value not found by value[" + value + "]");
    }

    private static List<String> findUsagesOfBox(String pId) {
        List<String> result = new ArrayList<String>();
        try {
            Map<String, Box> mapOfAllBoxes = new HashMap<String, Box>();
            for (Box box : webDataService.getBoxs()) {
                mapOfAllBoxes.put(box.getId(), box);
            }

            for (Pagex p : webDataService.getPagexs()) {
                result.addAll(findBoxUsages(p.getName(), p.getC1(), pId, p, Pagex.PROP_C1, mapOfAllBoxes));
                result.addAll(findBoxUsages(p.getName(), p.getC2(), pId, p, Pagex.PROP_C2, mapOfAllBoxes));
                result.addAll(findBoxUsages(p.getName(), p.getC3(), pId, p, Pagex.PROP_C3, mapOfAllBoxes));
                result.addAll(findBoxUsages(p.getName(), p.getHeader(), pId, p, Pagex.PROP_HEADER, mapOfAllBoxes));
                result.addAll(findBoxUsages(p.getName(), p.getFooter(), pId, p, Pagex.PROP_FOOTER, mapOfAllBoxes));
            }

            for (PageTemplate pt : siteDataService.getPageTemplates()) {
                result.addAll(findBoxUsages(pt.getName(), pt.getC1first(), pId, pt, PageTemplate.PROP_C1FIRST, mapOfAllBoxes));
                result.addAll(findBoxUsages(pt.getName(), pt.getC1last(), pId, pt, PageTemplate.PROP_C1LAST, mapOfAllBoxes));
                result.addAll(findBoxUsages(pt.getName(), pt.getC2first(), pId, pt, PageTemplate.PROP_C2FIRST, mapOfAllBoxes));
                result.addAll(findBoxUsages(pt.getName(), pt.getC2last(), pId, pt, PageTemplate.PROP_C2LAST, mapOfAllBoxes));
                result.addAll(findBoxUsages(pt.getName(), pt.getC3first(), pId, pt, PageTemplate.PROP_C3FIRST, mapOfAllBoxes));
                result.addAll(findBoxUsages(pt.getName(), pt.getC3last(), pId, pt, PageTemplate.PROP_C3LAST, mapOfAllBoxes));
                result.addAll(findBoxUsages(pt.getName(), pt.getMeta(), pId, pt, PageTemplate.PROP_META, mapOfAllBoxes));
                result.addAll(findBoxUsages(pt.getName(), pt.getHeader(), pId, pt, PageTemplate.PROP_HEADER, mapOfAllBoxes));
                result.addAll(findBoxUsages(pt.getName(), pt.getFooter(), pId, pt, PageTemplate.PROP_FOOTER, mapOfAllBoxes));
            }

            for (Map.Entry<String, Box> entry : mapOfAllBoxes.entrySet()) {
                String currentBoxId = entry.getKey();
                if (currentBoxId.equalsIgnoreCase(pId))
                    continue;
                Box curentBox = entry.getValue();
                StringBuffer tempPath = new StringBuffer("</br><a href=\"aswebdataBoxEdit?pId=" + currentBoxId + "\" > Box [" + curentBox.getName() + "] </a> -> " +
                        " <a href=\"aswebdataBoxSubboxesShow?ownerId=" + currentBoxId + "\"> Subboxes </a>");
                if (currentBoxId.equalsIgnoreCase(pId)) {
                    result.add(tempPath + "</br>");
                }
                List<String> subboxList = curentBox.getSubboxes();
                if (!subboxList.isEmpty()) {
                    findBoxInSubboxesRecursively(subboxList, pId, tempPath, result, mapOfAllBoxes);
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("can't retrieve pagex getPagex()", e);
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("can't retrieve pagex getPageTemplates()", e);
        }
        return result;
    }

    private static List<String> findBoxUsages(String nameOfPage, List<String> list, String searchElemId, DataObject dataObject, String c, Map<String, Box> mapOfAllBoxes) {
        if (StringUtils.isEmpty(searchElemId) || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> ret = new ArrayList<String>();

        String module = "aswebdata";
        if (dataObject.getDefinedName() != null && dataObject.getDefinedName().equalsIgnoreCase("PageTemplate")) {
            module = "assitedata";
        }

        StringBuffer pathBefore = new StringBuffer("</br> <a href=\"" + module + dataObject.getDefinedName() + "Edit?pId=" + dataObject.getId() + "\" >" + dataObject.getDefinedName() + " [" + nameOfPage + "]  </a> ->" +
                " <a href=\"" + module + dataObject.getDefinedName() + WordUtils.capitalize(c) + "Show?ownerId=" + dataObject.getId() + "\" > " + WordUtils.capitalize(c) + " </a>");

        for (String id : list) {
            Box box = mapOfAllBoxes.get(id);
            if (box == null)
                continue;

            if (id.equalsIgnoreCase(searchElemId)) {
                ret.add(pathBefore + "</br>");
            }
            List<String> subboxList = box.getSubboxes();
            if (!subboxList.isEmpty()) {
                findBoxInSubboxesRecursively(subboxList, searchElemId, pathBefore.append("-> <a href=\"aswebdataBoxEdit?pId=" + id + "\" > Box [" + box.getName() + "] </a> ->" +
                        " <a href=\"aswebdataBoxSubboxesShow?ownerId=" + id + "\"> Subboxes </a>"), ret, mapOfAllBoxes);
            }
        }

        return ret;
    }

    private static void findBoxInSubboxesRecursively(List<String> list, String searchElementId, StringBuffer pathBefore, List<String> tempList, Map<String, Box> mapOfAllBoxes) {
        for (String id : list) {
            Box box = mapOfAllBoxes.get(id);
            if (box == null)
                continue;

            StringBuffer tempPath = new StringBuffer(" -> <a href=\"aswebdataBoxEdit?pId=" + id + "\" > Box [" + box.getName() + "] </a> -> " +
                    " <a href=\"aswebdataBoxSubboxesShow?ownerId=" + id + "\"> Subboxes </a>");
            if (id.equalsIgnoreCase(searchElementId)) {
                tempList.add(pathBefore + " </br>");
            }
            List<String> subboxList = box.getSubboxes();
            if (!subboxList.isEmpty()) {
                findBoxInSubboxesRecursively(subboxList, searchElementId, pathBefore.append(tempPath), tempList, mapOfAllBoxes);
            }
        }
    }

    private static List<String> findUsagesOfAttribute(String pId) {
        List<String> result = new ArrayList<String>();
        try {
            Map<String, Attribute> mapOfAllAttributes = new HashMap<String, Attribute>();
            for (Attribute attribute : webDataService.getAttributes()) {
                mapOfAllAttributes.put(attribute.getId(), attribute);
            }

            for (Pagex pagex : webDataService.getPagexs()) {
                StringBuffer pathBefore = new StringBuffer("</br> <a href=\"aswebdataPagexEdit?pId=" + pagex.getId() + "\" > Pagex [" + pagex.getName() + "]  </a> ->" +
                        " <a href=\"aswebdataPagexAttributesShow?ownerId=" + pagex.getId() + "\" > Attributes </a>");
                for (String attributeId : pagex.getAttributes()) {
                    Attribute attribute = mapOfAllAttributes.get(attributeId);
                    if (attribute == null)
                        continue;

                    if (attributeId.equalsIgnoreCase(pId)) {
                        result.add(pathBefore + "</br>");
                    }
                    List<String> subattrubutes = attribute.getSubattributes();
                    if (!subattrubutes.isEmpty()) {
                        StringBuffer pathWithCurrentAttr = new StringBuffer(" ->   <a href=\"aswebdataAttributeEdit?pId=" + attribute.getId() + "\" > Attribute [" + attribute.getName() + "]  </a> ->" +
                                "<a href=\"aswebdataAttributeSubattributesShow?ownerId=" + attribute.getId() + "\" > Subattributes </a>");
                        findAttributeInSubattributesRecursively(subattrubutes, pId, pathBefore.append(pathWithCurrentAttr), result, mapOfAllAttributes);
                    }
                }
            }
            for (Box box : webDataService.getBoxs()) {
                StringBuffer pathBefore = new StringBuffer("</br> <a href=\"aswebdataBoxEdit?pId=" + box.getId() + "\" > Box [" + box.getName() + "]  </a> ->" +
                        " <a href=\"aswebdataBoxAttributesShow?ownerId=" + box.getId() + "\" > Attributes </a>");
                for (String attributeId : box.getAttributes()) {
                    Attribute attribute = mapOfAllAttributes.get(attributeId);
                    if (attribute == null)
                        continue;

                    if (attributeId.equalsIgnoreCase(pId)) {
                        result.add(pathBefore + "</br>");
                    }
                    List<String> subattrubutes = attribute.getSubattributes();
                    if (!subattrubutes.isEmpty()) {
                        StringBuffer pathWithCurrentAttr = new StringBuffer(" ->   <a href=\"aswebdataAttributeEdit?pId=" + attribute.getId() + "\" > Attribute [" + attribute.getName() + "]  </a> ->" +
                                "<a href=\"aswebdataAttributeSubattributesShow?ownerId=" + attribute.getId() + "\" > Subattributes </a>");
                        findAttributeInSubattributesRecursively(subattrubutes, pId, pathBefore.append(pathWithCurrentAttr), result, mapOfAllAttributes);
                    }
                }
            }
            for (Map.Entry<String, Attribute> entry : mapOfAllAttributes.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(pId))
                    continue;
                StringBuffer pathBefore = new StringBuffer("</br> <a href=\"aswebdataAttributeEdit?pId=" + entry.getValue().getId() + "\" > Attribute [" + entry.getValue().getName() + "]  </a> ->" +
                        " <a href=\"aswebdataAttributeSubattributesShow?ownerId=" + entry.getValue().getId() + "\" > Attributes </a>");
                for (String attributeId : entry.getValue().getSubattributes()) {
                    Attribute attribute = mapOfAllAttributes.get(attributeId);
                    if (attribute == null)
                        continue;

                    if (attributeId.equalsIgnoreCase(pId)) {
                        result.add(pathBefore + "</br>");
                    }
                    List<String> subattrubutes = attribute.getSubattributes();
                    if (!subattrubutes.isEmpty()) {
                        StringBuffer pathWithCurrentAttr = new StringBuffer(" ->   <a href=\"aswebdataAttributeEdit?pId=" + attribute.getId() + "\" > Attribute [" + attribute.getName() + "]  </a> ->" +
                                "<a href=\"aswebdataAttributeSubattributesShow?ownerId=" + attribute.getId() + "\" > Subattributes </a>");
                        findAttributeInSubattributesRecursively(subattrubutes, pId, pathBefore.append(pathWithCurrentAttr), result, mapOfAllAttributes);
                    }
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("IASWebDataService failed.", e);
        }
        return result;
    }

    private static void findAttributeInSubattributesRecursively(List<String> listOfSubattributes, String searcheElementId, StringBuffer pathBefore, List<String> resList, Map<String, Attribute> mapOfAllAttributes) {
        for (String subAttributeId : listOfSubattributes) {
            Attribute attribute = mapOfAllAttributes.get(subAttributeId);
            if (attribute == null)
                continue;

            if (subAttributeId.equalsIgnoreCase(searcheElementId)) {
                resList.add(pathBefore + "</br>");
            }
            List<String> subattrubutes = attribute.getSubattributes();
            if (!subattrubutes.isEmpty()) {
                StringBuffer pathWithCurrentAttr = new StringBuffer(" ->   <a href=\"aswebdataAttributeEdit?pId=" + attribute.getId() + "\" > Attribute [" + attribute.getName() + "]  </a> ->" +
                        "<a href=\"aswebdataAttributeSubattributesShow?ownerId=" + attribute.getId() + "\" > subattributes </a>");
                findAttributeInSubattributesRecursively(subattrubutes, searcheElementId, pathBefore.append(pathWithCurrentAttr), resList, mapOfAllAttributes);
            }

        }
    }

    private static List<String> findUsagesOfMediaLink(String scriptId) {
        List<String> result = new ArrayList<String>();
        try {
            for (Pagex p : webDataService.getPagexs()) {
                for (String id : p.getMediaLinks()) {
                    if (id.equalsIgnoreCase(scriptId)) {
                        result.add("</br><a href=\"aswebdataPagexEdit?pId=" + p.getId() + "\" > Page [" + p.getName() + "] </a> - " +
                                "<a href=\"aswebdataPagexScriptsShow?ownerId=" + p.getId() + "\" > MediaLinks </a>");
                    }
                }
            }
            for (Box b : webDataService.getBoxs()) {
                for (String id : b.getMediaLinks()) {
                    if (id.equalsIgnoreCase(scriptId)) {
                        result.add("</br><a href=\"aswebdataBoxEdit?pId=" + b.getId() + "\" > Box [" + b.getName() + "] </a> - " +
                                "<a href=\"aswebdataBoxScriptsShow?ownerId=" + b.getId() + "\" > MediaLinks </a>");
                    }
                }
            }
            for (PageTemplate pt : siteDataService.getPageTemplates()) {
                for (String id : pt.getMediaLinks()) {
                    if (id.equalsIgnoreCase(scriptId)) {
                        result.add("</br><a href=\"assitedataPageTemplateEdit?pId=" + pt.getId() + "\" > PageTemplete [" + pt.getName() + "] </a> - " +
                                "<a href=\"assitedataPageTemplateScriptsShow?ownerId=" + pt.getId() + "\" > MediaLinks </a>");
                    }
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use WebDataService.", e);
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use SiteDataService", e);
        }
        return result;
    }

    private static List<String> findUsagesOfScript(String scriptId) {
        List<String> result = new ArrayList<String>();
        try {
            for (Pagex p : webDataService.getPagexs()) {
                for (String id : p.getScripts()) {
                    if (id.equalsIgnoreCase(scriptId)) {
                        result.add("</br><a href=\"aswebdataPagexEdit?pId=" + p.getId() + "\" > Page [" + p.getName() + "] </a> - " +
                                "<a href=\"aswebdataPagexScriptsShow?ownerId=" + p.getId() + "\" > Scripts </a>");
                    }
                }
            }
            for (Box b : webDataService.getBoxs()) {
                for (String id : b.getScripts()) {
                    if (id.equalsIgnoreCase(scriptId)) {
                        result.add("</br><a href=\"aswebdataBoxEdit?pId=" + b.getId() + "\" > Box [" + b.getName() + "] </a> - " +
                                "<a href=\"aswebdataBoxScriptsShow?ownerId=" + b.getId() + "\" > Scripts </a>");
                    }
                }
            }
            for (PageTemplate pt : siteDataService.getPageTemplates()) {
                for (String id : pt.getScripts()) {
                    if (id.equalsIgnoreCase(scriptId)) {
                        result.add("</br><a href=\"assitedataPageTemplateEdit?pId=" + pt.getId() + "\" > PageTemplete [" + pt.getName() + "] </a> - " +
                                "<a href=\"assitedataPageTemplateScriptsShow?ownerId=" + pt.getId() + "\" > Scripts </a>");
                    }
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use WebDataService.", e);
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use SiteDataService", e);
        }
        return result;
    }

    private static List<String> findUsagesOfCustomHandler(String handlerId) {
        List<String> result = new ArrayList<String>();
        try {
            for (Box b : webDataService.getBoxs()) {
                if (b == null || b.getHandler() == null)
                    continue;
                String searchedId = "C-" + handlerId;
                if (searchedId.equalsIgnoreCase(b.getHandler())) {
                    result.add("</br><a href=\"aswebdataBoxEdit?pId=" + b.getId() + "\" > Box [" + b.getName() + "] </a>");
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use WebDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfGenericHandler(String handlerId) {
        List<String> result = new ArrayList<String>();
        try {
            for (Box b : webDataService.getBoxs()) {
                if (b == null || b.getHandler() == null)
                    continue;

                String searchedId = "G-" + handlerId;
                if (searchedId.equalsIgnoreCase(b.getHandler())) {
                    result.add("</br><a href=\"aswebdataBoxEdit?pId=" + b.getId() + "\" > Box [" + b.getName() + "] </a>");
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use WebDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfCustomType(String typeId) {
        List<String> result = new ArrayList<String>();
        try {
            for (Box b : webDataService.getBoxs()) {
                if (b == null || b.getType() == null)
                    continue;
                String searchedId = "C-" + typeId;
                if (searchedId.equalsIgnoreCase(b.getType())) {
                    result.add("</br><a href=\"aswebdataBoxEdit?pId=" + b.getId() + "\" > Box [" + b.getName() + "] </a>");
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use WebDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfGenericType(String typeId) {
        List<String> result = new ArrayList<String>();
        try {
            for (Box b : webDataService.getBoxs()) {
                if (b == null || b.getType() == null)
                    continue;
                String searchedId = "G-" + typeId;
                if (searchedId.equalsIgnoreCase(b.getType())) {
                    result.add("</br><a href=\"aswebdataBoxEdit?pId=" + b.getId() + "\" > Box [" + b.getName() + "]</a>");
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use WebDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfGenericGuard(String guardId) {
        List<String> result = new ArrayList<String>();
        String searchedId = "G-" + guardId;
        try {
            for (Box b : webDataService.getBoxs()) {
                for (String guard : b.getGuards()) {
                    if (searchedId.equalsIgnoreCase(guard)) {
                        result.add("</br><a href=\"aswebdataBoxEdit?pId=" + b.getId() + "\" > Box [" + b.getName() + "] </a> - " +
                                "<a href=\"aswebdataBoxGuardsShow?ownerId=" + b.getId() + "\" > Guards </a>");
                    }
                }
            }
            for (Attribute attribute : webDataService.getAttributes()) {
                for (String guard : attribute.getGuards()) {
                    if (searchedId.equalsIgnoreCase(guard)) {
                        result.add("</br><a href=\"aswebdataAttributeEdit?pId=" + attribute.getId() + "\" > Attribute [" + attribute.getName() + "] </a> - " +
                                "<a href=\"aswebdataAttributeGuardsShow?ownerId=" + attribute.getId() + "\" > Guards </a>");
                    }
                }
            }
            for (NaviItem naviItem : siteDataService.getNaviItems()) {
                for (String guard : naviItem.getGuards()) {
                    if (searchedId.equalsIgnoreCase(guard)) {
                        result.add("</br><a href=\"assitedataNaviItemEdit?pId=" + naviItem.getId() + "\" > NaviItem [" + naviItem.getName() + "] </a> - " +
                                "<a href=\"assitedataNaviItemGuardsShow?ownerId=" + naviItem.getId() + "\" > Guards </a>");
                    }
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use WebDataService.", e);
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use SiteDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfCustomGuard(String guardId) {
        List<String> result = new ArrayList<String>();
        String searchedId = "C-" + guardId;
        try {
            for (Box b : webDataService.getBoxs()) {
                for (String guard : b.getGuards()) {
                    if (searchedId.equalsIgnoreCase(guard)) {
                        result.add("</br><a href=\"aswebdataBoxEdit?pId=" + b.getId() + "\" > Box [" + b.getName() + "] </a> - " +
                                "<a href=\"aswebdataBoxGuardsShow?ownerId=" + b.getId() + "\" > Guards </a>");
                    }
                }
            }
            for (Attribute attribute : webDataService.getAttributes()) {
                for (String guard : attribute.getGuards()) {
                    if (searchedId.equalsIgnoreCase(guard)) {
                        result.add("</br><a href=\"aswebdataAttributeEdit?pId=" + attribute.getId() + "\" > Attributes [" + attribute.getName() + "] </a> - " +
                                "<a href=\"aswebdataAttributeGuardsShow?ownerId=" + attribute.getId() + "\" > Guards </a>");
                    }
                }
            }
            for (NaviItem naviItem : siteDataService.getNaviItems()) {
                for (String guard : naviItem.getGuards()) {
                    if (searchedId.equalsIgnoreCase(guard)) {
                        result.add("</br><a href=\"assitedataNaviItemEdit?pId=" + naviItem.getId() + "\" > NaviItem [" + naviItem.getName() + "] </a> - " +
                                "<a href=\"assitedataNaviItemGuardsShow?ownerId=" + naviItem.getId() + "\" > Guards </a>");
                    }
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use WebDataService.", e);
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use SiteDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfPageAlias(String pageAliasId) {
        List<String> result = new ArrayList<String>();
        try {
            for (NaviItem naviItem : siteDataService.getNaviItems()) {
                if (naviItem == null || naviItem.getPageAlias() == null)
                    continue;

                if (naviItem.getPageAlias().equalsIgnoreCase(pageAliasId)) {
                    result.add("</br><a href=\"assitedataNaviItemEdit?pId=" + naviItem.getId() + "\" > NaviItem [" + naviItem.getName() + "] </a>");
                }
            }
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use SiteDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfPage(String pageId) {
        List<String> result = new ArrayList<String>();
        try {
            for (Site site : siteDataService.getSites()) {
                if (site == null || site.getSearchpage() == null || site.getStartpage() == null)
                    continue;

                if (site.getStartpage().equalsIgnoreCase(pageId) || site.getSearchpage().equalsIgnoreCase(pageId)) {
                    result.add("</br><a href=\"assitedataSiteEdit?pId=" + site.getId() + "\" > Site [" + site.getName() + "] </a> ");
                }
            }
            for (NaviItem naviItem : siteDataService.getNaviItems()) {
                if (naviItem == null || naviItem.getInternalLink() == null)
                    continue;

                if (naviItem.getInternalLink().equalsIgnoreCase(pageId)) {
                    result.add("</br><a href=\"assitedataNaviItemEdit?pId=" + naviItem.getId() + "\" > NaviItem [" + naviItem.getName() + "] </a> ");
                }
            }
            for (EntryPoint entryPoint : siteDataService.getEntryPoints()) {
                if (entryPoint == null || entryPoint.getStartPage() == null)
                    continue;

                if (entryPoint.getStartPage().equalsIgnoreCase(pageId)) {
                    result.add("</br><a href=\"assitedataEntryPointEdit?pId=" + entryPoint.getId() + "\" > EntryPoint [" + entryPoint.getName() + "] </a> ");
                }
            }
            for (PageAlias pageAlias : siteDataService.getPageAliass()) {
                if (pageAlias == null || pageAlias.getTargetPage() == null)
                    continue;

                if (pageAlias.getTargetPage().equalsIgnoreCase(pageId)) {
                    result.add("</br><a href=\"assitedataPageAliasEdit?pId=" + pageAlias.getId() + "\" > PageAlias [" + pageAlias.getName() + "] </a> ");
                }
            }
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use SiteDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfSite(String siteId) {
        List<String> result = new ArrayList<String>();
        try {
            for (PageTemplate pageTemplate : siteDataService.getPageTemplates()) {
                if (pageTemplate == null || pageTemplate.getSite() == null)
                    continue;

                if (pageTemplate.getSite().equalsIgnoreCase(siteId)) {
                    result.add("</br><a href=\"assitedataPageTemplateEdit?pId=" + pageTemplate.getId() + "\" > PageTemplate [" + pageTemplate.getName() + "] </a> ");
                }
            }
            for (EntryPoint entryPoint : siteDataService.getEntryPoints()) {
                if (entryPoint == null || entryPoint.getStartSite() == null)
                    continue;

                if (entryPoint.getStartSite().equalsIgnoreCase(siteId)) {
                    result.add("</br><a href=\"assitedataEntryPointEdit?pId=" + entryPoint.getId() + "\" > EntryPoint [" + entryPoint.getName() + "] </a> ");
                }
            }
            for (PageAlias pageAlias : siteDataService.getPageAliass()) {
                if (pageAlias == null || pageAlias.getTargetPage() == null)
                    continue;

                if (pageAlias.getTargetPage().equalsIgnoreCase(siteId)) {
                    result.add("</br><a href=\"assitedataPageAliasEdit?pId=" + pageAlias.getId() + "\" > PageAlias [" + pageAlias.getName() + "] </a> ");
                }
            }
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use SiteDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfPageTemplate(String pageTemplateId) {
        List<String> result = new ArrayList<String>();
        try {
            for (Pagex p : webDataService.getPagexs()) {
                if (p == null || p.getTemplate() == null)
                    continue;

                if (p.getTemplate().equalsIgnoreCase(pageTemplateId)) {
                    result.add("</br><a href=\"aswebdataPagexEdit?pId=" + p.getId() + "\" > Page [" + p.getName() + "] </a>");
                }
            }
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use WebDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfLayout(String layoutId) {
        List<String> result = new ArrayList<String>();
        try {
            for (PageTemplate pageTemplate : siteDataService.getPageTemplates()) {
                if (pageTemplate == null || pageTemplate.getLayout() == null)
                    continue;

                if (pageTemplate.getLayout().equalsIgnoreCase(layoutId)) {
                    result.add("</br><a href=\"assitedataPageTemplateEdit?pId=" + pageTemplate.getId() + "\" > PageTemplate [" + pageTemplate.getName() + "] </a> ");
                }
            }
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use SiteDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfStyle(String styleId) {
        List<String> result = new ArrayList<String>();
        try {
            for (PageLayout pageLayout : layoutDataService.getPageLayouts()) {
                if (pageLayout == null || pageLayout.getStyle() == null)
                    continue;

                if (pageLayout.getStyle().equalsIgnoreCase(styleId)) {
                    result.add("</br><a href=\"aslayoutdataPageLayoutEdit?pId=" + pageLayout.getId() + "\" > Layout [" + pageLayout.getName() + "] </a> ");
                }
            }
        } catch (ASLayoutDataServiceException e) {
            LOGGER.error("failed to use LayoutDataService.", e);
        }
        return result;
    }

    private static List<String> findUsagesOfNaviItem(String naviItemId) {
        List<String> result = new ArrayList<String>();
        try {
            Map<String, NaviItem> mapOfAllNaviItems = new HashMap<String, NaviItem>();
            for (NaviItem naviItem : siteDataService.getNaviItems()) {
                mapOfAllNaviItems.put(naviItem.getId(), naviItem);
            }
            for (Map.Entry<String, NaviItem> entry : mapOfAllNaviItems.entrySet()) {
                for (String naviId : entry.getValue().getSubNavi()) {

                    StringBuffer pathBefore = new StringBuffer("</br><a href=\"assitedataNaviItemEdit?pId=" + entry.getValue().getId() + "\" > NaviItem [" + entry.getValue().getName() + "] </a> -> " +
                            "<a href=\"assitedataNaviItemSubNaviShow?ownerId=" + entry.getValue().getId() + "\" > SubNavi </a>");
                    NaviItem tempNaviItem = mapOfAllNaviItems.get(naviId);
                    if (tempNaviItem == null)
                        continue;

                    if (naviId.equalsIgnoreCase(naviItemId)) {
                        result.add(pathBefore + "</br>");
                    }
                    List<String> subNaviList = tempNaviItem.getSubNavi();
                    if (!subNaviList.isEmpty()) {
                        StringBuffer pathWithCurrentSubNavi = new StringBuffer(" -> </br><a href=\"assitedataNaviItemEdit?pId=" + tempNaviItem.getId() + "\" > NaviItem [" + tempNaviItem.getName() + "] </a> ->" +
                                "<a href=\"assitedataNaviItemSubNaviShow?ownerId=" + tempNaviItem.getId() + "\" > SubNavi </a>");
                        findNaviItemInSubNaviRecursively(subNaviList, naviItemId, pathBefore.append(pathWithCurrentSubNavi), result, mapOfAllNaviItems);
                    }
                }
            }

            for (Site site : siteDataService.getSites()) {
                if (site == null || site.getTopNavi() == null || site.getMainNavi() == null)
                    continue;

                if (!site.getTopNavi().isEmpty()) {
                    StringBuffer pathBeforeTopNavi = new StringBuffer("</br><a href=\"assitedataSiteEdit?pId=" + site.getId() + "\" > Site [" + site.getName() + "] </a> -> " +
                            "<a href=\"assitedataSiteTopNaviShow?ownerId=" + site.getId() + "\" > TopNavi </a>");
                    findNaviItemInSubNaviRecursively(site.getTopNavi(), naviItemId, pathBeforeTopNavi, result, mapOfAllNaviItems);
                }

                if (!site.getMainNavi().isEmpty()) {
                    StringBuffer pathBeforeMainNavi = new StringBuffer("</br><a href=\"assitedataSiteEdit?pId=" + site.getId() + "\" > Site [" + site.getName() + "] </a> -> " +
                            "<a href=\"assitedataSiteMainNaviShow?ownerId=" + site.getId() + "\" > TopNavi </a>");
                    findNaviItemInSubNaviRecursively(site.getTopNavi(), naviItemId, pathBeforeMainNavi, result, mapOfAllNaviItems);
                }
                //TODO implement search of naviitem through site document
            }

        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use SiteDataService.", e);
        }
        return result;
    }

    private static void findNaviItemInSubNaviRecursively(List<String> listOfSubNavi, String searcheElementId, StringBuffer pathBefore, List<String> resList, Map<String, NaviItem> mapOfAllNaviItems) {
        for (String subNaviId : listOfSubNavi) {
            NaviItem naviItem = mapOfAllNaviItems.get(subNaviId);
            if (naviItem == null)
                continue;

            if (subNaviId.equalsIgnoreCase(searcheElementId)) {
                resList.add(pathBefore + "</br>");
            }
            List<String> subNaviList = naviItem.getSubNavi();
            if (!subNaviList.isEmpty()) {
                StringBuffer pathWithCurrentAttr = new StringBuffer(" ->  <a href=\"assitedataNaviItemEdit?pId=" + naviItem.getId() + "\" > NaviItem [" + naviItem.getName() + "] </a>" +
                        "</br><a href=\"assitedataNaviItemSubNaviShow?ownerId=" + naviItem.getId() + "\" > SubNavi </a>");
                findNaviItemInSubNaviRecursively(subNaviList, searcheElementId, pathBefore.append(pathWithCurrentAttr), resList, mapOfAllNaviItems);
            }
        }
    }

    private static List<String> findUsagesOfLocalizationBundle(String bundleId) {
        List<String> result = new ArrayList<String>();

        try {
            result.addAll(findUsagesOfLocalizationBundleInBoxes(bundleId));
            result.addAll(findUsagesOfLocalizationBundleInPages(bundleId));
            result.addAll(findUsagesOfLocalizationBundleInTemplates(bundleId));
        } catch (ASWebDataServiceException e) {
            LOGGER.error("failed to use ASWebDataService.", e);
        } catch (ASSiteDataServiceException e) {
            LOGGER.error("failed to use ASSiteDataService.", e);
        }

        return result;
    }

    private static List<String> findUsagesOfLocalizationBundleInBoxes(String bundleId) throws ASWebDataServiceException {
        List<String> responses = new ArrayList<String>();

        for (Box box : webDataService.getBoxs()) {
            String response = new ResponseBuilder()
                    .setContainerUrlType("aswebdataBox")
                    .setContainerType("Box")
                    .setContainerId(box.getId())
                    .setContainerName(box.getName())
                    .setContentType("Localizations")
                    .build();

            responses.addAll(findUsages(bundleId, box.getLocalizations(), response));
        }

        return responses;
    }

    private static List<String> findUsagesOfLocalizationBundleInPages(String bundleId) throws ASWebDataServiceException {
        List<String> responses = new ArrayList<String>();

        for (Pagex pagex : webDataService.getPagexs()) {
            String response = new ResponseBuilder()
                    .setContainerUrlType("aswebdataPagex")
                    .setContainerType("Page")
                    .setContainerId(pagex.getId())
                    .setContainerName(pagex.getName())
                    .setContentType("Localizations")
                    .build();
            responses.addAll(findUsages(bundleId, pagex.getLocalizations(), response));
        }

        return responses;
    }

    private static List<String> findUsagesOfLocalizationBundleInTemplates(String bundleId) throws ASSiteDataServiceException {
        List<String> responses = new ArrayList<String>();

        for (PageTemplate pageTemplate : siteDataService.getPageTemplates()) {
            String response = new ResponseBuilder()
                    .setContainerUrlType("assitedataPageTemplate")
                    .setContainerType("PageTemplate")
                    .setContainerId(pageTemplate.getId())
                    .setContainerName(pageTemplate.getName())
                    .setContentType("Localizations")
                    .build();

            responses.addAll(findUsages(bundleId, pageTemplate.getLocalizations(), response));
        }

        return responses;
    }

    private static List<String> findUsages(String contentId, List<String> containerElemIds,
                                           String response) {
        List<String> responses = new ArrayList<String>();

        for (String otherId : containerElemIds) {
            if (otherId.equals(contentId)) {
                responses.add(response);
            }
        }

        return responses;
    }

    /**
     * The class allows build show usage response by predefined template
     */
    private static class ResponseBuilder {
        public static final String RESPONSE_TEMPLATE =
                "</br><a href=\"%sEdit?pId=%s\"> %s [%s] </a> -> " +
                "<a href=\"%s%sShow?ownerId=%s\"> %s </a>";

        private String containerUrlType;
        private String containerType;
        private String containerId;
        private String containerName;
        private String contentType;

        public ResponseBuilder setContainerUrlType(String containerUrlType) {
            this.containerUrlType = containerUrlType;
            return this;
        }

        public ResponseBuilder setContainerType(String containerType) {
            this.containerType = containerType;
            return this;
        }

        public ResponseBuilder setContainerId(String containerId) {
            this.containerId = containerId;
            return this;
        }

        public ResponseBuilder setContainerName(String containerName) {
            this.containerName = containerName;
            return this;
        }

        public ResponseBuilder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public String build() {
            return String.format(RESPONSE_TEMPLATE,
                    containerUrlType,
                    containerId,
                    containerType,
                    containerName,
                    containerUrlType,
                    contentType,
                    containerId,
                    contentType
            );
        }
    }
}
