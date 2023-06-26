package fedata.gba.fe8;

import fedata.gba.AbstractGBAData;
import io.FileHandler;

public class PromotionBranch extends AbstractGBAData {
    public PromotionBranch() {
        originalData = new byte[2];
        data = new byte[2];
    }

    public PromotionBranch(byte promo1, byte promo2) {
        originalData = new byte[] { promo1, promo2 };
        data = originalData.clone();
    }

    public PromotionBranch(FileHandler handler, long offset) {
        originalData = handler.readBytesAtOffset(offset, FE8Data.BytesPerPromotionBranchEntry);
        data = originalData.clone();

        originalOffset = offset;
    }

    public int getFirstPromotion() {
        return data[0] & 0xFF;
    }

    public void setFirstPromotion(int classID) {
        data[0] = (byte) (classID & 0xFF);
        wasModified = true;
    }

    public int getSecondPromotion() {
        return data[1] & 0xFF;
    }

    public void setSecondPromotion(int classID) {
        data[1] = (byte) (classID & 0xFF);
        wasModified = true;
    }
}