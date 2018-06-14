/*
 * Copyright 2018 Leibniz-Institut für Analytische Wissenschaften – ISAS – e.V..
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
 */
package de.isas.mztab2.validation.handlers;

import de.isas.mztab2.cvmapping.CvMappingUtils;
import de.isas.mztab2.cvmapping.CvParameterLookupService;
import de.isas.mztab2.cvmapping.ParameterComparisonResult;
import de.isas.mztab2.cvmapping.RuleEvaluationResult;
import de.isas.mztab2.model.Parameter;
import de.isas.mztab2.validation.CvRuleHandler;
import info.psidev.cvmapping.CvMappingRule;
import info.psidev.cvmapping.CvTerm;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author nilshoffmann
 */
@Slf4j
public class ResolvingCvRuleHandler implements CvRuleHandler {

    private final CvParameterLookupService client;

    public ResolvingCvRuleHandler(CvParameterLookupService client) {
        this.client = client;
    }

    @Override
    public RuleEvaluationResult handleRule(CvMappingRule rule,
        List<Pair<Pointer, ? extends Parameter>> filteredSelection) {

        final Map<String, Parameter> allowedParameters = new LinkedHashMap<>();
        final Map<String, Pair<Pointer, ? extends Parameter>> foundParameters = new LinkedHashMap<>();

        log.debug("Evaluating rule " + rule.getId() + " on " + rule.
            getCvElementPath());
        if (rule.getCvTermsCombinationLogic() == CvMappingRule.CvTermsCombinationLogic.AND) {
            if (rule.getCvTerm().
                size() > 1) {
                for (CvTerm term : rule.getCvTerm()) {
                    if (term.isAllowChildren()) {
                        throw new IllegalArgumentException(
                            CvMappingUtils.niceToString(rule) + " uses 'AND' combination logic with multiple CvTerms and allowChildren=true! Please change to OR or XOR logic!");
                    }
                }
            }
        }
        rule.getCvTerm().
            forEach((cvTerm) ->
            {
                for (Pair<Pointer, ? extends Parameter> pair : filteredSelection) {
                    if (cvTerm.isAllowChildren()) {
                        log.debug("Resolving children of " + cvTerm.
                            getTermAccession() + " against " + pair.getValue().
                                getCvAccession());
                        //resolve children
                        try {
                            ParameterComparisonResult result = client.
                                isChildOfOrSame(CvMappingUtils.asParameter(
                                    cvTerm),
                                    pair.getValue());
                            switch (result) {
                                case CHILD_OF:
                                    log.debug(pair.getValue().
                                        getCvAccession() + " is a child of " + cvTerm.
                                            getTermAccession());
                                    //use key of found parameter, since it is a child of cvTerm / cvTerm is a parent of the child
                                    allowedParameters.put(pair.getValue().
                                        getCvAccession().
                                        toUpperCase(), CvMappingUtils.
                                            asParameter(
                                                cvTerm));
                                    foundParameters.put(pair.getValue().
                                        getCvAccession().
                                        toUpperCase(), pair);
                                    break;
                                case IDENTICAL:
                                    if (cvTerm.isUseTerm()) {
                                        log.debug(pair.getValue().
                                            getCvAccession() + " is identical to " + cvTerm.
                                                getTermAccession());
                                        //use key of found parameter, since both are identical
                                        allowedParameters.put(pair.getValue().
                                            getCvAccession().
                                            toUpperCase(), CvMappingUtils.
                                                asParameter(cvTerm));
                                        foundParameters.put(pair.getValue().
                                            getCvAccession().
                                            toUpperCase(), pair);
                                    }
                                    break;
                                case NOT_RELATED:
                                    log.debug(pair.getValue().
                                        getCvAccession() + " is not related to " + cvTerm.
                                            getTermAccession());
//                                    //add term from rule as is
//                                    allowedParameters.put(cvTerm.
//                                        getTermAccession().
//                                        toUpperCase(), Parameters.asParameter(
//                                            cvTerm));
                                    //add found parameter as is
                                    foundParameters.put(pair.getValue().
                                        getCvAccession().
                                        toUpperCase(), pair);
                                    break;
                            }
                        } catch (org.springframework.web.client.HttpClientErrorException ex) {
                            throw new IllegalArgumentException(
                                "Could not retrieve parents for cv with label '" + pair.
                                    getValue().
                                    getCvLabel() + "' and term accession '" + pair.
                                    getValue().
                                    getCvAccession() + "'! Please check, whether the cv label and term accession is correct!",
                                ex);
                        }
                    } else if (cvTerm.isUseTermName()) {
                        throw new IllegalArgumentException(
                            "isUseTermName on cvTerm is not supported!");
                    } else {
                        if (cvTerm.getTermAccession().
                            toUpperCase().
                            equals(pair.getValue().
                                getCvAccession().
                                toUpperCase())) {
                            //TODO repeatable terms
                            allowedParameters.put(pair.getValue().
                                getCvAccession().
                                toUpperCase(), CvMappingUtils.
                                    asParameter(cvTerm));
                            foundParameters.put(pair.getValue().
                                getCvAccession().
                                toUpperCase(), pair);
                        }
                    }
                }
            });
        return new RuleEvaluationResult(rule, filteredSelection,
            allowedParameters, foundParameters);
    }

}