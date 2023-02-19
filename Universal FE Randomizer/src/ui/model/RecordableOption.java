package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public interface RecordableOption {
	public void record(RecordKeeper rk, GameType type);
}
