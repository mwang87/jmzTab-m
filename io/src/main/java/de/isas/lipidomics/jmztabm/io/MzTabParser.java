package de.isas.lipidomics.jmztabm.io;

import de.isas.lipidomics.jmztabm.model.MzTabFile;
import java.io.InputStream;

/**
 * Parses the mzTab file structurally, populating a generic
 * model of the file.
 * 
 * @author Nils Hoffmann <nils.hoffmann@isas.de>
 */
public interface MzTabParser {
    public MzTabParseResult parse(InputStream i);
}