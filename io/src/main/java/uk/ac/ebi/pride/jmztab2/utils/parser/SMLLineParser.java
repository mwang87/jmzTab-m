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
package uk.ac.ebi.pride.jmztab2.utils.parser;

import uk.ac.ebi.pride.jmztab2.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab2.model.ISmallMoleculeColumn;
import uk.ac.ebi.pride.jmztab2.model.MZTabConstants;
import uk.ac.ebi.pride.jmztab2.model.MZBoolean;
import uk.ac.ebi.pride.jmztab2.model.IMZTabColumn;
import uk.ac.ebi.pride.jmztab2.model.OptionColumn;
import uk.ac.ebi.pride.jmztab2.model.SmallMoleculeColumn;
import uk.ac.ebi.pride.jmztab2.model.AbundanceColumn;
import de.isas.mztab2.model.Metadata;
import de.isas.mztab2.model.OptColumnMapping;
import de.isas.mztab2.model.SmallMoleculeSummary;
import uk.ac.ebi.pride.jmztab2.utils.errors.MZTabErrorList;


/**
 * <p>SMLLineParser class.</p>
 *
 * @author qingwei
 * @author nilshoffmann
 * @since 10/02/13
 * 
 */
public class SMLLineParser extends MZTabDataLineParser<SmallMoleculeSummary> {

    private SmallMoleculeSummary smallMoleculeSummary;

    /**
     * <p>Constructor for SMLLineParser.</p>
     *
     * @param context a {@link uk.ac.ebi.pride.jmztab2.utils.parser.MZTabParserContext} object.
     * @param factory a {@link uk.ac.ebi.pride.jmztab2.model.MZTabColumnFactory} object.
     * @param positionMapping a {@link uk.ac.ebi.pride.jmztab2.utils.parser.PositionMapping} object.
     * @param metadata a {@link de.isas.mztab2.model.Metadata} object.
     * @param errorList a {@link uk.ac.ebi.pride.jmztab2.utils.errors.MZTabErrorList} object.
     */
    public SMLLineParser(MZTabParserContext context, MZTabColumnFactory factory, PositionMapping positionMapping,
                         Metadata metadata, MZTabErrorList errorList) {
        super(context, factory, positionMapping, metadata, errorList);
    }

    /** {@inheritDoc} */
    @Override
    protected int checkData() {

        IMZTabColumn column;
        String columnName;
        String target;
        int physicalPosition;
        String logicalPosition;
        smallMoleculeSummary = new SmallMoleculeSummary();

        for (physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {
            logicalPosition = positionMapping.get(physicalPosition);
            column = factory.getColumnMapping().get(logicalPosition);

            if (column != null) {
                columnName = column.getName();
                target = items[physicalPosition];
                if (column instanceof ISmallMoleculeColumn) {
                    SmallMoleculeColumn.Stable stableColumn = SmallMoleculeColumn.Stable.forName(columnName);
                    switch(stableColumn) {
                        case ADDUCT_IONS:
                            smallMoleculeSummary.adductIons(checkStringList(column, target, MZTabConstants.BAR));
                            break;
                        case BEST_ID_CONFIDENCE_MEASURE:
                            smallMoleculeSummary.bestIdConfidenceMeasure(checkParameter(column, target, true));
                            break;
                        case BEST_ID_CONFIDENCE_VALUE:
                            smallMoleculeSummary.bestIdConfidenceValue(checkDouble(column, target));
                            break;
                        case CHEMICAL_FORMULA:
                            smallMoleculeSummary.chemicalFormula(checkStringList(column, target, MZTabConstants.BAR));
                            break;
                        case CHEMICAL_NAME:
                            smallMoleculeSummary.chemicalName(checkStringList(column, target, MZTabConstants.BAR));
                            break;
                        case DATABASE_IDENTIFIER:
                            smallMoleculeSummary.databaseIdentifier(checkStringList(column, target, MZTabConstants.BAR));
                            break;
                        case INCHI:
                            smallMoleculeSummary.inchi(checkStringList(column, target, MZTabConstants.BAR));
                            break;
                        case RELIABILITY:
                            smallMoleculeSummary.reliability(checkString(column, target));
                            break;
                        case SMF_ID_REFS:
                            smallMoleculeSummary.smfIdRefs(checkIntegerList(column, target, MZTabConstants.BAR));
                            break;
                        case SMILES:
                            smallMoleculeSummary.smiles(checkSmiles(column, target));
                            break;
                        case SML_ID:
                            smallMoleculeSummary.smlId(checkInteger(column, target));
                            break;
                        case THEOR_NEUTRAL_MASS:
                            smallMoleculeSummary.theoreticalNeutralMass(
                                checkDoubleList(column, target));
                            break;
                        case URI:
                            smallMoleculeSummary.uri(
                                checkStringList(column, target, MZTabConstants.BAR));
                            break;
                            
                    }

                } else if (column instanceof AbundanceColumn) {
                    if (columnName.startsWith(SmallMoleculeSummary.Properties.abundanceAssay.getPropertyName())) {
                        smallMoleculeSummary.addAbundanceAssayItem(checkDouble(column, target));
                    }else if(columnName.startsWith(SmallMoleculeSummary.Properties.abundanceStudyVariable.getPropertyName())) {
                        smallMoleculeSummary.addAbundanceStudyVariableItem(checkDouble(column, target));
                    }else if(columnName.startsWith(SmallMoleculeSummary.Properties.abundanceVariationStudyVariable.getPropertyName())) {
                        smallMoleculeSummary.addAbundanceVariationStudyVariableItem(checkDouble(column, target));
                    }
                } else if (column instanceof OptionColumn) {
                    if (columnName.startsWith(MZTabConstants.OPT_PREFIX)) {
                        Class dataType = column.getDataType();
                        OptColumnMapping optColMapping = new OptColumnMapping();
                        optColMapping.identifier(columnName.substring(MZTabConstants.OPT_PREFIX.length()));
                        if (dataType.equals(String.class)) {
                            optColMapping.value(checkString(column, target));
                        } else if (dataType.equals(Double.class)) {
                            optColMapping.value(Double.toString(checkDouble(column, target)));
                        } else if (dataType.equals(MZBoolean.class)) {
                            optColMapping.value(Boolean.toString(checkMZBoolean(column, target).toBoolean()));
                        }
                        smallMoleculeSummary.addOptItem(optColMapping);
                    }
                }
            }
        }

        return physicalPosition;
    }

    /**
     * <p>getRecord.</p>
     *
     * @return a {@link de.isas.mztab2.model.SmallMoleculeSummary} object.
     */
    @Override
    public SmallMoleculeSummary getRecord() {

        if(smallMoleculeSummary == null){
            smallMoleculeSummary = new SmallMoleculeSummary();
        }
        return smallMoleculeSummary;
    }
}