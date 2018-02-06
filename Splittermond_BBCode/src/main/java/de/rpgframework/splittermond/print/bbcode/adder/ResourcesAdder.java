package de.rpgframework.splittermond.print.bbcode.adder;

import java.util.List;

import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;

import de.rpgframework.splittermond.SpliMoLabels;
import de.rpgframework.splittermond.print.bbcode.SingleBBCodeGenerator;

/**
 * Adds the resources and their values. Their descriptions are added if they are
 * not empty.
 * 
 * @author frank.buettner
 * 
 */
public class ResourcesAdder extends AbstractAdder {

	public ResourcesAdder(StringBuilder bbcodeBuilder,
			SpliMoCharacter spliMoCharacter) {
		super(bbcodeBuilder, spliMoCharacter);
	}

	@Override
	public void add() {
		List<ResourceReference> resources = spliMoCharacter.getResources();
		for (ResourceReference resourceReference : resources) {
			StringBuilder resourceBuilder = new StringBuilder();
			String name = resourceReference.getResource().getName();
			int value = resourceReference.getValue();

			String key = String.format("%s - %s",
					SpliMoLabels.getLabelResource(), name);
			// value
			StringBuilder valueBuilder = new StringBuilder().append(value);

			// notes
			String desciption = resourceReference.getDescription();
			if (desciption!=null && !desciption.trim().isEmpty()) {

				valueBuilder.append(String.format(". %s: %s",
						SpliMoLabels.getLabelNotes(), desciption));
			}

			SingleBBCodeGenerator.addGenericAttribute(resourceBuilder, key,
					valueBuilder.toString());
			bbcodeBuilder.append(resourceBuilder.toString());
		}
	}
}
