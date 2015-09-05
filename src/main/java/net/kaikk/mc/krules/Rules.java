package net.kaikk.mc.krules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rules {

	private List<List<String>> pagesRules;
	private int pages;
	private Map<UUID, List<Integer>> pagesRead;
	
	Rules(List<String> rules) throws Exception {
		processRules(rules);
	}
	Rules()	{
		throw new UnsupportedOperationException();
	}
	private void processRules(List<String> rules) throws Exception {
		Pattern pattern = Pattern.compile("([0-9]+:.*)");
		for(String rule : rules) {
			Matcher m = pattern.matcher(rule);
			if(m.matches()) {
				int curr = Integer.parseInt(rule.substring(0,rule.indexOf(":")));
				if(curr > (pagesRules.size()+1) || curr < pagesRules.size()) {
					throw new Exception("Improper rules format, put pages in order.");
				}
				if(pagesRules.size() == curr) {
					pagesRules.get(curr).add(rule);
				}
				else if(pagesRules.size() < curr) {
					pagesRules.add(new ArrayList<String>(Arrays.asList(rule)));
					this.pages++;
				}
			}
			else
				throw new Exception("Improper rules format.");
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
			return pagesRules.get(page);
		}
		else {
			pagesRead.get(player).add(page);
		}
		return pagesRules.get(page);
	}
	public List<Integer> pagesAlreadyRead(UUID player) {
		if(!pagesRead.containsKey(player)) {
			return null;			
		}
		return pagesRead.get(player);
	}
	public boolean hasReadAllRules(UUID player){
		if(pagesRead.get(player).size() == pages)
			return true;
		return false;
		
	}
	
}

