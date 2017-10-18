/*
 * 
 */
package de.isas.mztab.api;

import de.isas.mztab1_1.model.Assay;
import de.isas.mztab1_1.model.Contact;
import de.isas.mztab1_1.model.Metadata;
import de.isas.mztab1_1.model.MsRun;
import de.isas.mztab1_1.model.MzTab;
import de.isas.mztab1_1.model.Parameter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nils Hoffmann <nils.hoffmann@isas.de>
 */
public class MzTabTest {
    /*
    MTD	mzTab-version	1.1.0
MTD	mzTab-ID	JetBike Test
MTD	ms_run[1]	3injections_inj1_POS
MTD	ms_run[1]-location	D:\Data Sets\Metabolomics\MTBLS263\3injections_inj1_POS.mzML
MTD	ms_run[2]	3injections_inj2_POS
MTD	ms_run[2]-location	D:\Data Sets\Metabolomics\MTBLS263\3injections_inj2_POS.mzML
MTD	ms_run[3]	3injections_inj3_POS
MTD	ms_run[3]-location	D:\Data Sets\Metabolomics\MTBLS263\3injections_inj3_POS.mzML
MTD	ms_run[4]	3samples_sampl1_POS
MTD	ms_run[4]-location	D:\Data Sets\Metabolomics\MTBLS263\3samples_sampl1_POS.mzML
MTD	ms_run[5]	3samples_sampl2_POS
MTD	ms_run[5]-location	D:\Data Sets\Metabolomics\MTBLS263\3samples_sampl2_POS.mzML
MTD	ms_run[6]	3samples_sampl3_POS
MTD	ms_run[6]-location	D:\Data Sets\Metabolomics\MTBLS263\3samples_sampl3_POS.mzML
MTD	assay[1]-ms_run_ref	ms_run[1]
MTD	assay[2]-ms_run_ref	ms_run[2]
MTD	assay[3]-ms_run_ref	ms_run[3]
MTD	assay[4]-ms_run_ref	ms_run[4]
MTD	assay[5]-ms_run_ref	ms_run[5]
MTD	assay[6]-ms_run_ref	ms_run[6]
MTD	software[1]	[,,Progenesis QI,2.4.6479.46580]
MTD	study_variable[1]	Replicates
MTD	study_variable[1]-description	Replicates
MTD	study_variable[1]-assay_refs	assay[1] | assay[2] | assay[3]
MTD	study_variable[2]	Samples
MTD	study_variable[2]-description	Samples
MTD	study_variable[2]-assay_refs	assay[4] | assay[5] | assay[6]
MTD	cv[1]-label	MS
MTD	cv[1]-full_name	PSI-MS controlled vocabulary
MTD	cv[1]-version	4.0.9
MTD	cv[1]-url	https://raw.githubusercontent.com/HUPO-PSI/psi-ms-CV/master/psi-ms.obo
MTD	database[1]	[,,No database,]
MTD	database[1]-prefix	nd
MTD	database[1]-version	Unknown
MTD	database[2]	[,,D:\Databases\HMDB\hmdb+analgesic.sdf,]
MTD	database[2]-prefix	hmdb
MTD	database[2]-version	Unknown
MTD	small_molecule-quantification_unit	[,,Progenesis QI Normalised Abundance,]
MTD	small_molecule_feature-quantification_unit	[,,Progenesis QI Normalised Abundance,]
MTD	id_confidence_measure[1]	[,,Progenesis MetaScope Score,]
MTD	id_confidence_measure[2]	[,,Fragmentation Score,]
MTD	id_confidence_measure[3]	[,,Isotopic fit Score,]
    */
    @Test
    public void testMzTabObjectCreation() {
        final MzTab mztabfile = new MzTab().metadata(
            new Metadata().
                mzTabVersion("1.1.0").
                mzTabID("ISAS_2017_M_11451").
                title("A minimal test file").
                description("A description of an mzTab file.").
                addContactsItem(
                    new Contact().
                        name("Nils Hoffmann").
                        email("nils.hoffmann_at_isas.de").
                        affiliation("ISAS e.V. Dortmund, Germany")
                ).addMsrunItem(
                    new MsRun().
                        location("file:///path/to/file1.mzML").
                        format(
                            new Parameter().
                                cvLabel("MS").
                                cvAccession("MS:1000584").
                                name("mzML file")
                        ).
                        idFormat(
                            new Parameter().
                                cvLabel("MS").
                                cvAccession("MS:1001530").
                                name("mzML unique identifier")
                        )
                )
        );
        mztabfile.getMetadata().addAssayItem(
            new Assay().
                name("assay1").
                addMsRunRefItem(mztabfile.getMetadata().getMsrun().get(0))
        );
        
        System.out.println(mztabfile);
    }
}