package fedata.general;

import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gba.general.GBAFECharacterProvider;
import fedata.gba.general.GBAFEClassProvider;
import fedata.gba.general.GBAFEItemProvider;
import fedata.gba.general.GBAFETextProvider;

public class FEBase {

    public enum GameType {
        UNKNOWN, FE4, FE6, FE7, FE8, FE9;

        public boolean isGBA() {
            switch (this) {
                case FE6:
                case FE7:
                case FE8:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isSFC() {
            return this == FE4;
        }

        public boolean isGCN() {
            return this == FE9;
        }

        public boolean hasSTRMAGSplit() {
            switch (this) {
                case FE4:
                case FE9:
                    return true;
                default:
                    return false;
            }
        }

        public boolean hasEnglishPatch() {
            switch (this) {
                case FE4:
                case FE6:
                    return true;
                default:
                    return false;
            }
        }
        
        public GBAFETextProvider textProvider() {
        	switch (this) {
        	case FE6:
        		return FE6Data.textProvider;
        	case FE7:
        		return FE7Data.textProvider;
        	case FE8:
        		return FE8Data.textProvider;
        	default:
        		return null;
        	}
        }
        
        public GBAFEClassProvider classProvider() {
        	switch (this) {
        	case FE6:
        		return FE6Data.classProvider;
        	case FE7:
        		return FE7Data.classProvider;
        	case FE8:
        		return FE8Data.classProvider;
        	default:
        		return null;
        	}
        }
        
        public GBAFECharacterProvider charProvider() {
        	switch(this) {
        	case FE6:
        		return FE6Data.characterProvider;
        	case FE7:
        		return FE7Data.characterProvider;
        	case FE8:
        		return FE8Data.characterProvider;
        	default:
        		return null;
        	}
        }
        
        public GBAFEItemProvider itemProvider() {
        	switch (this) {
        	case FE6:
        		return FE6Data.itemProvider;
        	case FE7:
        		return FE7Data.itemProvider;
        	case FE8:
        		return FE8Data.itemProvider;
        	default:
        		return null;
        	}
        }

        public String[] getFileExtensions() {
            switch (this) {
                case FE4:
                    return new String[]{"*.smc"};
                case FE9:
                    return new String[]{"*.iso"};
                case FE6:
                case FE7:
                case FE8:
                    return new String[]{"*.gba"};
                default:
                    throw new UnsupportedOperationException("unkown game type " + this.name());
            }
        }
    }
}
