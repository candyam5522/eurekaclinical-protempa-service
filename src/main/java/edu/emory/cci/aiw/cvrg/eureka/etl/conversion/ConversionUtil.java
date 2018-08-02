/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * This program is dual licensed under the Apache 2 and GPLv3 licenses.
 * 
 * Apache License, Version 2.0:
 * 
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
 * 
 * GNU General Public License version 3:
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package edu.emory.cci.aiw.cvrg.eureka.etl.conversion;

//import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;
//import org.eurekaclinical.phenotype.service.entity.ExtendedPhenotype;
//import org.eurekaclinical.phenotype.service.entity.RelationOperator;
//import org.eurekaclinical.phenotype.service.entity.TimeUnit;
//import org.eurekaclinical.phenotype.service.entity.ValueThresholdEntity;
//import org.eurekaclinical.phenotype.service.entity.ValueThresholdGroupEntity;
import java.util.List;
import org.eurekaclinical.eureka.client.comm.Phenotype;
import org.eurekaclinical.eureka.client.comm.PhenotypeField;
import org.protempa.ContextDefinition;
import org.protempa.ContextOffset;
import org.protempa.proposition.interval.Interval.Side;
import org.protempa.PropertyConstraint;
import org.protempa.SimpleGapFunction;
import org.protempa.TemporalExtendedParameterDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;

import org.eurekaclinical.phenotype.client.comm.RelationOperator;
import org.eurekaclinical.phenotype.client.comm.TimeUnit;

/**
 *
 * @author Andrew Post
 */
class ConversionUtil {

	static final String PRIMARY_PROP_ID_SUFFIX = "_PRIMARY";
	static final String VALUE = "YES";
	static final String VALUE_COMP = "NO";
	static final String PROP_ID_WRAPPED_SUFFIX = "_WRAPPED";
	static final String USER_KEY_PREFIX = "USER:";
	
	private static final PhenotypeConversionSupport CONVERSION_SUPPORT =
			new PhenotypeConversionSupport();

	static AbsoluteTimeUnit unit(TimeUnit unit) {
		return unit != null ? AbsoluteTimeUnit.nameToUnit(unit.getName())
				: null;
	}
        
        //TODO: replace this hard-coded function with RESTFUL to phenotype/protected/timeunits
        static AbsoluteTimeUnit unit(Long unit) {
                String unitName;
                switch (unit.intValue()){
                    case 1:
                        unitName = "day";
                        break;
                    case 2:
                        unitName = "hour";
                        break;
                    case 3:
                        unitName = "minute";
                        break;
                    default:
                        unitName = null;
                }
		return unitName != null ? AbsoluteTimeUnit.nameToUnit(unitName)
				: null;
	}
        //Frequency Type Name
        static String frequencyTypeName(Long frequencyType) {
                String frequencyTypeName;
                switch (frequencyType.intValue()){
                    case 1:
                        frequencyTypeName = "at least";
                        break;
                    case 2:
                        frequencyTypeName = "first";
                        break;
                    default:
                        frequencyTypeName = null;
                }
		return frequencyTypeName;
	}

	static TemporalExtendedPropositionDefinition buildExtendedPropositionDefinition(PhenotypeField phenotypeField) {
		TemporalExtendedPropositionDefinition tepd =
				buildExtendedPropositionDefinitionBarebone(phenotypeField);
		if (phenotypeField.getHasPropertyConstraint()) {
			PropertyConstraint pc = new PropertyConstraint();
			pc.setPropertyName(
					phenotypeField.getProperty());
			pc.setValue(ValueType.VALUE.parse(phenotypeField.getPropertyValue()));
			pc.setValueComp(ValueComparator.EQUAL_TO);

			tepd.setPropertyConstraints(new PropertyConstraint[] {pc});
		}
		tepd.setMinLength(phenotypeField.getMinDuration());
		tepd.setMinLengthUnit(unit(phenotypeField.getMaxDurationUnits()));
		tepd.setMaxLength(phenotypeField.getMaxDuration());
		tepd.setMaxLengthUnit(unit(phenotypeField.getMinDurationUnits()));
		return tepd;
	}

