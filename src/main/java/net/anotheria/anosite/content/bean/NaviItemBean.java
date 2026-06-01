package net.anotheria.anosite.content.bean;

import java.util.ArrayList;
import java.util.List;

public class NaviItemBean {
    private boolean popup;
    private String name;
    private String icon;
    private String link;
    private String title;
    private boolean selected;
    private String className;

    private List<NaviItemBean> subNavi;

    public NaviItemBean() {
        subNavi = new ArrayList<NaviItemBean>();
    }

    public void addNaviItem(NaviItemBean toAdd) {
        subNavi.add(toAdd);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isPopup() {
        return popup;
    }

    public void setPopup(boolean popup) {
        this.popup = popup;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHasChilds() {
        return subNavi != null && subNavi.size() > 0;
    }

    public List<NaviItemBean> getSubNavi() {
        return subNavi;
    }

    public void setSubNavi(List<NaviItemBean> subNavi) {
        this.subNavi = subNavi;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "NaviItemBean [popup=" + popup + ", name=" + name + ", icon=" + icon + ", link=" + link + ", title=" + title + ", selected=" + selected + ", className=" + className + ", subNavi=" + subNavi
                + "]";
    }

}
