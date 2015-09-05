package net.kaikk.mc.krules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rules {

	private List<List<String>> pagesRules;
	private int pages;
	private Map<UUID, List<Integer>> pagesRead;
	
	Rules(List<String> rulesRaw) throws Exception {
		pagesRead = new HashMap<UUID, List<Integer>>();
		pagesRules = new ArrayList<List<String>>();
		pages = 0;
		processRules(rulesRaw);
	}
	Rules()	{
		throw new UnsupportedOperationException();
	}
	private void processRules(List<String> rulesRaw) throws Exception {
		Pattern pattern = Pattern.compile("(^[0-9]+:$)");

		Matcher m = pattern.matcher(rulesRaw.get(0));
		if(!m.matches()){
			throw new Exception("impropper rules format");
		}
		
		int lastPage = 0;
		for(String line : rulesRaw) {
			m = pattern.matcher(line);
			if(m.matches()){
				if(Integer.parseInt(line.substring(0, line.indexOf(":"))) != lastPage+1){
					throw new Exception("impropper rules format");
				}
				lastPage++;
				pagesRules.add(new ArrayList<String>());
				pages++;
			}
			else
				pagesRules.get(lastPage-1).add(line);			
			
		}
	}
	public int getPageCount() {
		return pages;		
	}
	public List<String> getAllRules() {
		List<String> temp = new ArrayList<String>();
		for(List<String> page : pagesRules) {		
			for(String rule : page)	{
				temp.add(rule);
			}
		}
		return temp;
	}
	public List<String> readRules(int page, UUID player) throws IndexOutOfBoundsException {
		if(page < 1 || page > pagesRules.size())
			throw new IndexOutOfBoundsException("Not a page");
		
		if(!pagesRead.containsKey(player)) {
			pagesRead.put(player, Arrays.asList(page));
		}
		else if(pagesRead.get(player).contains(page)) {
			return pagesRules.get(page-1);
		}
		else {
			List<Integer> newList = new ArrayList<Integer>();
			newList.addAll(pagesRead.get(player));
			newList.add(page);
			pagesRead.replace(player, newList);
		}
		return pagesRules.get(page-1);
	}
	public List<Integer> pagesAlreadyRead(UUID player) {
		if(!pagesRead.containsKey(player)) {
			return null;			
		}
		return pagesRead.get(player);
	}
	public boolean hasReadAllRules(UUID player){
		if(pagesRead.isEmpty())
			return false;
		if(pagesRead.get(player).size() == pages)
			return true;
		return false;
		
	}
	
}

