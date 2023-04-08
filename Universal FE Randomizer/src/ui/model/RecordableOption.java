package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

/**
 * Interface for all Option Classes which are able to be recorded in the
 * Changelog.
 */
public interface RecordableOption {

	/**
	 * This method will be called by the randomizer to handover the responsibility
	 * of recording the selected options to this RecordableOption.
	 */
	public void record(RecordKeeper rk, GameType type);
}
