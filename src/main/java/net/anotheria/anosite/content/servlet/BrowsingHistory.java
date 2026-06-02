package net.anotheria.anosite.content.servlet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BrowsingHistory {
	
	public static final int MAX_SIZE = 5;
	
	private List<String> browsingHistoryList = new CopyOnWriteArrayList<String>();
	
	public void addHistoryItem(String id){
		String lastElement = browsingHistoryList.size()==0 ? "" : browsingHistoryList.get(browsingHistoryList.size()-1);
		if (!lastElement.equals(id))
			browsingHistoryList.add(id);
		if (browsingHistoryList.size()>MAX_SIZE*2)
			browsingHistoryList = browsingHistoryList.subList(MAX_SIZE, browsingHistoryList.size());
	}
	
	public List<String> getHistoryItems(){
		return browsingHistoryList;
	}
	
	public String getPreviousItem(){
		if (browsingHistoryList.size()<2)
			return null;
		return browsingHistoryList.get(browsingHistoryList.size()-2);
	}
	
	@Override public String toString(){
		StringBuilder ret = new StringBuilder();
		
		for (String s : browsingHistoryList){
			if (ret.length()>0)
				ret.append("->");
			ret.append(s);
		}
		
		return ret.toString();
	}
}
