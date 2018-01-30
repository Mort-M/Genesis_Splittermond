/**
 * 
 */
package org.prelle.rpgframework.splittermond.merchant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Availability;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Complexity;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.items.Enhancement.EnhancementType;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;

/**
 * @author prelle
 *
 */
public class Shop {
	
	private final static Logger logger = Logger.getLogger("splittermond.merchant");
	
	private Availability maxAvail;
	private Complexity   maxComplex;
	private List<ItemType> types;

	//-------------------------------------------------------------------
	/**
	 */
	public Shop(Availability maxAvail, Complexity maxComplex, ItemType... types) {
		this.maxAvail = maxAvail;
		this.maxComplex = maxComplex;
		this.types    = Arrays.asList(types);
	}

	//-------------------------------------------------------------------
	private boolean isAllowedItemType(ItemTemplate template) {
		for (ItemType type : types) {
			if (template.isType(type)) 
				return true;
		}
		return false;
	}

	//-------------------------------------------------------------------
	private boolean isAllowedAvailability(ItemTemplate template) {
		return template.getAvailability().ordinal()<=maxAvail.ordinal();
	}

	//-------------------------------------------------------------------
	private boolean isAllowedComplexity(ItemTemplate template) {
		return template.getComplexity().ordinal()<=maxComplex.ordinal();
	}

	//-------------------------------------------------------------------
	public List<CarriedItem> generateItems() {
		Random random = new Random();
		List<CarriedItem> items = new ArrayList<>();
		for (ItemTemplate template : SplitterMondCore.getItems()) {
			// Is it a allowed item type
			if (!isAllowedItemType(template))
				continue;
			if (!isAllowedAvailability(template))
				continue;
			if (!isAllowedComplexity(template))
				continue;
			
			// This is a valid item
			CarriedItem item = new CarriedItem();
			item.setItem(template);
			items.add(item);
			
			/*
			 * Build variants. 
			 * The higher the normal complexity, the more likely are variants
			 */
			int numVariants = template.getComplexity().ordinal();
			int spanMin = -1;
			int spanMax =  1 + (maxComplex.ordinal() - template.getComplexity().ordinal())*2;
			for (int i=0; i<numVariants; i++) {
				int quality = 0;
				logger.debug("Range from "+spanMin+" to "+spanMax +" is "+(spanMax - spanMin));
				while (quality==0) {
					quality = random.nextInt(spanMax - spanMin);
					quality += spanMin;
				}
				logger.debug("Add "+quality+" of "+item.getName());
				
				item = new CarriedItem();
				item.setItem(template);
				items.add(item);
				int pool = quality;
				if (pool>0) {
					while (pool>0) {
						int index = random.nextInt(SplitterMondCore.getEnhancements().size());
						Enhancement enhance = SplitterMondCore.getEnhancements().get(index);
						// Does the enhancement match the item types?
						for (ItemType allowed : enhance.getItemTypeLimitations()) {
							if (template.isType(allowed)) {
								if (enhance.getSize()<=pool && enhance.getType()==EnhancementType.NORMAL) {
									boolean added = item.addEnhancement(new EnhancementReference(enhance));
									if (added) {
										pool -= enhance.getSize();
										logger.debug("  with enhancement "+enhance);
									}
								}
							}
						}
						
					}
				}
			}
		}
		
		return items;
	}

}
