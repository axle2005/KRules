package net.kaikk.mc.krules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.TextMessageException;

public class Rules {
	private KRules instance;
	private List<String> rawRules;
	public PaginationBuilder pages;
	private int hoverWidth = 80;
	
	Rules(KRules instance) throws IOException, TextMessageException{
		this.instance= instance;
		setup();
	}
	
	Rules()	{
		throw new UnsupportedOperationException();
	}
	
	private void setup() throws IOException, TextMessageException{
		rawRules = new ArrayList<String>();
		new File("mods"+File.separator+"KRules").mkdirs();
		File fRules = new File("mods"+File.separator+"KRules"+File.separator+"rules.txt");
		FileInputStream input;
	    try {
			if (!fRules.exists()) {
				PrintWriter writer;
				writer = new PrintWriter(fRules.toPath().toString());
				writer.println("{text:\"I am the Title (always the first line, must be present)\",color:\"red\"}");
				writer.println("I am rule 1, no json here, it's totally optional");
				writer.println("{text:\"I am rule 2, I use json for formatting\",color:\"green\"}");
				writer.println("	I am rule 2 hover over details that are automagically wrapped at 80 chars(first char is tab, I am optional, do not use json on me)");
				writer.println("I am rule 3, jsons should be kept on a single line");
				writer.println("{text:\"I am click to accept text (always last line, must be present)\",color:\"blue\",underlined:\"true\"}");
				writer.close();
			}
	        input = new FileInputStream(fRules);
	        CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();
	        decoder.onMalformedInput(CodingErrorAction.IGNORE);
	        InputStreamReader reader = new InputStreamReader(input, decoder);
	        BufferedReader bufferedReader = new BufferedReader( reader );
	        String line = bufferedReader.readLine();
	        while( line != null ) {
	            rawRules.add(line);
	            line = bufferedReader.readLine();
	        }
	        bufferedReader.close();
	    } catch( IOException e ) {
	        throw e;
	    }
	    
		processRules();
			
	}
	private void processRules() throws IOException, TextMessageException {
		List<Text> contents = new ArrayList<>();
		TextBuilder lastProcessedRule = null;
		
		for (int x = 1; x <= rawRules.size(); x++) {			
			String line = rawRules.get(x);
			if (x == rawRules.size() - 1) {
				contents.add(Texts.json().fromUnchecked(line).builder().onClick(TextActions.executeCallback(src -> instance.acceptRules(src))).build());
				break;
			} else if (line.indexOf('	') > -1) {
				if (lastProcessedRule == null) {
					throw new UnsupportedOperationException("Impropperly formatted rules.txt delete it for an example.");
				}
				if (rawRules.get(x + 1).indexOf('	') > -1) {
					throw new UnsupportedOperationException("Impropperly formatted rules.txt delete it for an example. (One hover per rule)");
				}
				if (line.length() > hoverWidth) {
					outerLoop:
					for (int y = hoverWidth; y < line.length(); y += hoverWidth) {
						while (line.charAt(y)!= ' ') {
							if ( y - 1 < 0) {
								break outerLoop;
							}
							y--;
						}
						line = new StringBuilder(line).replace(y, y+1, "\n").toString();
						if (y + hoverWidth >= line.length()) {
							break;
						}
					}
				}
				lastProcessedRule = lastProcessedRule.onHover(TextActions.showText(Texts.of(line.substring(1, line.length()))));
			} else {
				lastProcessedRule = Texts.json().fromUnchecked(line).builder();
				if (rawRules.get(x + 1).indexOf('	') > -1) {
					continue;
				}
			}
			contents.add(lastProcessedRule.build());
		}
		PaginationService paginationService = instance.game.getServiceManager().provide(PaginationService.class).get();
		pages = paginationService.builder().contents(contents).title(Texts.json().fromUnchecked(rawRules.get(0)));
				
	}
}