	static ContextDefinition extractContextDefinition(
			ValueThresholds entity,
			List<ExtendedPhenotype> extendedPhenotypes,
			ValueThreshold v) {
		ContextDefinition cd = new ContextDefinition(
				entity.getKey() + "_SUB_CONTEXT");
		cd.setGapFunction(new SimpleGapFunction(0, null));
		TemporalExtendedPropositionDefinition[] tepds =
				new TemporalExtendedPropositionDefinition[extendedPhenotypes.size()];
		int i = 0;
		for (ExtendedPhenotype ede : extendedPhenotypes) {
			PhenotypeEntity dee = ede.getPhenotypeEntity();
			TemporalExtendedPropositionDefinition tepd;
			String tepdId = dee.getKey();
			if (!dee.isInSystem()) {
				tepdId = CONVERSION_SUPPORT.toPropositionId(tepdId);
			}
			if (dee instanceof ValueThresholdGroupEntity) {
				TemporalExtendedParameterDefinition teParamD =
						new TemporalExtendedParameterDefinition(tepdId);
				teParamD.setValue(CONVERSION_SUPPORT.asValue(dee));
				tepd = teParamD;
			} else {
				tepd = new TemporalExtendedPropositionDefinition(tepdId);
			}
			tepd.setDisplayName(dee.getDisplayName());
			tepd.setMaxLength(ede.getMaxDuration());
			tepd.setMaxLengthUnit(
					unit(ede.getMaxDurationTimeUnit()));
			tepd.setMinLength(ede.getMinDuration());
			tepd.setMinLengthUnit(
					unit(ede.getMinDurationTimeUnit()));
			tepds[i++] = tepd;
		}
		cd.setInducedBy(tepds);
		ContextOffset offset = new ContextOffset();
		RelationOperator relOp = v.getRelationOperator();
		Integer withinAtLeast = v.getWithinAtLeast();
		Integer withinAtMost = v.getWithinAtMost();
		String relOpName = relOp.getName();
		if ("before".equals(relOpName)) {
			offset.setStartIntervalSide(Side.FINISH);
			offset.setFinishIntervalSide(Side.FINISH);
			if (withinAtLeast != null) {
				offset.setStartOffset(withinAtLeast);
			}
			offset.setStartOffsetUnits(unit(v.getWithinAtLeastUnits()));
			offset.setFinishOffset(withinAtMost);
			offset.setFinishOffsetUnits(unit(v.getWithinAtMostUnits()));
		} else if ("after".equals(relOpName)) {
			offset.setStartIntervalSide(Side.START);
			offset.setFinishIntervalSide(Side.START);
			offset.setStartOffset(withinAtMost != null ? -withinAtMost : null);
			offset.setStartOffsetUnits(unit(v.getWithinAtMostUnits()));
			if (withinAtLeast != null) {
				offset.setFinishOffset(-withinAtLeast);
			}
			offset.setFinishOffsetUnits(unit(v.getWithinAtLeastUnits()));
		} else if ("around".equals(relOpName)) {
			offset.setStartIntervalSide(Side.START);
			offset.setFinishIntervalSide(Side.FINISH);
			offset.setStartOffset(withinAtLeast != null ? -withinAtLeast : null);
			offset.setStartOffsetUnits(unit(v.getWithinAtLeastUnits()));
			offset.setFinishOffset(withinAtMost);
			offset.setFinishOffsetUnits(unit(v.getWithinAtMostUnits()));
		}
		cd.setOffset(offset);
		return cd;
	}

	private static TemporalExtendedPropositionDefinition buildExtendedPropositionDefinition(String propId,
			PhenotypeField phenotypeFiled) {
		TemporalExtendedPropositionDefinition tepd;
		if (phenotypeFiled.getType()==Phenotype.Type.VALUE_THRESHOLD) {
			TemporalExtendedParameterDefinition tepvDef =
					new TemporalExtendedParameterDefinition(propId);
			tepvDef.setValue(CONVERSION_SUPPORT.asValue(phenotypeFiled.getPhenotypeKey()));
			tepd = tepvDef;
		} else {
			tepd = new TemporalExtendedPropositionDefinition(propId);
		}
		return tepd;
	}

	private static TemporalExtendedPropositionDefinition buildExtendedPropositionDefinitionBarebone(
			PhenotypeField phenotypeFiled) {
		String propId;
		if (phenotypeFiled.isInSystem()) {
			propId = phenotypeFiled.getPhenotypeKey();
		} else {
			propId = CONVERSION_SUPPORT.toPropositionId(phenotypeFiled.getPhenotypeKey());
		}
		return buildExtendedPropositionDefinition(propId, phenotypeFiled);
	}
}
