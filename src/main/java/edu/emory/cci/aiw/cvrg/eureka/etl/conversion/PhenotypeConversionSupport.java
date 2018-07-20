package edu.emory.cci.aiw.cvrg.eureka.etl.conversion;

/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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
import org.eurekaclinical.eureka.client.comm.Phenotype;
import org.protempa.proposition.value.NominalValue;

/**
 *
 * @author Andrew Post
 */
public class PhenotypeConversionSupport {

	public PhenotypeConversionSupport() {
	}

	public String toPropositionIdWrapped(String phenotypeKey) {
		return phenotypeKey + ConversionUtil.PROP_ID_WRAPPED_SUFFIX;
	}

	public String toPropositionIdWrapped(Phenotype phenotype) {
		return toPropositionIdWrapped(phenotype.getKey());
	}

	public String toPropositionId(String phenotypeKey) {
		return phenotypeKey + ConversionUtil.PRIMARY_PROP_ID_SUFFIX;
	}

	public String toPropositionId(Phenotype phenotype) {
		return toPropositionId(phenotype.getKey());
	}

	public String toPhenotypeKey(String propId) {
		int lastIndexOf
				= propId.lastIndexOf(ConversionUtil.PRIMARY_PROP_ID_SUFFIX);
		return propId.substring(0, lastIndexOf);
	}

	public String asValueString(String phenotypeKey) {
		return ConversionUtil.VALUE;
	}
	
	public String asValueCompString(String phenotypeKey) {
		return ConversionUtil.VALUE_COMP;
	}

	public String asValueString(Phenotype phenotype) {
		return asValueString(phenotype.getKey());
	}
	
	public String asValueCompString(Phenotype phenotype) {
		return asValueCompString(phenotype.getKey());
	}

	public NominalValue asValue(Phenotype phenotype) {
		return NominalValue.getInstance(asValueString(phenotype));
	}

	public NominalValue asValue(String phenotypeKey) {
		return NominalValue.getInstance(asValueString(phenotypeKey));
	}

}
