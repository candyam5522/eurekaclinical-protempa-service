/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package edu.emory.cci.aiw.cvrg.eureka.etl.conversion;

//import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eurekaclinical.eureka.client.comm.Phenotype;
import org.protempa.PropositionDefinition;


/**
 *
 * @author Andrew Post
 */
public class PropositionDefinitionCollector {

	public static PropositionDefinitionCollector getInstance(
			PropositionDefinitionConverterVisitor converterVisitor,
			List<Phenotype> phenotypes) {
		List<PropositionDefinition> userProps
				= new ArrayList<>();
		List<String> toShow = new ArrayList<>();
		for (Phenotype de : phenotypes) {
			de.accept(converterVisitor);
			Collection<PropositionDefinition> propDefs
					= converterVisitor.getPropositionDefinitions();
			userProps.addAll(propDefs);
			toShow.add(converterVisitor.getPrimaryPropositionId());
		}
		PropositionDefinitionCollector result
				= new PropositionDefinitionCollector();
		result.userPropDefs = userProps;
		result.toShowPropDefs = toShow;
		return result;
	}

	private List<PropositionDefinition> userPropDefs;
	private List<String> toShowPropDefs;

	private PropositionDefinitionCollector() {

	}

	public List<PropositionDefinition> getUserPropDefs() {
		return userPropDefs;
	}

	public List<String> getToShowPropDefs() {
		return toShowPropDefs;
	}

}
